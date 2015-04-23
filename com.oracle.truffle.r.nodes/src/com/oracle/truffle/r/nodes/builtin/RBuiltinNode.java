/*
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.r.nodes.builtin;

import java.util.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.r.nodes.access.*;
import com.oracle.truffle.r.nodes.function.*;
import com.oracle.truffle.r.nodes.function.RCallNode.LeafCallNode;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.RDeparse.State;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.env.frame.*;

@NodeFields(value = {@NodeField(name = "builtin", type = RBuiltinFactory.class), @NodeField(name = "suppliedSignature", type = ArgumentsSignature.class)})
@NodeChild(value = "arguments", type = RNode[].class)
@GenerateNodeFactory
/*
 * TODO Remove this as only about 20 builtins truly need a Factory (because they are used/created by
 * other builtins). However doing so greatly complicates the builtin loading/initialization/creation
 * process, as the class naming changes considerably. N.B. Those subclasses that need a Factory are
 * explicitly annotated with @GenerateNodeFactory.
 */
public abstract class RBuiltinNode extends LeafCallNode implements VisibilityController {

    private ArgumentsSignature signature;

    public String getSourceCode() {
        throw RInternalError.shouldNotReachHere();
    }

    /**
     * @return This is the accessor to the 'suppliedArgsNames': The names that have been given to
     *         the arguments supplied to the current function call. These are in the order as they
     *         appear in the source, of course.
     */
    public abstract ArgumentsSignature getSuppliedSignature();

    /**
     * Implementation is generated by Truffle in the "Factory" class. Note that some builtins, e.g.
     * subclasses of {@link RWrapperBuiltinNode} do not have a Factory class, so this will return
     * {@code null}.
     */
    public abstract RBuiltinFactory getBuiltin();

    /**
     * Accessor to the Truffle-generated 'arguments' field, used by binary operators and such.<br/>
     * <strong>ATTENTION:</strong> For implementing default values, use
     * {@link #getDefaultParameterValues()}!!!
     *
     * @return The arguments this builtin has received
     */
    public abstract RNode[] getArguments();

    /**
     * Return the names of the builtin's formal arguments. Historically this was always manually
     * overridden by the subclass. Now the information is acquired from the {@link RBuiltin}
     * annotation. If that cannot be determined (because {@link #getBuiltin()} returns {@code null}
     * the result is null, which allows the (sole) caller in this class to handle it.
     */
    private String[] getParameterNames() {
        RBuiltin builtin = getRBuiltin();
        return builtin == null ? null : builtin.parameterNames();
    }

    private ArgumentsSignature getParameterSignature() {
        if (signature == null) {
            String[] names = getParameterNames();
            if (names == null) {
                signature = ArgumentsSignature.empty(getArguments().length);
            } else {
                names = names.clone();
                for (int i = 0; i < names.length; i++) {
                    names[i] = names[i].isEmpty() ? null : names[i];
                }
                signature = ArgumentsSignature.get(names);
            }
        }
        return signature;
    }

    /**
     * Return the default values of the builtin's formal arguments. This is only valid for builtins
     * of {@link RBuiltinKind kind} PRIMITIVE or SUBSTITUTE. Only simple scalar constants and
     * {@link RMissing#instance}, {@link RNull#instance} and {@link RArgsValuesAndNames#EMPTY} are
     * allowed.
     */
    public Object[] getDefaultParameterValues() {
        return EMPTY_OBJECT_ARRAY;
    }

    @Override
    public final Object execute(VirtualFrame frame, RFunction function) {
        return execute(frame);
    }

    @Override
    public final int executeInteger(VirtualFrame frame, RFunction function) throws UnexpectedResultException {
        return executeInteger(frame);
    }

    @Override
    public final double executeDouble(VirtualFrame frame, RFunction function) throws UnexpectedResultException {
        return executeDouble(frame);
    }

    private static RNode[] createAccessArgumentsNodes(RBuiltinFactory builtin) {
        int total = builtin.getRBuiltin().parameterNames().length;
        RNode[] args = new RNode[total];
        for (int i = 0; i < total; i++) {
            args[i] = AccessArgumentNode.create(i);
        }
        return args;
    }

    static RootCallTarget createArgumentsCallTarget(RBuiltinFactory builtin) {
        CompilerAsserts.neverPartOfCompilation();

        // Create function initialization
        RNode[] argAccessNodes = createAccessArgumentsNodes(builtin);
        RBuiltinNode node = createNode(builtin, argAccessNodes.clone(), ArgumentsSignature.empty(argAccessNodes.length));

        assert builtin.getRBuiltin() == null || builtin.getRBuiltin().kind() != RBuiltinKind.INTERNAL || node.getDefaultParameterValues().length == 0 : "INTERNAL builtins do not need default values";
        FormalArguments formals = FormalArguments.createForBuiltin(node.getDefaultParameterValues(), node.getParameterSignature());
        for (RNode access : argAccessNodes) {
            ((AccessArgumentNode) access).setFormals(formals);
        }

        // Setup
        FrameDescriptor frameDescriptor = new FrameDescriptor();
        RBuiltinRootNode root = new RBuiltinRootNode(node, formals, frameDescriptor);
        FrameSlotChangeMonitor.initializeFunctionFrameDescriptor(frameDescriptor);
        return Truffle.getRuntime().createCallTarget(root);
    }

    public RCallNode inline(InlinedArguments args) {
        // static number of arguments
        RNode[] builtinArguments = inlineStaticArguments(args);
        RBuiltinNode builtin = createNode(getBuiltin(), builtinArguments, args.getSignature());
        return builtin;
    }

    protected RBuiltin getRBuiltin() {
        return getRBuiltin(getClass());
    }

    private static RBuiltin getRBuiltin(Class<?> klass) {
        GeneratedBy generatedBy = klass.getAnnotation(GeneratedBy.class);
        if (generatedBy != null) {
            return generatedBy.value().getAnnotation(RBuiltin.class);
        } else {
            return null;
        }
    }

    private static RBuiltinNode createNode(RBuiltinFactory factory, RNode[] builtinArguments, ArgumentsSignature signature) {
        Object[] args = new Object[3 + factory.getConstantArguments().length];
        int index = 0;
        for (; index < factory.getConstantArguments().length; index++) {
            args[index] = factory.getConstantArguments()[index];
        }

        args[index++] = builtinArguments;
        args[index++] = factory;
        args[index] = signature;

        assert signature != null : factory + " " + Arrays.toString(builtinArguments);
        return factory.getFactory().createNode(args);
    }

    protected RNode[] inlineStaticArguments(InlinedArguments args) {
        int execSignatureSize = getBuiltin().getFactory().getExecutionSignature().size();
        int parameterLength = getBuiltin().getRBuiltin().parameterNames().length;
        if (execSignatureSize > parameterLength) {
            throwMissingFormalParameterError(parameterLength, execSignatureSize);
        }

        return args.getInlinedArgs();
    }

    @TruffleBoundary
    private void throwMissingFormalParameterError(int argsLength, int specializationExpectesArgs) {
        String name = getBuiltin().getRBuiltin().name();
        RInternalError.shouldNotReachHere("Builtin '" + name + "': Length of 'parameterNames' (" + argsLength + ") and specialization signature (" + specializationExpectesArgs +
                        ") must be consistent!");
    }

    /*
     * The following two overrides are only needed when a {@code .Internal} call has been rewritten
     * to replace itself with the {@link RBuiltinNode}. It may be better to create an AST structure
     * that is more similar to the normal case.
     */

    @Override
    public RNode getFunctionNode() {
        return this;
    }

    @Override
    public void deparse(State state) {
        RBuiltin rb = getBuiltin().getRBuiltin();
        assert rb.kind() == RBuiltinKind.INTERNAL;
        state.append(".Internal(");
        state.append(rb.name());
        // arguments; there is no CallArgumentsNode, so we create one to reuse the deparse code
        CallArgumentsNode.createUnnamed(false, false, getArguments()).deparse(state);
        state.append(')');
    }

    @Override
    public String toString() {
        return (getRBuiltin() == null ? getClass().getSimpleName() : getRBuiltin().name());
    }

    /**
     * A wrapper builtin is a {@link RCustomBuiltinNode} that is able to create any arbitrary node
     * as builtin (e.g., 'max', 'sum', etc.). It can be used as normal builtin. Implement
     * {@link #createDelegate()} to create that node.
     */
    public abstract static class RWrapperBuiltinNode extends RCustomBuiltinNode {

        @Child private RNode delegate;

        public RWrapperBuiltinNode(RBuiltinNode prev) {
            super(prev);
        }

        protected abstract RNode createDelegate();

        private RNode getDelegate() {
            if (delegate == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                delegate = insert(createDelegate());
            }
            return delegate;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            return getDelegate().execute(frame);
        }

        @Override
        public Object[] executeArray(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeArray(frame);
        }

        @Override
        public byte executeByte(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeByte(frame);
        }

        @Override
        public double executeDouble(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeDouble(frame);
        }

        @Override
        public RFunction executeFunction(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeFunction(frame);
        }

        @Override
        public int executeInteger(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeInteger(frame);
        }

        @Override
        public RNull executeNull(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeNull(frame);
        }

        @Override
        public RDoubleVector executeRDoubleVector(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeRDoubleVector(frame);
        }

        @Override
        public RIntVector executeRIntVector(VirtualFrame frame) throws UnexpectedResultException {
            return getDelegate().executeRIntVector(frame);
        }
    }

    public static class RCustomBuiltinNode extends RBuiltinNode {

        @Children protected final RNode[] arguments;

        private final RBuiltinFactory builtin;
        private final ArgumentsSignature suppliedSignature;

        public RCustomBuiltinNode(RBuiltinNode prev) {
            this(prev.getArguments(), prev.getBuiltin(), prev.getSuppliedSignature());
        }

        public RCustomBuiltinNode(RNode[] arguments, RBuiltinFactory builtin, ArgumentsSignature suppliedSignature) {
            this.arguments = arguments;
            this.builtin = builtin;
            this.suppliedSignature = suppliedSignature;
        }

        @Override
        public RNode[] getArguments() {
            return arguments;
        }

        @Override
        public RBuiltinFactory getBuiltin() {
            return builtin;
        }

        @Override
        protected RBuiltin getRBuiltin() {
            return builtin.getRBuiltin();
        }

        @Override
        public ArgumentsSignature getSuppliedSignature() {
            return suppliedSignature;
        }

    }
}

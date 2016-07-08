/*
 * Copyright (c) 2016, 2016, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.library.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.Function;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.frame.FrameInstance.FrameAccess;
import com.oracle.truffle.api.instrumentation.EventContext;
import com.oracle.truffle.api.instrumentation.ExecutionEventListener;
import com.oracle.truffle.api.instrumentation.SourceSectionFilter;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.r.nodes.builtin.RExternalBuiltinNode;
import com.oracle.truffle.r.nodes.function.FunctionDefinitionNode;
import com.oracle.truffle.r.nodes.instrumentation.RInstrumentation;
import com.oracle.truffle.r.runtime.RArguments;
import com.oracle.truffle.r.runtime.RCaller;
import com.oracle.truffle.r.runtime.RError;
import com.oracle.truffle.r.runtime.RRuntime;
import com.oracle.truffle.r.runtime.Utils;
import com.oracle.truffle.r.runtime.data.RNull;
import com.oracle.truffle.r.runtime.data.model.RAbstractStringVector;

public abstract class Rprof extends RExternalBuiltinNode.Arg8 {

    private PrintWriter out;
    private ProfileThread profileThread;
    private StatementListener statementListener;
    private long intervalInMillis;

    @SuppressWarnings("unused")
    @Specialization
    public Object do_Rprof(RAbstractStringVector filenameVec, byte appendL, double intervalD, byte memProfilingL,
                    byte gcProfilingL, byte lineProfilingL, int numFiles, int bufSize) {
        String filename = filenameVec.getDataAt(0);
        if (filename.length() == 0) {
            // disable
            endProfiling();
        } else {
            // enable
            if (out != null) {
                endProfiling();
            }
            boolean append = RRuntime.fromLogical(appendL);
            boolean memProfiling = RRuntime.fromLogical(memProfilingL);
            boolean gcProfiling = RRuntime.fromLogical(gcProfilingL);
            try {
                out = new PrintWriter(new FileWriter(filename, append));
                if (memProfiling) {
                    RError.warning(this, RError.Message.GENERIC, "Rprof: memory profiling not supported");
                }
                if (gcProfiling) {
                    RError.warning(this, RError.Message.GENERIC, "Rprof: gc profiling not supported");
                }
                // interval is in seconds, we convert to millis
                intervalInMillis = (long) (1E3 * intervalD);
                statementListener = new StatementListener();
                profileThread = new ProfileThread(intervalInMillis, statementListener);
                profileThread.setDaemon(true);
                profileThread.start();
            } catch (IOException ex) {
                throw RError.error(this, RError.Message.GENERIC, String.format("Rprof: cannot open profile file '%s'", filename));
            }
        }
        return RNull.instance;
    }

    private void endProfiling() {
        profileThread.running = false;
        out.printf("sample.interval=%d\n", intervalInMillis * 1000);
        for (ArrayList<RCaller> intervalStack : statementListener.intervalStacks) {
            boolean didOutput = false;
            for (RCaller caller : intervalStack) {
                while (caller.isPromise()) {
                    caller = caller.getParent();
                }
                RootNode rootNode = caller.getSyntaxNode().asRNode().getRootNode();
                if (rootNode instanceof FunctionDefinitionNode) {
                    String name = rootNode.getName();
                    out.printf("\"%s\" ", name);
                    didOutput = true;
                }
            }
            if (didOutput) {
                out.println();
            }
        }
        out.close();
    }

    private static class ProfileThread extends Thread {
        private final long interval;
        private final StatementListener statementListener;
        private volatile boolean running = true;

        private ProfileThread(long interval, StatementListener statementListener) {
            this.interval = interval;
            this.statementListener = statementListener;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(interval);
                    statementListener.intervalElapsed();
                } catch (InterruptedException ex) {

                }
            }
        }

    }

    /**
     * Emulates a sampling timer by checking when the sample interval rolls over and at that point
     * collects the stack of functions.
     */
    private static class StatementListener implements ExecutionEventListener {
        private ArrayList<ArrayList<RCaller>> intervalStacks = new ArrayList<>();
        private volatile boolean newInterval;

        private StatementListener() {
            SourceSectionFilter.Builder builder = SourceSectionFilter.newBuilder();
            builder.tagIs(StandardTags.StatementTag.class);
            SourceSectionFilter filter = builder.build();
            RInstrumentation.getInstrumenter().attachListener(filter, this);
        }

        private void intervalElapsed() {
            newInterval = true;
        }

        @Override
        public void onEnter(EventContext context, VirtualFrame frame) {
            if (newInterval) {
                final ArrayList<RCaller> callers = collectStack();
                intervalStacks.add(callers);
                newInterval = false;
            }
        }

        @TruffleBoundary
        private static ArrayList<RCaller> collectStack() {
            final ArrayList<RCaller> callers = new ArrayList<>();
            Utils.iterateRFrames(FrameAccess.READ_ONLY, new Function<Frame, Object>() {

                @Override
                public Object apply(Frame f) {
                    RCaller call = RArguments.getCall(f);
                    if (call != null && call.isValidCaller()) {
                        callers.add(RArguments.getCall(f));
                    }
                    return null;
                }

            });
            return callers;
        }

        @Override
        public void onReturnValue(EventContext context, VirtualFrame frame, Object result) {
        }

        @Override
        public void onReturnExceptional(EventContext context, VirtualFrame frame, Throwable exception) {
        }

    }
}

/*
 * Copyright (c) 2017, 2017, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.r.nodes.ffi;

import com.oracle.truffle.r.runtime.ffi.UpCallsRFFI;

/**
 * Generated from {@link UpCallsRFFI}. Any native code that is dependent on the ordinal value of
 * these enums must be kept in sync. The {@link #main} method will generate the appropriate C
 * #define statements.
 */
public enum RFFIUpCallMethod {
    CADDR("(object) : object"),
    CADR("(object) : object"),
    CAR("(object) : object"),
    CDDR("(object) : object"),
    CDR("(object) : object"),
    DUPLICATE_ATTRIB("(object, object) : void"),
    ENCLOS("(object) : object"),
    GetRNGstate("() : void"),
    INTEGER("(object) : object"),
    IS_S4_OBJECT("(object) : sint32"),
    SET_S4_OBJECT("(object) : void"),
    UNSET_S4_OBJECT("(object) : void"),
    LENGTH("(object) : sint32"),
    LOGICAL("(object) : object"),
    NAMED("(object) : sint32"),
    OBJECT("(object) : sint32"),
    PRCODE("(object) : object"),
    PRENV("(object) : object"),
    PRINTNAME("(object) : object"),
    PRSEEN("(object) : sint32"),
    PRVALUE("(object) : object"),
    PutRNGstate("() : void"),
    RAW("(object) : object"),
    RDEBUG("(object) : sint32"),
    REAL("(object) : object"),
    RSTEP("(object) : sint32"),
    R_BaseEnv("() : object"),
    R_BaseNamespace("() : object"),
    R_BindingIsLocked("(object, object) : sint32"),
    R_CHAR("(object) : object"),
    R_CleanUp("(sint32, sint32, sint32) : void"),
    R_ExternalPtrAddr("(object) : object"),
    R_ExternalPtrProtected("(object) : object"),
    R_ExternalPtrTag("(object) : object"),
    R_FindNamespace("(object) : object"),
    R_GetConnection("(sint32) : object"),
    R_GlobalContext("() : object"),
    R_GlobalEnv("() : object"),
    R_HomeDir("() : object"),
    R_Interactive("() : sint32"),
    R_MakeExternalPtr("(object, object, object) : object"),
    R_MethodsNamespace("() : object"),
    R_NamespaceRegistry("() : object"),
    R_NewHashedEnv("(object, object) : object"),
    R_ParseVector("(object, sint32, object) : object"),
    R_PromiseExpr("(object) : object"),
    R_ReadConnection("(sint32, object) : sint32"),
    R_SetExternalPtrAddr("(object, object) : void"),
    R_SetExternalPtrProtected("(object, object) : void"),
    R_SetExternalPtrTag("(object, object) : void"),
    R_ToplevelExec("() : object"),
    R_WriteConnection("(sint32, object) : sint32"),
    R_compute_identical("(object, object, sint32) : sint32"),
    R_do_MAKE_CLASS("(string) : object"),
    R_do_new_object("(object) : object"),
    R_do_slot("(object, object) : object"),
    R_do_slot_assign("(object, object, object) : object"),
    R_getContextCall("(object) : object"),
    R_getContextEnv("(object) : object"),
    R_getContextFun("(object) : object"),
    R_getContextSrcRef("(object) : object"),
    R_getGlobalFunctionContext("() : object"),
    R_getParentFunctionContext("(object) : object"),
    R_insideBrowser("() : sint32"),
    R_isEqual("(object, object) : sint32"),
    R_isGlobal("(object) : sint32"),
    R_lsInternal3("(object, sint32, sint32) : object"),
    R_new_custom_connection("(string, string, string, object) : object"),
    R_tryEval("(object, object, object) : object"),
    Rf_GetOption1("(object) : object"),
    Rf_PairToVectorList("(object) : object"),
    Rf_ScalarDouble("(double) : object"),
    Rf_ScalarInteger("(sint32) : object"),
    Rf_ScalarLogical("(sint32) : object"),
    Rf_ScalarString("(object) : object"),
    Rf_allocArray("(sint32, object) : object"),
    Rf_allocMatrix("(sint32, sint32, sint32) : object"),
    Rf_allocVector("(sint32, sint32) : object"),
    Rf_any_duplicated("(object, sint32) : sint32"),
    Rf_asChar("(object) : object"),
    Rf_asInteger("(object) : sint32"),
    Rf_asLogical("(object) : sint32"),
    Rf_asReal("(object) : double"),
    Rf_classgets("(object, object) : object"),
    Rf_coerceVector("(object, sint32) : object"),
    Rf_cons("(object, object) : object"),
    Rf_copyListMatrix("(object, object, sint32) : void"),
    Rf_copyMatrix("(object, object, sint32) : void"),
    Rf_defineVar("(object, object, object) : void"),
    Rf_duplicate("(object, sint32) : object"),
    Rf_error("(string) : void"),
    Rf_eval("(object, object) : object"),
    Rf_findVar("(object, object) : object"),
    Rf_findVarInFrame("(object, object) : object"),
    Rf_findVarInFrame3("(object, object, sint32) : object"),
    Rf_findFun("(object, object) : object"),
    Rf_getAttrib("(object, object) : object"),
    Rf_gsetVar("(object, object, object) : void"),
    Rf_inherits("(string, object) : sint32"),
    Rf_install("(string) : object"),
    Rf_installChar("(object) : object"),
    Rf_isNull("(object) : sint32"),
    Rf_isString("(object) : sint32"),
    Rf_lengthgets("(object, sint32) : object"),
    Rf_mkCharLenCE("(pointer, sint32, sint32) : object"),
    Rf_ncols("(object) : sint32"),
    Rf_nrows("(object) : sint32"),
    Rf_setAttrib("(object, object, object) : void"),
    Rf_warning("(string) : void"),
    Rf_warningcall("(object, string) : void"),
    Rprintf("(string) : void"),
    SETCADR("(object, object) : object"),
    SETCAR("(object, object) : object"),
    SETCDR("(object, object) : object"),
    SET_RDEBUG("(object, sint32) : void"),
    SET_RSTEP("(object, sint32) : void"),
    SET_STRING_ELT("(object, sint32, object) : void"),
    SET_SYMVALUE("(object, object) : void"),
    SET_TAG("(object, object) : object"),
    SET_TYPEOF_FASTR("(object, sint32) : object"),
    SET_VECTOR_ELT("(object, sint32, object) : void"),
    STRING_ELT("(object, sint32) : object"),
    SYMVALUE("(object) : object"),
    TAG("(object) : object"),
    TYPEOF("(object) : sint32"),
    VECTOR_ELT("(object, sint32) : object"),
    getConnectionClassString("(object) : object"),
    getOpenModeString("(object) : object"),
    getSummaryDescription("(object) : object"),
    isSeekable("(object) : object"),
    unif_rand("() : double");

    /**
     * The signature used for the upcall in Truffle NFI.
     */
    public final String nfiSignature;

    RFFIUpCallMethod(String signature) {
        this.nfiSignature = signature;
    }

    public static void main(String[] args) {
        for (RFFIUpCallMethod f : RFFIUpCallMethod.values()) {
            System.out.printf("#define %s_x %d\n", f.name(), f.ordinal());
        }
        System.out.printf("\n#define CALLBACK_TABLE_SIZE %d\n", RFFIUpCallMethod.values().length);
    }

}

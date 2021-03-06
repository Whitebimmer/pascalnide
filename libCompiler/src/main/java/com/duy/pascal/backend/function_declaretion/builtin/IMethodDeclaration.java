/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.backend.function_declaretion.builtin;

import android.support.annotation.Nullable;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.ArgumentType;
import com.duy.pascal.backend.pascaltypes.DeclaredType;
import com.js.interpreter.expressioncontext.ExpressionContext;
import com.js.interpreter.runtime_value.FunctionCall;
import com.js.interpreter.runtime_value.RuntimeValue;


public interface IMethodDeclaration {
    /**
     * @return simple name of method;
     */
    String name();

    FunctionCall generateCall(LineInfo line, RuntimeValue[] values,
                              ExpressionContext f) throws ParsingException;

    FunctionCall generatePerfectFitCall(LineInfo line,
                                        RuntimeValue[] values, ExpressionContext f)
            throws ParsingException;

    ArgumentType[] argumentTypes();

    /**
     * return type of method
     */
    DeclaredType returnType();

    /**
     * short description of method, it can be null
     */
    @Nullable
    String description();
}

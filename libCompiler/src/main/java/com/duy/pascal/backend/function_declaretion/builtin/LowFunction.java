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


import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.pascaltypes.ArgumentType;
import com.duy.pascal.backend.pascaltypes.ArrayType;
import com.duy.pascal.backend.pascaltypes.BasicType;
import com.duy.pascal.backend.pascaltypes.DeclaredType;
import com.duy.pascal.backend.pascaltypes.RuntimeType;
import com.js.interpreter.expressioncontext.CompileTimeContext;
import com.js.interpreter.expressioncontext.ExpressionContext;
import com.js.interpreter.instructions.Executable;
import com.js.interpreter.runtime_value.FunctionCall;
import com.js.interpreter.runtime_value.RuntimeValue;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.codeunit.RuntimeExecutableCodeUnit;
import com.js.interpreter.runtime.exception.RuntimePascalException;

public class LowFunction implements IMethodDeclaration {

    private ArgumentType[] argumentTypes = {new RuntimeType(BasicType.create(Object.class), false)};

    @Override
    public String name() {
        return "low";
    }

    @Override
    public FunctionCall generateCall(LineInfo line, RuntimeValue[] arguments,
                                     ExpressionContext f) throws ParsingException {
        RuntimeValue object = arguments[0];
        RuntimeType type = object.getType(f);
        return new LowCall(type, line);
    }

    @Override
    public FunctionCall generatePerfectFitCall(LineInfo line, RuntimeValue[] values, ExpressionContext f) throws ParsingException {
        return generateCall(line, values, f);
    }

    @Override
    public ArgumentType[] argumentTypes() {
        return argumentTypes;
    }

    @Override
    public DeclaredType returnType() {
        return BasicType.Integer;
    }

    @Override
    public String description() {
        return null;
    }

    private class LowCall extends FunctionCall {

        private LineInfo line;
        private RuntimeType type;

        LowCall(RuntimeType type, LineInfo line) {
            this.type = type;
            this.line = line;
        }

        @Override
        public RuntimeType getType(ExpressionContext f) throws ParsingException {
            return new RuntimeType(BasicType.Integer, false);
        }

        @Override
        public LineInfo getLineNumber() {
            return line;
        }


        @Override
        public Object compileTimeValue(CompileTimeContext context) {
            return null;
        }

        @Override
        public RuntimeValue compileTimeExpressionFold(CompileTimeContext context)
                throws ParsingException {
            return new LowCall(type, line);
        }

        @Override
        public Executable compileTimeConstantTransform(CompileTimeContext c)
                throws ParsingException {
            return new LowCall(type, line);
        }

        @Override
        protected String getFunctionName() {
            return "low";
        }

        @Override
        public Object getValueImpl(VariableContext f, RuntimeExecutableCodeUnit<?> main)
                throws RuntimePascalException {
            DeclaredType declType = type.declType;
            if (declType instanceof ArrayType) {
                return ((ArrayType) declType).getBounds().lower;
            } else if (BasicType.Byte.equals(declType)) {
                return Byte.MIN_VALUE;
            } else if (BasicType.Short.equals(declType)) {
                return Short.MIN_VALUE;
            } else if (BasicType.Integer.equals(declType)) {
                return Integer.MIN_VALUE;
            } else if (BasicType.Long.equals(declType)) {
                return Long.MIN_VALUE;
            } else if (BasicType.Double.equals(declType)) {
                return Double.MIN_VALUE;
            } else if (BasicType.Character.equals(declType)) {
                return Character.MIN_VALUE;
            }
            return 0;
        }
    }
}

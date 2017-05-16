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

package com.duy.pascal.backend.exceptions.value;

import com.duy.pascal.backend.exceptions.ParsingException;
import com.js.interpreter.ast.returnsvalue.ReturnValue;

public class NonIntegerException extends ParsingException {

    public ReturnValue value;

    public NonIntegerException(ReturnValue value) {
        super(value.getLineNumber());
        this.value = value;
    }

    @Override
    public String getMessage() {
        return "Value must be integers: " + value;
    }
}
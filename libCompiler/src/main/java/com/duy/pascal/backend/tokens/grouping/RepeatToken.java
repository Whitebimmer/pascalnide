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

package com.duy.pascal.backend.tokens.grouping;

import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.tokens.Token;

public class RepeatToken extends GrouperToken {

    public RepeatToken(LineInfo line) {
        super(line);
    }

    @Override
    public String toCode() {
        StringBuilder result = new StringBuilder("repeat ");
        if (next != null) {
            result.append(next).append(' ');
        }
        for (Token t : this.queue) {
            result.append(t).append(' ');
        }
        result.append("end");
        return result.toString();
    }

    @Override
    public String toString() {
        return "repeat";
    }

    @Override
    protected String getClosingText() {
        return "until";
    }
}

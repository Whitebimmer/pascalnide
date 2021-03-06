package com.duy.pascal.backend.tokens.grouping;


import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.tokens.Token;

public class ClassToken extends GrouperToken {
    public ClassToken(LineInfo line) {
        super(line);
    }

    @Override
    public String toCode() {
        StringBuilder result = new StringBuilder("class ");
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
        return "class";
    }

    @Override
    protected String getClosingText() {
        return "end";
    }
}

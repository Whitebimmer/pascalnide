package com.duy.pascal.backend.tokens.value;


import com.duy.pascal.backend.linenumber.LineInfo;

public class LongToken extends ValueToken {
    public long value;

    public LongToken(LineInfo line, long i) {
        super(line);
        value = i;
    }

    @Override
    public String toCode() {
        return String.valueOf(value);
    }

    @Override
    public Object getValue() {
        return value;
    }
}

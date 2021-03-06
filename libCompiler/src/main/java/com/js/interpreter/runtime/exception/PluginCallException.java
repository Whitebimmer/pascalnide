package com.js.interpreter.runtime.exception;

import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.function_declaretion.AbstractFunction;

public class PluginCallException extends RuntimePascalException {
    public Throwable cause;
    public AbstractFunction function;

    public PluginCallException(LineInfo line, Throwable cause,
                               AbstractFunction function) {
        super(line);
        this.cause = cause;
        this.function = function;
    }

    @Override
    public String getMessage() {
        return "When calling Function or Procedure " + function.name()
                + ", The following java exception: \"" + cause + "\"\n" +
                "Message: " + (cause != null ? cause.getMessage() : "");
    }
}

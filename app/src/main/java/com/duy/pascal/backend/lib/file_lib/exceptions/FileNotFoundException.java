package com.duy.pascal.backend.lib.file_lib.exceptions;

import com.duy.pascal.backend.linenumber.LineInfo;
import com.js.interpreter.runtime.exception.RuntimePascalException;

/**
 * This class is throw file not open when call command "readf", "writef" without open file before
 * Created by Duy on 07-Apr-17.
 */
public class FileNotFoundException extends RuntimePascalException {
    public FileNotFoundException(LineInfo line) {
        super(line);
    }
    public String filePath;

    public FileNotFoundException(String filePath) {
        super(null);
        this.filePath = filePath;
    }

    public FileNotFoundException(LineInfo line, String mes) {
        super(line, mes);
    }
}

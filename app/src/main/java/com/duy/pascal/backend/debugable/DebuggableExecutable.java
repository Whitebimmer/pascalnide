package com.duy.pascal.backend.debugable;

import com.js.interpreter.ast.instructions.Executable;
import com.js.interpreter.ast.instructions.ExecutionResult;
import com.js.interpreter.runtime.FunctionOnStack;
import com.js.interpreter.runtime.VariableContext;
import com.js.interpreter.runtime.codeunit.RuntimeExecutable;
import com.js.interpreter.runtime.exception.RuntimePascalException;
import com.js.interpreter.runtime.exception.UnhandledPascalException;

import java.util.ArrayList;

/**
 * DEBUG class
 * this class can check stack size
 */
public abstract class DebuggableExecutable implements Executable {
    public boolean DEBUG = true;

    @Override
    public ExecutionResult execute(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException {
        try {
            if (main != null) {
                main.scriptControlCheck(getLineNumber());
            }

            if (DEBUG) {
                try {
                    printDebug(f, main);
                } catch (Exception ignored) {
                }
            }

            return executeImpl(f, main);
        } catch (RuntimePascalException e) {
            throw e;
        } catch (Exception e) {
            throw new UnhandledPascalException(this.getLineNumber(), e);
        }
    }

    /**
     * print list variable and value of this to console
     *
     * @param f
     * @param main
     */
    private void printDebug(VariableContext f, RuntimeExecutable<?> main) {
        if (main.getDebugListener() == null) return;
        DebugListener listener = main.getDebugListener();
        /**
         * get global variables, main program
         */
        listener.onNewMessage("List global variable: ");
        ArrayList<String> listNameVariables = main.getListNameGlobalVariables();
        for (String name : listNameVariables) {
            Object o = main.getGlobalVariable(name);
            if (o != null) {
                if (o instanceof Integer ||
                        o instanceof Long || o instanceof Double || o instanceof StringBuilder ||
                        o instanceof Character) {
                    listener.onNewMessage(name + " = " + String.valueOf(o));
                } else if (o.getClass().isArray()) {
                    //print array
                    Object[] array = (Object[]) o;
                    String res = name + " = " + arrayToString(array);
                    listener.onNewMessage(res);
                } else {
                    listener.onNewMessage(name + " : " + o.toString()
                            + " - " + o.getClass().getSimpleName());
                }
            }
        }
        /**
         * get local variable if f this class is function or procedure
         */
        listener.onNewMessage("List local variable: " + f.getClass().getSimpleName());
        if (f instanceof FunctionOnStack) {
            ArrayList<String> listNameLocalVariable = ((FunctionOnStack) f).getListNameLocalVariable();
            String nameFunction = "\t" + ((FunctionOnStack) f).getCurrentFunction().name;
            for (String name : listNameLocalVariable) {
                try {
                    Object o = f.getGlobalVariable(name);
                    if (o != null) {
                        if (o instanceof Integer || o instanceof Long || o instanceof Double ||
                                o instanceof StringBuilder || o instanceof Character) {
                            listener.onNewMessage(nameFunction + ": " + name + " = " + String.valueOf(o));
                        } else if (o.getClass().isArray()) {
                            //print array
                            Object[] array = (Object[]) o;
                            String res = nameFunction + ": " + name + " = " + arrayToString(array);
                            listener.onNewMessage(res);
                        } else {
                            listener.onNewMessage(nameFunction + ": " + name + " : " + o.toString()
                                    + " - " + o.getClass().getSimpleName());
                        }
                    }
                } catch (RuntimePascalException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

    private String arrayToString(Object[] array) {
        StringBuilder res = new StringBuilder();
        res.append("[");
        for (Object var : array) {
            if (var instanceof Object[]) {
                String tmp = arrayToString((Object[]) var);
                res.append(tmp);
            } else {
                res.append(var.toString()).append(", ");
            }
        }
        res.append("]");
        return res.toString();
    }

    public abstract ExecutionResult executeImpl(VariableContext f, RuntimeExecutable<?> main)
            throws RuntimePascalException;
}
package com.duy.pascal.backend.core;


import com.duy.pascal.backend.exceptions.ParsingException;
import com.duy.pascal.backend.lib.crt.CrtLib;
import com.duy.pascal.backend.lib.DosLib;
import com.duy.pascal.backend.lib.SystemLib;
import com.duy.pascal.backend.lib.file.FileLib;
import com.duy.pascal.backend.lib.graph.GraphLib;
import com.duy.pascal.backend.lib.io.IOLib;
import com.duy.pascal.backend.lib.io.InOutListener;
import com.duy.pascal.backend.lib.math.MathLib;
import com.duy.pascal.frontend.activities.ExecHandler;
import com.duy.pascal.frontend.activities.RunnableActivity;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.duy.pascal.backend.function_declaretion.AbstractFunction;
import com.duy.pascal.backend.function_declaretion.MethodDeclaration;
import com.js.interpreter.codeunit.library.UnitPascal;
import com.js.interpreter.codeunit.program.PascalProgram;
import com.js.interpreter.source_include.ScriptSource;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class PascalCompiler {
    public static final String TAG = PascalCompiler.class.getSimpleName();
    public static final boolean android = true;
    public static final boolean DEBUG = false;

    public RunnableActivity handler;

    private SystemLib systemLib = new SystemLib();
    private IOLib ioLib = new IOLib(null);
    private CrtLib crtLib = new CrtLib(null);
    private GraphLib graphLib = new GraphLib(null);
    private FileLib fileLib = new FileLib();

    public PascalCompiler(RunnableActivity handler) {
        this.handler = handler;
    }

    public static ListMultimap<String, AbstractFunction> loadFunctionTable(
            List<ScriptSource> includeSearchPath) throws ParsingException {
        return ArrayListMultimap.create();
    }

    /**
     * constructor
     *
     * @param sourcename - file name
     * @param in         - Input Reader
     * @param handler    - handler for variable and function, debug, input and output to screen, ....
     */
    public static PascalProgram loadPascal(String sourcename, Reader in,
                                           List<ScriptSource> includeSearchPath,
                                           RunnableActivity handler) throws ParsingException {
        ListMultimap<String, AbstractFunction> functiontable = loadFunctionTable(includeSearchPath);
        return new PascalProgram(in, functiontable, sourcename, includeSearchPath, handler);
    }


    public static UnitPascal loadLibrary(String sourcename, Reader in,
                                         List<ScriptSource> includeSearchPath,
                                         RunnableActivity handler) throws ParsingException {
        ListMultimap<String, AbstractFunction> functiontable = loadFunctionTable(includeSearchPath);
        return new UnitPascal(in, sourcename, functiontable, includeSearchPath, handler);
    }

    private void loadPluginsPascal(ListMultimap<String, AbstractFunction> functionTable) {

        ArrayList<Class> classes = new ArrayList<>();
        classes.add(MathLib.class);
        classes.add(systemLib.getClass());
        classes.add(crtLib.getClass());
        classes.add(DosLib.class);
        classes.add(graphLib.getClass());

        /*
          Important: load file library before io lib. Because method readln(file, ...)
          in {@link FileLib} will be override method readln(object...) in {@link IOLib}
         */
        classes.add(fileLib.getClass());
        classes.add(ioLib.getClass());
        for (Class t : classes) {
            Object o = null;
            try {
                Constructor constructor = t.getConstructor(InOutListener.class);
                o = constructor.newInstance(handler);
            } catch (Exception ignored) {
            }
            if (o == null) {
                try {
                    Constructor constructor;
                    constructor = t.getConstructor(ExecHandler.class);
                    o = constructor.newInstance(handler);
                } catch (Exception ignored) {
                }
            }
            if (o == null) {
                try {
                    Constructor constructor;
                    constructor = t.getConstructor();
                    o = constructor.newInstance();
                } catch (Exception ignored) {
                }
            }

            for (Method m : t.getDeclaredMethods()) {
                if (Modifier.isPublic(m.getModifiers())) {
                    MethodDeclaration tmp = new MethodDeclaration(o, m);
                }
            }

        }
    }
}

package com.duy.pascal.backend.tokenizer;


import com.duy.pascal.backend.exceptions.grouping.GroupingExceptionType;
import com.duy.pascal.backend.exceptions.grouping.GroupingException;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.tokens.EOFToken;
import com.duy.pascal.backend.tokens.GroupingExceptionToken;
import com.duy.pascal.backend.tokens.Token;
import com.duy.pascal.backend.tokens.WarningToken;
import com.duy.pascal.backend.tokens.closing.ClosingToken;
import com.duy.pascal.backend.tokens.grouping.BaseGrouperToken;
import com.duy.pascal.backend.tokens.grouping.GrouperToken;
import com.js.interpreter.source_include.ScriptSource;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Stack;

public class NewLexer implements Runnable {
    public BaseGrouperToken tokenQueue;
    private Stack<GrouperToken> groupers;
    private Lexer lexer;

    public NewLexer(Reader reader, String sourcename,
                    List<ScriptSource> searchDirectories) throws GroupingException {
        this.lexer = new Lexer(reader, sourcename, searchDirectories);
        groupers = new Stack<>();
        tokenQueue = new BaseGrouperToken(new LineInfo(0, sourcename));
        groupers.push(tokenQueue);
    }

    private void TossException(GroupingException e) {
        GroupingExceptionToken t = new GroupingExceptionToken(e);
        for (GrouperToken g : groupers) {
            g.put(t);
        }
    }

    private void TossException(LineInfo line, GroupingExceptionType.GroupExceptionType t) {
        GroupingExceptionToken gt = new GroupingExceptionToken(line, t);
        for (GrouperToken g : groupers) {
            g.put(gt);
        }
    }

    public void parse() {
        while (true) {
            GrouperToken topOfStack = groupers.peek();
            try {
                Token t = lexer.yylex();
                if (t instanceof EOFToken) {
                    if (groupers.size() != 1) {
                        TossException(((EOFToken) t).getClosingException(topOfStack));
                    } else {
                        topOfStack.put(t);
                    }
                    return;
                } else if (t instanceof ClosingToken) {
                    GroupingException g = ((ClosingToken) t).getClosingException(topOfStack);
                    if (g == null) {
                        topOfStack.put(new EOFToken(t.getLineInfo()));
                        groupers.pop();
                        continue;
                    } else {
                        TossException(g);
                        return;
                    }
                }
                if (t instanceof WarningToken) {
                    // TODO handle warnings...
                    continue;
                }

                // Everything else passes through normally.
                topOfStack.put(t);
                if (t instanceof GrouperToken) {
                    groupers.push((GrouperToken) t);
                }
            } catch (IOException e) {
                GroupingExceptionType g = new GroupingExceptionType(topOfStack.getLineInfo(),
                        GroupingExceptionType.GroupExceptionType.IO_EXCEPTION);
                g.caused = e;
                TossException(g);
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void run() {
        parse();
    }
}

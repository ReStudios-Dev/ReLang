package org.restudios.relang.parser.lexer;

import org.restudios.relang.parser.ast.LineCol;

import java.io.PrintStream;


public class LexerError {
    public final LineCol at;
    public final String source;
    public final String message;

    public LexerError(LineCol at, String source, String message) {
        this.at = at;
        this.source = source;
        this.message = message;
    }

    public void critical() {
        throw new RuntimeException(this.toString());
        //System.exit(1);
    }

    @SuppressWarnings("unused")
    public void print(PrintStream err) {
        err.println(this);
    }

    @Override
    public String toString() {
        return "[LEXER AT "+source+":"+this.at+"] "+this.message;
    }
}

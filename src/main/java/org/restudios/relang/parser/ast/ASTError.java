package org.restudios.relang.parser.ast;

import org.restudios.relang.parser.tokens.Token;

import java.io.PrintStream;


public class ASTError extends RuntimeException {
    public final Token token;
    public final String source;
    public final String message;

    public ASTError(Token token, String source, String message) {
        this.token = token;
        this.source = source;
        this.message = message;
    }

    public void critical() {
        throw this;//new RuntimeException("[PARSE AT "+source+":"+this.token.getFrom().toString()+"] "+this.message);
        //System.exit(1);
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println("[AST "+source+":"+token.getFrom()+"] "+message);
    }
}

package org.restudios.relang.parser.ast;

import org.restudios.relang.parser.tokens.Token;


public class ASTError {
    public final Token token;
    public final String source;
    public final String message;

    public ASTError(Token token, String source, String message) {
        this.token = token;
        this.source = source;
        this.message = message;
    }

    public void critical() {
        throw new RuntimeException("[PARSE AT "+source+":"+this.token.getFrom().toString()+"] "+this.message);
        //System.exit(1);
    }
}

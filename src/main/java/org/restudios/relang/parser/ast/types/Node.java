package org.restudios.relang.parser.ast.types;

import org.restudios.relang.parser.tokens.Token;

public abstract class Node {
    public final Token token;

    public Node(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}

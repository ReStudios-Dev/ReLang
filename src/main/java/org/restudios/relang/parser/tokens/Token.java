package org.restudios.relang.parser.tokens;

import org.restudios.relang.parser.ast.LineCol;

public class Token {
    public final TokenType type;
    public final String source;
    public final String string;
    private final int position;

    private final LineCol from;
    private final LineCol to;

    public Token(TokenType type, String source, String string, int position, LineCol from, LineCol to) {
        this.type = type;
        this.source = source;
        this.string = string;
        this.position = position;
        this.from = from;
        this.to = to;
    }

    public TokenType getType() {
        return type;
    }

    public String getString() {
        return string;
    }

    public int getPosition() {
        return position;
    }

    public String getSource() {
        return source;
    }

    public LineCol getFrom() {
        return from;
    }

    public LineCol getTo() {
        return to;
    }

    @Override
    public String toString() {
        return type.name();
    }

    public String stringify() {
        return source+":"+from;
    }
}

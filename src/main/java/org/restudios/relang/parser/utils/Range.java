package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.tokens.Token;

public class Range {
    private final Token from, to;

    public Range(Token from, Token to) {
        this.from = from;
        this.to = to;
    }

    public Range(Token from) {
        this.from = from;
        this.to = from;
    }

    public Token getFrom() {
        return from;
    }

    public Token getTo() {
        return to;
    }

    @Override
    public String toString() {
        return from+"-"+to;
    }
}

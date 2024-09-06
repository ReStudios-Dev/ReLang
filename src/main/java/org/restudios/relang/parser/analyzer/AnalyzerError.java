package org.restudios.relang.parser.analyzer;

import org.restudios.relang.parser.tokens.Token;

import java.io.PrintStream;
import java.io.PrintWriter;

public class AnalyzerError extends RuntimeException{
    Token token;
    public AnalyzerError(String message, Token token) {
        super(message);
        this.token = token;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.println("["+token.stringify()+"] "+getMessage());
    }
}
package org.restudios.relang.parser.utils;

import java.io.*;

public class RLOutput {

    public final PrintStream errorStream;
    public final PrintStream stream;
    public int exitCode;

    public RLOutput() {
        ByteArrayOutputStream out;
        stream = new PrintStream(out = new ByteArrayOutputStream());
        errorStream = new PrintStream(out);
    }

    public RLOutput(PrintStream stream) {
        this.stream = stream;
        this.errorStream = stream;
    }

    public RLOutput(PrintStream errorStream, PrintStream stream) {
        this.errorStream = errorStream;
        this.stream = stream;
    }
}

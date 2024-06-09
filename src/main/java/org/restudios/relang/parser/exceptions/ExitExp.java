package org.restudios.relang.parser.exceptions;

public class ExitExp extends RuntimeException{
    public final int code;

    public ExitExp(int code) {
        this.code = code;
    }
}

package org.restudios.relang.parser.ast;

public class LineCol {
    private final int line;
    private final int column;

    public LineCol(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return line + ":" + column;
    }
}

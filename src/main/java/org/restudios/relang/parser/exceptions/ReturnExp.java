package org.restudios.relang.parser.exceptions;

import org.restudios.relang.parser.ast.types.values.values.Value;

public class ReturnExp extends RuntimeException{
    public final Value value;

    public ReturnExp(Value value) {
        this.value = value;
    }
}

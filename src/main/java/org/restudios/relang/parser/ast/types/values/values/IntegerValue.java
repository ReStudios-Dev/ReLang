package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;

public class IntegerValue implements Value{

    private final int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return new Type(null, Primitives.INTEGER);
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public RLClass getRLClass() {
        return null;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public double floatValue() {
        return value;
    }

    @Override
    public boolean booleanValue() {
        return value > 1;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

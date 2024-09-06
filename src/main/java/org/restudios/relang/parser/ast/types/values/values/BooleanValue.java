package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;


public class BooleanValue implements Value{
    public final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return new Type(null, Primitives.BOOL);
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
        return value ? 1 : 0;
    }

    @Override
    public double floatValue() {
        return value ? 1 : 0;
    }

    @Override
    public boolean booleanValue() {
        return value;
    }

    @Override
    public String toString() {
        return "bool "+value;
    }

    @Override
    public Object convertNative() {
        return value;
    }

    @Override
    public String stringValue() {
        return value ? "true" : "false";
    }
}

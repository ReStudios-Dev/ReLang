package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;

public class NullValue implements Value{
    @Override
    public Type type() {
        return new Type(null, Primitives.NULL);
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
        return null;
    }

    @Override
    public int intValue() {
        return -Integer.MAX_VALUE;
    }

    @Override
    public double floatValue() {
        return -Float.MAX_VALUE;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public String stringValue() {
        return "null";
    }
}

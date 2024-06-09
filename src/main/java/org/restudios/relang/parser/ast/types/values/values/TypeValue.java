package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;

public class TypeValue implements Value{

    public final Type value;

    public TypeValue(Type value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return new Type(null, Primitives.TYPE);
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
        return -1;
    }

    @Override
    public double floatValue() {
        return -1;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public String toString() {
        return value.displayName();
    }
}
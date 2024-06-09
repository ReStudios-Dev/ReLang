package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.TBoolean;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;

public class TBooleanValue implements Value{
    public final TBoolean value;

    public TBooleanValue(TBoolean value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return new Type(null, Primitives.TBOOL);
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
        return value.ordinal();
    }

    @Override
    public double floatValue() {
        return value.ordinal();
    }

    @Override
    public boolean booleanValue() {
        return value == TBoolean.HIGH;
    }

    @Override
    public String stringValue() {
        return value.name().toLowerCase();
    }
}

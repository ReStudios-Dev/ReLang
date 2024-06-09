package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.RLEnumClass;
import org.restudios.relang.parser.ast.types.values.Context;

import java.util.ArrayList;

public class EnumItemValue implements Value {
    private RLEnumClass enumClass;
    private final String name;
    private Context context;

    public EnumItemValue(RLEnumClass enumClass, String name, Context context) {
        this.enumClass = enumClass;
        this.name = name;
        this.context = context;
    }

    public String name(){
        return name;
    }

    @Override
    public Type type() {
        return new Type(null, new ArrayList<>(), enumClass);
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public RLClass getRLClass() {
        return enumClass;
    }

    @Override
    public Object value() {
        return this;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public double floatValue() {
        return 0;
    }

    @Override
    public boolean booleanValue() {
        return true;
    }

    public void init(RLEnumClass rlEnumClass) {
        this.enumClass = rlEnumClass;
    }

    @Override
    public Value finalExpression() {
        return enumClass.instantiateEnumeration(context, this);
    }

    @Override
    public Value initContext(Context context) {
        this.context = context;
        return this;
    }
}

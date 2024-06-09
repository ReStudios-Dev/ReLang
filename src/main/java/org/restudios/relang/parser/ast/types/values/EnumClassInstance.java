package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;

import java.util.ArrayList;

public class EnumClassInstance extends ClassInstance {
    private final EnumItemValue value;

    @SuppressWarnings("unused")
    public EnumClassInstance(String sll, Context parent, EnumItemValue value) {
        super(sll, new ArrayList<>(), parent);
        this.value = value;
    }

    public EnumClassInstance(RLClass clazz, Context parent, EnumItemValue value) {
        super(clazz, new ArrayList<>(), parent);
        this.value = value;
    }

    public EnumItemValue getEnumValue() {
        return value;
    }
}

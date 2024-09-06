package org.restudios.relang.parser.ast.types.values.values.sll.classes;

import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLClassInstance;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.ArrayList;

public class RLStr extends SLLClassInstance {
    public String value;
    public RLStr(String value, Context parent) {
        super(DynamicSLLClass.STRING, new ArrayList<>(), parent);
        this.value = value;


    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int intValue() {
        return value.length();
    }

    @Override
    public double floatValue() {
        return value.length();
    }

    public String getValue() {
        return value;
    }


    @Override
    public Object convertNative() {
        return value;
    }
}

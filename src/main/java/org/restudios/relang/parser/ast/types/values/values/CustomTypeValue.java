package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.nodes.Type;

public class CustomTypeValue {
    public final String name;
    public Type value;

    public CustomTypeValue(String name, Type value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Type getValue() {
        return value;
    }

    public void setValue(Type value) {
        this.value = value;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public CustomTypeValue clone(){
        return new CustomTypeValue(name, value.clone());
    }

    @Override
    public String toString() {
        return getValue()+" "+name;
    }
}

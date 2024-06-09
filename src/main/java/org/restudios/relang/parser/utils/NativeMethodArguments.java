package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;

import java.util.HashMap;

public class NativeMethodArguments {
    private final HashMap<String, Value> values;

    public NativeMethodArguments() {
        values = new HashMap<>();
    }

    public HashMap<String, Value> getValues() {
        return values;
    }

    public Value getVariable(String name) {
        Value v = this.values.get(name);
        if(v instanceof Variable){
            v = ((Variable) v).absoluteValue();
        }

        return v;
    }
    @SuppressWarnings("unused")
    public <T> T getVariable(String name, Class<? extends T> cast) {
        Value v = this.values.get(name);
        if(v instanceof Variable){
            v = ((Variable) v).absoluteValue();
        }

        //noinspection unchecked
        return (T) v;
    }

    public String getString(String index) {
        return getVariable(index, RLStr.class).value;
    }
    public int getInt(String index) {
        return getVariable(index).intValue();
    }

    public double getFloat(String value) {
        return getVariable(value).floatValue();
    }

    public ClassInstance getClassInstance(String index) {
        return getVariable(index, ClassInstance.class);
    }
}

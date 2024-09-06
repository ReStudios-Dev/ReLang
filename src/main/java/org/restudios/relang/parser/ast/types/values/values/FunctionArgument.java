package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.nodes.Type;

import java.util.*;

public class FunctionArgument {
    public final String name;
    public final Type type;
    public final boolean varArg;

    public FunctionArgument(String name, Type type, boolean varArg) {
        this.name = name;
        this.type = type;
        this.varArg = varArg;
    }

    public static List<FunctionArgument> fromMap(LinkedHashMap<String, Type> arguments, boolean varArg) {
        List<FunctionArgument> fa= new ArrayList<>();
        ArrayList<Map.Entry<String, Type>> es = new ArrayList<>(arguments.entrySet());
        for (int i = 0; i < es.size(); i++) {
            Map.Entry<String, Type> e= es.get(i);
            boolean isVarArg = i+1 == es.size() && varArg;
            fa.add(new FunctionArgument(e.getKey(), e.getValue(), isVarArg));
        }
        return fa;
    }

    public boolean canBe(Type type) {
        return this.type.canBe(type);
    }

    public boolean isVarArg() {
        return varArg;
    }

    @Override
    public String toString() {
        return type+" "+name;
    }
}

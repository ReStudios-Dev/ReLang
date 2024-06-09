package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.nodes.Type;

public class FunctionArgument {
    public final String name;
    public final Type type;

    public FunctionArgument(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    public boolean canBe(Type type) {
        return this.type.canBe(type);
    }
}

package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;

public class VoidValue extends NullValue {
    @Override
    public Type type() {
        return new Type(null,  Primitives.VOID);
    }
}

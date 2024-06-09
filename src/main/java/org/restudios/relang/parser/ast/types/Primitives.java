package org.restudios.relang.parser.ast.types;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLEnumClass;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

public enum Primitives {
    CHAR,
    INTEGER,
    FLOAT ,
    BOOL ,
    TBOOL ,
    VOID,
    ARRAY, NULL, TYPE;
    public Type type(){
        return Type.primitive(this);
    }

    public Value getReflectionClass(Context context) {
        RLEnumClass e = ((RLEnumClass) context.getClass(DynamicSLLClass.REFL_PRIMITIVE));
        e.initStatic();
        for (EnumItemValue value : e.values) {
            if (value.name().equalsIgnoreCase(this.name())) {
                return e.instantiateEnumeration(context, value);
            }
        }
        return new NullValue();
    }
}

package org.restudios.relang.parser.ast.types;

import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLEnumClass;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.List;

public enum Visibility {
    PUBLIC, PRIVATE, READONLY,
    OVERRIDE, FINAL, STATIC;

    public Value getReflectionVisibilityClass(Context context) {
        RLEnumClass e = ((RLEnumClass) context.getClass(DynamicSLLClass.REFL_VISIBILITY));
        e.initContext(context);
        e.initStatic();
        for (EnumItemValue value : e.values) {
            if (value.name().equalsIgnoreCase(this.name())) {
                return e.instantiateEnumeration(context, value);
            }
        }
        return new NullValue();
    }

    public static Value getReflectionVisibility(List<Visibility> visibilityList, Context context){
        for (Visibility vis : visibilityList) {
            if(vis == Visibility.PRIVATE || vis == Visibility.PUBLIC){
                return vis.getReflectionVisibilityClass(context);
            }
        }
        return new NullValue();
    }
}

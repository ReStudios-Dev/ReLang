package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.values.Value;

import java.util.ArrayList;
import java.util.List;

public interface Instantiable<T extends Instance> {
    default T instantiate(Context context) {
        return instantiate(context, new ArrayList<>());
    }
    default T instantiate(Context context, Value... constructorArguments) {
        return instantiate(context, new ArrayList<>(), constructorArguments);
    }
    T instantiate(Context context, List<Type> types, Value... constructorArguments);
}

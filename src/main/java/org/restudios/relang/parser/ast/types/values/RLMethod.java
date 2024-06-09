package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.ReFunction;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;

import java.util.List;

public abstract class RLMethod extends ReFunction {

    public RLMethod(List<FunctionArgument> arguments, List<CustomTypeValue> customTypes, Type returnType) {
        super(arguments, returnType, customTypes);
    }

}

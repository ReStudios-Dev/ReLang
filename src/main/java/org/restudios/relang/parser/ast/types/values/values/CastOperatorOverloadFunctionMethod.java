package org.restudios.relang.parser.ast.types.values.values;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CastOperatorOverloadFunctionMethod extends FunctionMethod {
    public final FunctionArgument from;
    public final Type to;
    public final boolean implicit;

    public CastOperatorOverloadFunctionMethod(List<CustomTypeValue> customTypes, BlockStatement code, FunctionArgument from, Type to, boolean implicit) {
        super(customTypes, Collections.singletonList(from), to, implicit?"implicit":"explicit", new ArrayList<>(), code, false, false);
        this.from = from;
        this.to = to;
        this.implicit = implicit;
    }

}

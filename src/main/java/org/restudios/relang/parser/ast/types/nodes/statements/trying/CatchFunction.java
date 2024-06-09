package org.restudios.relang.parser.ast.types.nodes.statements.trying;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.*;

import java.util.ArrayList;
import java.util.List;

public class CatchFunction extends ReFunction {
    private final List<FunctionArgument> catching;
    private final Statement body;

    public CatchFunction(List<FunctionArgument> catching, Statement body) {
        super(catching, Type.primitive(Primitives.VOID), new ArrayList<>());
        this.catching = catching;
        this.body = body;
    }

    @Override
    public Value handle(Context context, Context callContext) {
        body.execute(context);

        return new VoidValue();
    }
    @SuppressWarnings("unused")
    public List<FunctionArgument> getCatching() {
        return catching;
    }
}

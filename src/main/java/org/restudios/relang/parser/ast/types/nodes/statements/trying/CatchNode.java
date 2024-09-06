package org.restudios.relang.parser.ast.types.nodes.statements.trying;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.statements.VariableDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.ReFunction;

import java.util.ArrayList;
import java.util.List;

public class CatchNode  {
    private final List<VariableDeclarationStatement> catching;
    private final Statement body;

    public CatchNode(List<VariableDeclarationStatement> catching, Statement body) {
        this.catching = catching;
        this.body = body;
    }

    public List<VariableDeclarationStatement> getCatching() {
        return catching;
    }

    public Statement getBody() {
        return body;
    }

    public ReFunction function() {
        List<FunctionArgument> functionArguments = new ArrayList<>();
        for (VariableDeclarationStatement variableDeclarationStatement : catching) {
             functionArguments.add(new FunctionArgument(
                     variableDeclarationStatement.variable,
                     variableDeclarationStatement.type,
                     false
             ));
        }
        return new CatchFunction(functionArguments, body);
    }

    public void analyze(AnalyzerContext n) {
        AnalyzerContext context = n.create();

        for (VariableDeclarationStatement variableDeclarationStatement : catching) {
            context.putVariable(variableDeclarationStatement.variable, variableDeclarationStatement.type);
        }
        body.analyze(context);
    }
}

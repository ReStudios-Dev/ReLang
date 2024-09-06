package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.trying.CatchNode;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.List;

public class TryStatement extends Statement {

    private Statement body;
    private List<CatchNode> catching;

    public TryStatement(Token token, Statement body, List<CatchNode> catching) {
        super(token);
        this.body = body;
        this.catching = catching;
    }

    public CatchNode find(Type c, Context context){
        for (CatchNode catchNode : catching) {
            for (VariableDeclarationStatement functionArgument : catchNode.getCatching()) {
                functionArgument.type.init(context);
                functionArgument.type.initClassOrType(context);
                if(c.canBe(functionArgument.type)) return catchNode;
            }
        }
        return null;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        AnalyzerContext nc = context.create();
        body.analyze(nc);
        for (CatchNode catchNode : catching) {
            catchNode.analyze(context);
        }
    }

    public Value[] getArguments(CatchNode node, ClassInstance instance, Context context){
        Value[] values = new Value[node.getCatching().size()];
        for (int i = 0; i < node.getCatching().size(); i++) {
            VariableDeclarationStatement functionArgument = node.getCatching().get(i);
            functionArgument.type.init(context);
            functionArgument.type.initClassOrType(context);
            if(instance.type().canBe(functionArgument.type)){
                values[i] = instance;
            }else{
                values[i] = new NullValue();
            }
        }
        return values;
    }
    @SuppressWarnings("unused")
    public TryStatement(Token token) {
        super(token);
    }

    @Override
    public void execute(Context context) {
        Context nc = new Context(context);
        try {
            body.execute(nc);
        } catch (RLException e) {
            CatchNode node = find(e.getType(), context);
            node.function().runMethod(context, context, getArguments(node, e.instantiate(context), context));
        }
    }

    public Statement getBody() {
        return body;
    }
    @SuppressWarnings("unused")
    public List<CatchNode> getCatching() {
        return catching;
    }
}

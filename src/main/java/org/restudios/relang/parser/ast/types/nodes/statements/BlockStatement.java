package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.nodes.DeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;

public class BlockStatement extends Statement {
    public final ArrayList<Statement> statements;

    public BlockStatement(Token token, ArrayList<Statement> statements) {
        super(token);
        this.statements = statements;
    }

    public static BlockStatement returnStatement(Expression value, Token token) {
        BlockStatement bs = new BlockStatement(token, new ArrayList<>());

        Statement ret = new ReturnStatement(token, value);

        bs.statements.add(ret);

        return bs;
    }


    @Override
    public void execute(Context context) {
        for (Statement statement : statements) {
            statement.execute(context);
        }
    }

    /**
     * When run the program
     */
    public void runProgram(Context context){
        ArrayList<DeclarationStatement> topPriority = new ArrayList<>();
        ArrayList<Statement> lowPriority = new ArrayList<>();

        for (Statement statement : statements) {
            if(statement instanceof MethodDeclarationStatement){
                topPriority.add((DeclarationStatement) statement);
            }else if(statement instanceof ClassDeclarationStatement){
                topPriority.add((DeclarationStatement) statement);
            } else {
                lowPriority.add(statement);
            }
        }
        topPriority.forEach(statement -> statement.validate(context));
        lowPriority.forEach(statement -> statement.execute(context));
    }



    public void prepare(Context context){
        ArrayList<DeclarationStatement> topPriority = new ArrayList<>();
        for (Statement statement : statements) {
            if(statement instanceof MethodDeclarationStatement){
                topPriority.add((DeclarationStatement) statement);
            }else if(statement instanceof ClassDeclarationStatement){
                topPriority.add((DeclarationStatement) statement);
            }
        }
        topPriority.forEach(statement -> statement.prepare(context));
    }

    @Override
    public void analyze(AnalyzerContext context) {
        AnalyzerContext nc = context.create();
        for (Statement statement : statements) {
            statement.analyze(nc);
        }
    }
}

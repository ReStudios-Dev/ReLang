package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.FloatValue;
import org.restudios.relang.parser.ast.types.values.values.IntegerValue;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class ForiStatement extends Statement {
    public final Statement initialization;
    public final Expression initValue;
    public final Expression maxValue;
    public final Expression step;
    public final Statement body;

    public ForiStatement(Token token, Statement initialization, Expression initValue, Expression maxValue, Expression step, Statement body) {
        super(token);
        this.initialization = initialization;
        this.initValue = initValue;
        this.maxValue = maxValue;
        this.step = step;
        this.body = body;
    }
    @Override
    public void execute(Context context) {
        Context sub = new Context(context);
        double in = initValue.eval(sub).floatValue();
        double max = maxValue.eval(sub).floatValue();
        double st = step.eval(sub).floatValue();
        initialization.execute(sub);
        if(initialization instanceof VariableDeclarationStatement){
            VariableDeclarationStatement v = (VariableDeclarationStatement)initialization;
            boolean integer = checkInt(v.type, context);
            for (double i = in; i <= max; i+= st) {
                Context perStepSub = new Context(sub);
                Variable fx = new Variable(v.type, v.variable, integer ? new IntegerValue((int) i) : new FloatValue(i), v.visibilities);
                perStepSub.putVariable(fx);
                body.execute(perStepSub);
            }
        }
    }
    private boolean checkInt(Type t, Context context){
        if(t.isCustomType()){
            throw new RLException("Variable type can be integer or float", Type.internal(context), context);
        }
        if(t.primitive != Primitives.INTEGER && t.primitive != Primitives.FLOAT){
            throw new RLException("Variable type can be integer or float", Type.internal(context), context);
        }
        return t.primitive == Primitives.INTEGER;
    }
}

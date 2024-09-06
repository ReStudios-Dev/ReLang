package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.Variable;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class ForeachStatement extends Statement {
    public final VariableDeclarationStatement variable;
    public final Expression array;
    public final Statement code;

    public ForeachStatement(Token token, VariableDeclarationStatement variable, Expression array, Statement code) {
        super(token);
        this.variable = variable;
        this.array = array;
        this.code = code;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        AnalyzerContext nc = context.create();
        Type t = variable.type;
        t.initClassOrType(context);
        variable.analyze(nc);
        Type arr = array.predictType(context);
        arr.initClassOrType(context);
        if(arr.clazz.isInstanceOf(context.getClass(DynamicSLLClass.ARRAY))){
            arr.firstTypeOrVoid().initClassOrType(context);
            if(!arr.firstTypeOrVoid().canBe(t)){
                throw new AnalyzerError("Invalid array iterate type", array.token);
            }
        }else{
            throw new AnalyzerError("Cannot iterate non array", arr.token);
        }
        code.analyze(nc);
    }

    @Override
    public void execute(Context context) {
        Value arr = array.eval(context);
        arr = arr.finalExpression();
        if(arr instanceof NullValue){
            throw new RLException("Array is null", Type.nullPointer(context), context);
        }
        if(arr instanceof RLArray){
            iterateList((RLArray) arr, context);
            return;
        }
        throw new RLException("Cannot iterate non array", Type.internal(context), context);
    }
    private void iterateList(RLArray value, Context context) {
        variable.type.init(context);
        for (Value item : value.getValues()) {
            if(!item.type().canBe(variable.type)){
                throw new RLException("Cast error. "+item.type().displayName()+" to "+variable.type.displayName(), Type.internal(context), context);
            }
            Context sub = new Context(context);
            Variable v = new Variable(variable.type, variable.variable, item, variable.visibilities);
            sub.putVariable(v);
            code.execute(sub);
        }
    }
}

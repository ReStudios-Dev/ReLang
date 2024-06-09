package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Statement;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.IdentifierExpression;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.RLEnumClass;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class MemberStatement extends Statement  {
    public final Expression left;
    public final Expression right;

    public MemberStatement(Token token, Expression left, Expression right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    @Override
    public Value eval(Context context) {
        Value l = left.eval(context).initContext(context).finalExpression();
        if(l instanceof NullValue){
            throw new RLException("null value", Type.nullPointer(context), context);
        }
        if(!(l instanceof ClassInstance) && !(l instanceof RLClass) && !(l instanceof EnumItemValue)){
            throw new RLException("Cannot access non class", Type.internal(context), context);
        }
        if(right instanceof MethodCallStatement){
            MethodCallStatement mcs = (MethodCallStatement) right;
            if(l instanceof ClassInstance){
                ClassInstance ci = (ClassInstance) l;
                return mcs.fromClassInstance(right.token, context, ci);
            }
            if(l instanceof EnumItemValue){
                EnumItemValue etv = (EnumItemValue) l;
                return mcs.fromClassInstance(right.token, context, ((RLEnumClass) etv.getRLClass()).instantiateEnumeration(context, etv));
            }

            RLClass clazz = (RLClass) l;
            clazz.initStatic();
            return mcs.fromStatic(clazz, context);
        }
        IdentifierExpression variable = (IdentifierExpression) right;
        if(l instanceof ClassInstance){
            return variable.fromClassInstance(((ClassInstance) l));
        }
        if(!(l instanceof RLClass)){
            throw new RLException("Cannot operate", Type.internal(context), context);
        }
        RLClass clazz = (RLClass) l;
        clazz.initStatic();
        if (variable.value.equals("class")) {
            return clazz.getReflectionClass(context);
        }
        return right.eval(clazz.getStaticContext());
    }


    @Override
    public void execute(Context context) {
        eval(context);
    }
}

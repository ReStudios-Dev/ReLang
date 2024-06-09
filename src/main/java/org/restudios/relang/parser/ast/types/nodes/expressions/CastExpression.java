package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.CastOperatorOverloadFunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

public class CastExpression extends Expression {
    public final Type type;
    public final Expression expression;

    public CastExpression(Token token, Type type, Expression expression) {
        super(token);
        this.type = type;
        this.expression = expression;
    }

    public static Value cast(Type castToType, Value valueToCast, Context context) {
        valueToCast = valueToCast.initContext(context).finalExpression();
        castToType.initClassOrType(context);
        if(castToType.isCustomType())castToType.init(context);
        if(valueToCast instanceof ClassInstance){
            ClassInstance ci = (ClassInstance) valueToCast;

            if(castToType.isCustomType()){
                castToType.init(context);
                if(ci.getRLClass().isAssignableFrom(castToType.clazz)){

                    if (ci.getSubTypes().size() == castToType.subTypes.size()) {
                        boolean success = true;
                        for (int i = 0; i < ci.getSubTypes().size(); i++) {
                            CustomTypeValue cst = ci.getSubTypes().get(i);
                            Type t = castToType.subTypes.get(i);
                            t.init(context);
                            if (!t.canBe(cst.value)) {
                                success = false;
                                break;
                            }
                        }
                        if (success) {
                            return ci;//ci.cast(castToType, context);
                        }else{
                            throw new RLException("Cannot cast "+valueToCast.type().displayName()+" to "+castToType.displayName(), Type.castException(context), context);
                        }
                    }
                }
            }

            CastOperatorOverloadFunctionMethod overload = ci.getRLClass().getExplicitOverloading(castToType);
            if(overload != null) return overload.runMethod(ci.getRLClass().getStaticContext(), context, valueToCast);
        }
        if(castToType.isCustomType()){
            RLClass clazz = castToType.clazz;
            CastOperatorOverloadFunctionMethod overload = clazz.getImplicitOverloading(valueToCast.type());
            if(overload != null) return overload.runMethod(clazz.getStaticContext(), context, valueToCast);
        }

        if(valueToCast.type().like(castToType)){
            return valueToCast;
        }
        if(!valueToCast.type().canBe(castToType)){
            throw new RLException("Cannot cast "+valueToCast.type().displayName()+" to "+castToType.displayName(), Type.castException(context), context);
        }

        // TODO: cast
        return valueToCast;
    }

    @Override
    public Value eval(Context context) {
        return cast(type, expression.eval(context).initContext(context).finalExpression(), context);
    }
}

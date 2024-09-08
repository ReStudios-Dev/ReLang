package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.values.CastOperatorOverloadFunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLRunnable;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.NativeMethod;
import org.restudios.relang.parser.utils.NativeMethodBuilder;

import java.util.LinkedHashMap;
import java.util.List;

public class CastExpression extends Expression {
    public final Type type;
    public final Expression expression;

    public CastExpression(Token token, Type type, Expression expression) {
        super(token);
        this.type = type;
        this.expression = expression;
    }

    @Override
    public Type predictType(AnalyzerContext c) {
        Type t = expression.predictType(c);
        t.initClassOrType(c);
        type.initClassOrType(c);
        if(!t.canBe(type, true)){
            throw new AnalyzerError("Invalid cast type", token);
        }
        return type;
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
                            if(ci.isRunnable() && castToType.isRunnable()){
                                ((RLRunnable) ci).setReturn(castToType.subTypes.get(0));
                            }
                            return ci;//ci.cast(castToType, context);
                        }else{
                            throw new RLException("Cannot cast "+valueToCast.type().displayName()+" to "+castToType.displayName(), Type.castException(context), context);
                        }
                    }
                }else{
                    if(castToType.clazz != null && castToType.clazz.isInterface() && ci.isRunnable()){
                        RLClass clazz = castToType.clazz;
                        if(clazz.getAllDeclaredMethods().size() == 1){
                            RLRunnable rl = (RLRunnable) ci;
                            FunctionMethod fm = clazz.getAllDeclaredMethods().get(0);
                            List<FunctionArgument> fromArg = rl.getFunction().getArguments();
                            List<FunctionArgument> toArg = fm.getArguments();
                            boolean suc = !fromArg.isEmpty() || toArg.isEmpty();
                            if(!fromArg.isEmpty() && toArg.isEmpty()) suc = false;
                            if(suc) for (int i = 0; i < fromArg.size(); i++) {
                                FunctionArgument from = fromArg.get(i);
                                FunctionArgument to = toArg.get(i);
                                from.type.init(context);
                                to.type.init(context);
                                if(!from.type.canBe(to.type)){
                                    suc = false;
                                    break;
                                }
                            }
                            if(suc){
                                fm.getReturnType().init(context);
                                rl.setReturn(fm.getReturnType());

                                ClassInstance c = clazz.instantiate(context);

                                LinkedHashMap<String, Type> args = new LinkedHashMap<>();
                                for (FunctionArgument argument : fm.getArguments()) {
                                    args.put(argument.name, argument.type);
                                }
                                c.implementMethod(fm, (NativeMethod) new NativeMethodBuilder(clazz, fm.name)
                                        .setArguments(args)

                                        .setHandler((arguments, context1, callContext, instance) -> {
                                            Value[] vals = new Value[fm.getArguments().size()];
                                            for (int i = 0; i < fm.getArguments().size(); i++) {
                                                FunctionArgument fa = fm.getArguments().get(i);
                                                vals[i] = arguments.getVariable(fa.name).finalExpression();
                                            }
                                            return rl.getFunction().execute(context1, callContext, vals);
                                        }).build().setReturnType(fm.getReturnType()));
                                return c;

                            }

                        }
                    }
                }
            }

            CastOperatorOverloadFunctionMethod overload = ci.getRLClass().findExplicitOperator(castToType);
            if(overload != null) return overload.runMethod(ci.getRLClass().getStaticContext(), context, valueToCast);
        }
        if(castToType.isCustomType()){
            RLClass clazz = castToType.clazz;
            CastOperatorOverloadFunctionMethod overload = clazz.findImplicitOperator(valueToCast.type());
            if(overload != null) return overload.runMethod(clazz.getStaticContext(), context, valueToCast);
        }

        if(valueToCast.type().like(castToType)){
            return valueToCast;
        }
        if(!valueToCast.type().canBe(castToType)){
            throw new RLException("Cannot cast "+valueToCast.type().displayName()+" to "+castToType.displayName(), Type.castException(context), context);
        }
        return valueToCast;
    }

    @Override
    public Value eval(Context context) {
        return cast(type, expression.eval(context).initContext(context).finalExpression(), context);
    }
}

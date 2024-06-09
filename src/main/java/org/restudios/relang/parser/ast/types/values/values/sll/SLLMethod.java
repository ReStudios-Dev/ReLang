package org.restudios.relang.parser.ast.types.values.values.sll;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.utils.NativeMethodArguments;

import java.util.*;

public class SLLMethod extends FunctionMethod {

    @SafeVarargs
    public static LinkedHashMap<String, Type> args(Map.Entry<String, Type>... args) {
        LinkedHashMap<String, Type> result = new LinkedHashMap<>();
        if(args != null) for (Map.Entry<String, Type> arg : args) {
            result.put(arg.getKey(), arg.getValue());
        }
        return result;
    }
    private final String name;
    public final boolean staticMethod;
    public final LinkedHashMap<String, Type> arguments;
    private final SLLMethodCall handler;
    private Context context;

    public SLLMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, SLLMethodCall handler, FunctionMethod original) {
        super(original);
        this.staticMethod = staticMethod;
        this.name = name;
        this.arguments = arguments;
        this.handler = handler;
    }
    @SuppressWarnings("unused")
    public SLLMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, SLLMethodCall handler, ClassInstance clazz) {
        this(name, staticMethod, arguments, handler, clazz.getRLClass().originalMethod(name, arguments, clazz.getContext()));
    }
    public SLLMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, SLLMethodCall handler, RLClass clazz, Context context) {
        this(name, staticMethod, arguments, handler, clazz.originalMethod(name, arguments, context));
        this.context = context;
    }
    @SuppressWarnings("unused")
    public SLLMethod(String name, boolean staticMethod, Map.Entry<String, Type> argument, SLLMethodCall handler, RLClass clazz, Context context) {
        this(name, staticMethod, argumentToMap(argument), handler, clazz.originalMethod(name, argumentToMap(argument), context));
        this.context = context;
    }
    public SLLMethod(String name, boolean staticMethod, SLLMethodCall handler, RLClass clazz, Context context) {
        this(name, staticMethod, new LinkedHashMap<>(), handler, clazz.originalMethod(name, new LinkedHashMap<>(), context));
        this.context = context;
    }
    private static LinkedHashMap<String, Type> argumentToMap(Map.Entry<String, Type> i){
        LinkedHashMap<String, Type> result = new LinkedHashMap<>();
        result.put(i.getKey(), i.getValue());
        return result;
    }

    public Value execute(NativeMethodArguments arguments, Context context, Context callContext) {
        ClassInstance ci = null;
        if(context.containsVariable("this")){
            ci = context.thisClass();
        }
        return handler.apply(arguments, context, callContext, ci);
    }

    @Override
    public Value execute(Context context, Context callContext, Value... values) {
        NativeMethodArguments args = new NativeMethodArguments ();
        ArrayList<String> names = new ArrayList<>(arguments.keySet());
        for (int i = 0; i < names.size(); i++) {
            args.getValues().put(names.get(i), values[i]);
        }
        return execute(args, context, callContext);
    }

    public String getName() {
        return name;
    }

    @Override
    public Value handle(Context context, Context callContext) {return null;}
    @SuppressWarnings("unused")
    public LinkedHashMap<String, Type> getAArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(!(obj instanceof FunctionMethod)){
            return false;
        }
        List<FunctionArgument> fa1 = getArguments();
        FunctionMethod functionMethod = (FunctionMethod) obj;
        getReturnType().init(context);
        functionMethod.getReturnType().init(context);

        if(getReturnType().token.string.equals(functionMethod.getReturnType().token.string)
                || getReturnType().canBe(functionMethod.getReturnType())){
            if (functionMethod.name.equals(getName())){
                if(functionMethod.visibility.equals(visibility)){
                    List<FunctionArgument> fa2 = functionMethod.getArguments();
                    if(fa1.size() == fa2.size()){
                        for (int i = 0; i < fa1.size(); i++) {
                            if(!fa1.get(i).type.like(fa2.get(i).type)){
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public interface SLLMethodCall {
        Value apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance clazz);
    }
}

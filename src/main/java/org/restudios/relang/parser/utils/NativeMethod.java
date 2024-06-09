package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.NullValue;
import org.restudios.relang.parser.ast.types.values.values.Value;

import java.util.*;
@SuppressWarnings("unused")
public class NativeMethod extends FunctionMethod {
    private final String name;
    public final boolean staticMethod;
    public final LinkedHashMap<String, Type> arguments;
    private final NativeMethodExecution handler;
    private Context context;

    public void init(FunctionMethod original){
        this.setCustomTypes(original.getCustomTypes());
        this.setArguments(original.getArguments());
        this.setReturnType(original.getReturnType());
        this.visibility = original.visibility;
    }
    public NativeMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, NativeMethodExecution handler) {
        super(new ArrayList<>(), new ArrayList<>(), Type.primitive(Primitives.VOID), name, new ArrayList<>(), null, false, true);
        this.staticMethod = staticMethod;
        this.name = name;
        this.arguments = arguments;
        this.handler = handler;
    }
    public NativeMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, NativeMethodExecution handler, FunctionMethod original) {
        super(original);
        this.staticMethod = staticMethod;
        this.name = name;
        this.arguments = arguments;
        this.handler = handler;
    }
    public NativeMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, NativeMethodExecution handler, ClassInstance clazz) {
        this(name, staticMethod, arguments, handler, clazz.getRLClass().originalMethod(name, arguments, clazz.getContext()));
    }
    public NativeMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, NativeMethodExecution handler, RLClass clazz, Context context) {
        this(name, staticMethod, arguments, handler, clazz.originalMethod(name, arguments, context));
        this.context = context;
    }
    public NativeMethod(String name, boolean staticMethod, Map.Entry<String, Type> argument, NativeMethodExecution handler, RLClass clazz, Context context) {
        this(name, staticMethod, argumentToMap(argument), handler, clazz.originalMethod(name, argumentToMap(argument), context));
        this.context = context;
    }
    public NativeMethod(String name, boolean staticMethod, NativeMethodExecution handler, RLClass clazz, Context context) {
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
        Value v = handler.apply(arguments, context, callContext, ci);
        return v == null ? new NullValue() : v;
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
        getReturnType().initClassOrType(context);
        functionMethod.getReturnType().init(context);
        functionMethod.getReturnType().initClassOrType(context);
        if(getReturnType().canBe(functionMethod.getReturnType()) || getReturnType().tokenEquality(functionMethod.getReturnType())){
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
    public interface NativeMethodExecution {

        Value apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
    public interface VoidNativeMethodExecution {
        void apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
    public interface IntNativeMethodExecution {
        int apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
    public interface FloatNativeMethodExecution {
        double apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
    public interface CharNativeMethodExecution {
        char apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
    public interface BoolNativeMethodExecution {
        boolean apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
    public interface StrNativeMethodExecution {
        String apply(NativeMethodArguments arguments, Context context, Context callContext, ClassInstance instance);
    }
}

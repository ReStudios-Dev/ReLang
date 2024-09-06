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
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.*;
@SuppressWarnings("unused")
public class NativeMethod extends FunctionMethod {
    private final String name;
    public final boolean staticMethod;
    public final LinkedHashMap<String, Type> arguments;
    private final NativeMethodExecution handler;
    private final boolean constructorMethod;
    private final boolean varArgs;
    private Context context;

    public void init(FunctionMethod original){
        this.setCustomTypes(original.getCustomTypes());
        this.setArguments(original.getArguments());
        this.setReturnType(original.getReturnType());
        this.visibility = original.visibility;
    }
    public NativeMethod(String name, boolean staticMethod, boolean constructorMethod, LinkedHashMap<String, Type> arguments, boolean varArgs, NativeMethodExecution handler) {
        super(new ArrayList<>(), FunctionArgument.fromMap(arguments, varArgs), Type.primitive(Primitives.VOID), name, new ArrayList<>(), null, false, true, new ArrayList<>());
        this.staticMethod = staticMethod;
        this.name = name;
        this.arguments = arguments;
        this.handler = handler;
        this.varArgs = varArgs;
        this.constructorMethod = constructorMethod;
    }
    public NativeMethod(String name, boolean staticMethod, boolean constructorMethod, LinkedHashMap<String, Type> arguments, boolean varArgs, NativeMethodExecution handler, FunctionMethod original) {
        super(original);
        this.staticMethod = staticMethod;
        this.name = name;
        this.arguments = arguments;
        this.handler = handler;
        this.varArgs = varArgs;
        this.constructorMethod = constructorMethod;
    }
    public NativeMethod(String name, boolean staticMethod, boolean constructorMethod, LinkedHashMap<String, Type> arguments, boolean varArgs, NativeMethodExecution handler, ClassInstance clazz) {
        this(name, staticMethod, constructorMethod, arguments, varArgs, handler, clazz.getRLClass().findRawMethod(name, arguments, varArgs, clazz.getContext()));
    }
    public NativeMethod(String name, boolean staticMethod, boolean constructorMethod, LinkedHashMap<String, Type> arguments, boolean varArgs, NativeMethodExecution handler, RLClass clazz, Context context) {
        this(name, staticMethod, constructorMethod, arguments,varArgs, handler, clazz.findRawMethod(name, arguments, varArgs, context));
        this.context = context;
    }
    public NativeMethod(String name, boolean staticMethod, boolean constructorMethod, Map.Entry<String, Type> argument, boolean varArgs, NativeMethodExecution handler, RLClass clazz, Context context) {
        this(name, staticMethod, constructorMethod, argumentToMap(argument), varArgs, handler, clazz.findRawMethod(name, argumentToMap(argument), varArgs, context));
        this.context = context;
    }
    public NativeMethod(String name, boolean staticMethod, boolean constructorMethod, NativeMethodExecution handler, RLClass clazz, Context context) {
        this(name, staticMethod, constructorMethod, new LinkedHashMap<>(), false, handler, clazz.findRawMethod(name, new LinkedHashMap<>(), false, context));
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
        if(arguments.size() != values.length && (!varArgs || values.length < names.size()-1)){
            throw new RLException("Method receiving "+arguments.size()+" arguments, but got "+values.length, Type.internal(context), context);
        }
        for (int i = 0; i < names.size(); i++) {
            boolean last = i == names.size() -1;
            Type type = arguments.get(names.get(i));
            Value v = null;
            if(last && varArgs){
                List<Value> vals = new ArrayList<>(Arrays.asList(values).subList(i, values.length));
                RLArray a = new RLArray(type, context);
                for (Value val : vals) {
                    a.add(val);
                }
                v = a;
            }
            if(v == null) v= values[i];
            args.getValues().put(names.get(i), v);
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

    @Override
    public boolean canBeExecuted(Value[] values, Context context) {
        Map<String, Type> map = arguments;
        List<Type> args = new ArrayList<>(map.values());
        if(args.size() != values.length) return false;
        for (int i = 0; i < values.length; i++) {
            if(values[i].getRLClass() != null) {
                args.get(i).init(values[i].getRLClass().getCreatedContext());
            } else {
                args.get(i).init(context);
            }
            if(!values[i].type().canBe(args.get(i)))return false;
        }
        return  true;
    }

    public boolean isConstructorMethod() {
        return constructorMethod;
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

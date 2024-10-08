package org.restudios.relang.parser.ast.types.values.values.nativing;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.utils.NativeClass;
import org.restudios.relang.parser.utils.NativeMethod;
import org.restudios.relang.parser.utils.NativeMethodArguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class DynamicNativeClass extends RLClass {
    public final NativeClass nativeClass;
    public DynamicNativeClass(NativeClass nativeClass, RLClass original){
        this(nativeClass, original.getName(), original.getClassType(), original.getVisibility());
    }
    public DynamicNativeClass(NativeClass nativeClass, String name, ClassType classType, ArrayList<Visibility> visibility) {
        super(name, classType, visibility);
        this.nativeClass = nativeClass;
    }

    public void init() {
        for (NativeMethod nativeMethod : nativeClass.getNativeMethods()) {
            FunctionMethod om = originalMethod(nativeMethod.getName(), nativeMethod.arguments, nativeMethod.isConstructorMethod());
            if(om == null){
                throw new RuntimeException("Original method "+nativeMethod.getName()+" not found");
            }
            if(om.isNative){
                nativeMethod.init(om);
            }
        }
    }

    @Override
    public FunctionMethod callConstructor(Context context, Context constructContext, Value... values) {
        if(nativeClass.getNativeMethods().isEmpty()) {
            return super.callConstructor(context, constructContext, values);
        }
        for (NativeMethod constructor : nativeClass.getNativeMethods()) {
            if(!constructor.isConstructorMethod()) continue;
            if(!constructor.canBeExecuted(values, constructContext)) continue;
            List<Type> fa = new ArrayList<>(constructor.arguments.values());
            Value[] casted = new Value[fa.size()];
            for (int i = 0; i < fa.size(); i++) {
                Type f = fa.get(i);
                casted[i] = CastExpression.cast(f, values[i], context);
            }
            constructor.runMethod(constructContext, context, casted);
            return constructor;
        }
        return super.callConstructor(context, constructContext, values);
    }

    @Override
    public ClassInstance instantiate(Context context, List<Type> types, Value... constructorArguments) {
        initializeStaticContext();
        ClassInstance ci = new NativeClassInstance(this.getName(), types, context);
        createdChild(ci);
        FunctionMethod fm = callConstructor(context, ci.getContext(), constructorArguments);
        NativeMethodArguments args = new NativeMethodArguments();
        if(fm != null){
            List<FunctionArgument> arguments = fm.getArguments();
            for (int i = 0; i < arguments.size(); i++) {
                FunctionArgument argument = arguments.get(i);
                args.getValues().put(argument.name, constructorArguments[i]);
            }
        }
        for (Consumer<NativeMethodArguments> nativeMethodArgumentsConsumer : nativeClass.getOnInstantiate()) {
            nativeMethodArgumentsConsumer.accept(args);
        }
        context.getModuleRegistry().forEach(module -> module.onClassInstantiate(ci, context, constructorArguments));
        return ci;
    }


    @Override
    public ArrayList<FunctionMethod> getAllMethods(boolean includeThis, boolean implementedOnly, boolean allowStatic) {
        ArrayList<FunctionMethod> result = super.getAllMethods(includeThis, implementedOnly, allowStatic);
        for (NativeMethod sllMethod : nativeClass.getNativeMethods()) {
            //noinspection DataFlowIssue
            result = tryToAdd(sllMethod, allowStatic, result, getCreatedContext());
        }
        return result;
    }


    @Override
    public ArrayList<FunctionMethod> getStaticMethods() {
        ArrayList<FunctionMethod> fm = super.getStaticMethods();
        for (NativeMethod nativeMethod : nativeClass.getNativeMethods()) {
            if(nativeMethod.staticMethod){
                nativeMethod.getReturnType().init(getStaticContext());
                for (FunctionArgument argument : nativeMethod.getArguments()) {
                    argument.type.init(getStaticContext());
                }
                fm = tryToAdd(nativeMethod, true, getAllDeclaredMethods(), getCreatedContext());
            }
        }

        return fm;
    }

    public FunctionMethod originalMethod(String name, LinkedHashMap<String, Type> arguments, boolean constructor) {

        for (FunctionMethod method : constructor ? getConstructors() : getAllDeclaredMethods()) {
            if(constructor || name.equals(method.name)){
                ArrayList<Type> types = new ArrayList<>(arguments.values());
                for (int i = 0; i < method.getArguments().size(); i++) {
                    FunctionArgument fa = method.getArguments().get(i);
                    if(types.get(i).isCustomType()){
                        types.get(i).init(getCreatedContext());
                    }
                    if(fa.type.isCustomType()){
                        fa.type.init(getCreatedContext());
                    }
                    if(types.get(i).canBe(fa.type)){
                        return method;
                    }
                }
                if(arguments.isEmpty() && method.getArguments().isEmpty()){
                    return method;
                }
            }
        }
        throw new RLException("Method not found: " + name, Type.internal(getCreatedContext()), getCreatedContext());
    }

}

package org.restudios.relang.parser.ast.types.values.values.nativing;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
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
            FunctionMethod om = originalMethod(nativeMethod.getName(), nativeMethod.arguments);
            if(om == null){
                throw new RuntimeException("Original method "+nativeMethod.getName()+" not found");
            }
            if(om.isNative){
                nativeMethod.init(om);
            }
        }
    }

    @Override
    public ClassInstance instantiate(Context context, List<Type> types, Value... constructorArguments) {
        initStatic();
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
        return ci;
    }


    @Override
    public ArrayList<FunctionMethod> getParentMethods(boolean includeThis, boolean implementedOnly, boolean allowStatic) {
        ArrayList<FunctionMethod> result = super.getParentMethods(includeThis, implementedOnly, allowStatic);
        for (NativeMethod sllMethod : nativeClass.getNativeMethods()) {
            //noinspection DataFlowIssue
            result = tadd(sllMethod, allowStatic, result, getCreatedContext());
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
                fm = tadd(nativeMethod, true, getAllDeclaredMethods(), getCreatedContext());
            }
        }

        return fm;
    }

    public FunctionMethod originalMethod(String name, LinkedHashMap<String, Type> arguments) {
        for (FunctionMethod method : getAllDeclaredMethods()) {
            if(name.equals(method.name)){
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

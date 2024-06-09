package org.restudios.relang.parser.ast.types.values.values.sll;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class SLLClassInstance extends ClassInstance {
    public SLLClassInstance(String clazz, List<Type> types, Context parent) {
        super(clazz, types, parent);
        init();
    }

    public SLLClassInstance(RLClass clazz, List<Type> types, Context parent) {
        super(clazz, types, parent);
        init();
    }

    public void init(){
        for (FunctionMethod method : getRLClass().getAllDeclaredMethods()) {
            Context context = method.visibility.contains(Visibility.STATIC) ? getRLClass().getStaticContext() : this.getContext();
            method.getReturnType().init(context);
            for (FunctionArgument argument : method.getArguments()) {
                argument.type.init(context);
            }
        }
        getAvailableMethods().forEach(functionMethod -> functionMethod.getCustomTypes().addAll(getSubTypes()));
    }

    @Override
    public FunctionMethod findMethodFromNameAndArguments(Context context, String name, Value[] values) {
        ArrayList<FunctionMethod> mfs = getRLClass().getParentMethods(true, true, true);
        DynamicSLLClass dyn = (DynamicSLLClass) getRLClass();
        dyn.init();
        for (SLLMethod sllMethod : dyn.sllMethods) {
            RLClass.tadd(sllMethod, false, mfs, context);
        }
        return ClassInstance.findMethodFromNameAndArguments(name, values, mfs, context, this);
    }
    @SuppressWarnings("unused")
    public FunctionMethod originalMethod(String name, LinkedHashMap<String, Type> arguments) {
        for (FunctionMethod method : getRLClass().getAllDeclaredMethods()) {
            if(name.equals(method.name)){
                ArrayList<Type> types = new ArrayList<>(arguments.values());

                for (int i = 0; i < method.getArguments().size(); i++) {
                    FunctionArgument fa = method.getArguments().get(i);
                    if(types.get(i).isCustomType()){
                        types.get(i).init(getContext());
                    }
                    if(fa.type.isCustomType()){
                        fa.type.init(getContext());
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
        throw new RLException("Method not found: " + name, Type.internal(getContext()), getContext());
    }
}

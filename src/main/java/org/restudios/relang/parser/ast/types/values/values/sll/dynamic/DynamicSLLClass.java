package org.restudios.relang.parser.ast.types.values.values.sll.dynamic;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.ConstructorMethod;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLClassInstance;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLMethod;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLThread;

import java.util.ArrayList;
import java.util.List;

public class DynamicSLLClass extends RLClass {

    public static final String STRING = "str";
    public static final String OBJECT = "obj";
    public static final String ARRAY = "array";
    public static final String CAST = "CastUtils";
    public static final String RUNNABLE = "Runnable";
    public static final String THREAD = "Thread";
    public static final String ENUM = "enumeration";
    public static final String EXCEPTION = "Exception";
    public static final String STACK_TRACE_ELEMENT = "StackTraceElement";
    public static final String REFL_CLASS = "Class";
    public static final String REFL_VISIBILITY = "ObjectVisibility";
    public static final String REFL_CLASSTYPE = "ClassType";
    public static final String REFL_CLASSVARIABLE = "ClassVariable";
    public static final String REFL_CLASSMETHOD = "ClassMethod";
    public static final String REFL_PRIMITIVE = "PrimitiveType";
    public static final String REFL_TYPE = "Type";

    public ArrayList<SLLMethod> sllMethods = new ArrayList<>();
    private boolean methodsInitialized = false;

    public DynamicSLLClass(String name, ClassType classType, ArrayList<Visibility> visibility) {
        super(name, classType, visibility);
    }

    public DynamicSLLClass(Context context, String name, ClassType classType, ArrayList<CustomTypeValue> subTypes, RLClass extending, ArrayList<RLClass> implementing, ArrayList<Visibility> visibility, ArrayList<FunctionMethod> methods, ArrayList<ConstructorMethod> constructors, ArrayList<RLClass> subClasses, ArrayList<UnInitializedVariable> variables, ArrayList<FunctionMethod> operatorsOverloading) {
        super(context, name, classType, subTypes, extending, implementing, visibility, methods, constructors, subClasses, variables, operatorsOverloading);

    }

    public void init(){
        if(!methodsInitialized){
            methodsInitialized = true;
            sllMethods = DynamicValues.getForMe(this);
        }
    }

    @Override
    public void initializeStaticContext() {
        super.initializeStaticContext();
        init();
    }


    @Override
    public ClassInstance instantiate(Context context, List<Type> types, Value... constructorArguments) {
        loadClassData(context);
        initializeStaticContext();
        ClassInstance ci;
        if(getName().equals(DynamicSLLClass.ARRAY) && constructorArguments.length == 0 && (types.size() == 1 || types.isEmpty())){
            Type t = types.isEmpty() ? Type.clazz(DynamicSLLClass.OBJECT, context) : types.get(0);
            ci = new RLArray(t, context);
        }else if(getName().equals(DynamicSLLClass.THREAD)) {
            ci = new RLThread(this, types, context);
        } else {
            ci = new SLLClassInstance(this, types, context);
        }
        createdChild(ci);
        callConstructor(context, ci.getContext(), constructorArguments);
        init();
        return ci;
    }

    @Override
    public void createdChild(ClassInstance child) {
        super.createdChild(child);
        init();
    }

    @Override
    public ArrayList<FunctionMethod> getAllMethods(boolean includeThis, boolean implementedOnly, boolean allowStatic) {
        ArrayList<FunctionMethod> result = super.getAllMethods(includeThis, implementedOnly, allowStatic);
        init();
        for (FunctionMethod sllMethod : sllMethods) {
            result = tryToAdd(sllMethod, allowStatic, getAllDeclaredMethods(), getCreatedContext());
        }
        return result;
    }


    @Override
    public ArrayList<FunctionMethod> getStaticMethods() {
        ArrayList<FunctionMethod> fm = super.getStaticMethods();
        if(sllMethods.isEmpty()){
            sllMethods = DynamicValues.getForMe(this);
        }
        for (FunctionMethod sllMethod : sllMethods) {
            if(sllMethod.visibility.contains(Visibility.STATIC)){
                sllMethod.getReturnType().init(getStaticContext());
                for (FunctionArgument argument : sllMethod.getArguments()) {
                    argument.type.init(getStaticContext());
                }
                fm = tryToAdd(sllMethod, true, getAllDeclaredMethods(), getCreatedContext());
            }
        }
        return fm;
    }

}

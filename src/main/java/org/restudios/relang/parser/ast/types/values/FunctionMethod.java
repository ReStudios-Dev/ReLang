package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.BlockStatement;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.FunctionArgument;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.VoidValue;
import org.restudios.relang.parser.ast.types.values.values.sll.SLLClassInstance;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLStr;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.exceptions.ReturnExp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionMethod extends RLMethod {
    public final String name;
    public List<Visibility> visibility;
    public final BlockStatement code;
    public final boolean isAbstract;
    public final boolean isNative;
    public FunctionMethod(FunctionMethod original){
        this(original.getCustomTypes(), original.getArguments(), original.getReturnType(), original.name, original.visibility, original.code, original.isAbstract, original.isNative);
    }
    public FunctionMethod(List<CustomTypeValue> customTypes, List<FunctionArgument> arguments, Type returnType, String name, List<Visibility> visibility, BlockStatement code, boolean isAbstract, boolean isNative) {
        super(arguments, customTypes, returnType);
        this.name = name;
        this.visibility = visibility;
        this.code = code;
        this.isAbstract = isAbstract;
        this.isNative = isNative;
    }

    @Override
    public Value handle(Context context, Context callContext) {
        try {
            if(code == null){
                throw new RLException("Method "+name+" not implemented", Type.internal(context), context);
            }
            String currentMethod = context.getCurrentMethod();
            context.setCurrentMethod(this.name);
            code.execute(context);
            context.setCurrentMethod(currentMethod);
        } catch (ReturnExp e) {
            return e.value;
        }
        return new VoidValue();
    }

    public ArrayList<Type> getArgumentTypes() {
        ArrayList<Type> result = new ArrayList<>();
        for (FunctionArgument argument : getArguments()) {
            result.add(argument.type);
        }

        return result;
    }

    @Override
    public String toString() {
        String vis = visibility.stream().map(visibility1 -> visibility1.name().toLowerCase()).collect(Collectors.joining(" "));
        return vis+(isAbstract ? " abstract" : "")+" "+name+"()";
    }

    public boolean checkOverridden(FunctionMethod method) {
        if (method.name.equals(name)) {
            if (method.getReturnType().like(getReturnType())) {
                if (method.getArguments().size() != getArguments().size()) return false;
                for (int i = 0; i < method.getArguments().size(); i++) {
                    FunctionArgument fa = method.getArguments().get(i);
                    Type ar = getArgumentTypes().get(i);
                    if (!fa.type.like(ar)) {
                        return false;
                    }
                }
                if (!method.visibility.equals(visibility)) {

                    List<Visibility> nw = new ArrayList<>(method.visibility);
                    nw.remove(Visibility.OVERRIDE);
                    ArrayList<Visibility> fw = new ArrayList<>(visibility);
                    fw.remove(Visibility.FINAL);
                    return nw.equals(fw);
                }
                return true;

            }
        }

        return false;
    }

    public boolean same(FunctionMethod functionMethod) {
        if((functionMethod.visibility.contains(Visibility.STATIC) && !visibility.contains(Visibility.STATIC)) ||
                (visibility.contains(Visibility.STATIC) && !functionMethod.visibility.contains(Visibility.STATIC)))return false;
        if(name.equals(functionMethod.name)){
            if(getArguments().size() == functionMethod.getArguments().size()){

                ArrayList<Type> me = getArgumentTypes();
                ArrayList<Type> he = functionMethod.getArgumentTypes();
                for (int i = 0; i < me.size(); i++) {
                    if(!me.get(i).canBe(he.get(i))){
                        return false;
                    }
                }
                return true;

            }
        }

        return false;
    }

    public boolean canBeExecuted(Value[] values, Context context) {
        List<FunctionArgument> args = getArguments();
        if(args.size() != values.length) return false;
        for (int i = 0; i < values.length; i++) {
            if(values[i].getRLClass() != null) {
                args.get(i).type.init(values[i].getRLClass().getCreatedContext());
            } else {
                args.get(i).type.init(context);
            }
            if(!values[i].type().canBe(args.get(i).type))return false;
        }
        return  true;
    }

    public Value getReflectionClass(Context context){
        SLLClassInstance classInstance = (SLLClassInstance) context.getClass(DynamicSLLClass.REFL_CLASSMETHOD).instantiate(context, new ArrayList<>());

        classInstance.getContext().getVariable("name").setValueForce(new RLStr(this.name, context));
        classInstance.getContext().getVariable("visibility").setValueForce(Visibility.getReflectionVisibility(this.visibility, context));
        classInstance.getContext().getVariable("isStatic").setValueForce(Value.value(this.visibility.contains(Visibility.STATIC)));
        classInstance.getContext().getVariable("isFinal").setValueForce(Value.value(this.visibility.contains(Visibility.FINAL)));
        return classInstance;
    }
    @SuppressWarnings("unused")
    public Value getReflectionArguments(Context context){
        RLArray array = new RLArray(Type.clazz(DynamicSLLClass.REFL_TYPE, context), context);
        for (Type argumentType : this.getArgumentTypes()) {
            array.add(argumentType.getReflectionClass(context));
        }
        return array;
    }
}

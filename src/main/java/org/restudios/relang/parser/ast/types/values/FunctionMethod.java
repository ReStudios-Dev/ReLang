package org.restudios.relang.parser.ast.types.values;

import org.restudios.relang.modules.Module;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.nodes.extra.AnnotationDefinition;
import org.restudios.relang.parser.ast.types.nodes.extra.LoadedAnnotation;
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
import java.util.Objects;
import java.util.stream.Collectors;

public class FunctionMethod extends RLMethod {
    public final String name;
    public List<Visibility> visibility;
    public List<LoadedAnnotation> annotations;
    public final BlockStatement code;
    public final boolean isAbstract;
    public final boolean isNative;
    public FunctionMethod(FunctionMethod original){
        this(original.getCustomTypes(), original.getArguments(), original.getReturnType(), original.name, original.visibility, original.code, original.isAbstract, original.isNative, original.annotations);
    }
    public FunctionMethod(List<CustomTypeValue> customTypes, List<FunctionArgument> arguments, Type returnType, String name, List<Visibility> visibility, BlockStatement code, boolean isAbstract, boolean isNative, List<LoadedAnnotation> annotations) {
        super(arguments, customTypes, returnType);
        this.name = name;
        this.visibility = visibility;
        this.code = code;
        this.isAbstract = isAbstract;
        this.annotations = annotations;
        this.isNative = isNative;
    }

    @Override
    public Value handle(Context context, Context callContext) {
        try {
            if(code == null){
                for (Module module : context.getModuleRegistry()) {
                    Value val = module.methodNotImplemented(context.thisClass(), context, this);
                    if(val != null){
                        return val;
                    }
                }
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
        String vis = visibility.stream().map(visibility1 -> visibility1.name().toLowerCase()).filter(s -> !s.trim().isEmpty()).collect(Collectors.joining(" "));
        String args = getArguments().stream().map(FunctionArgument::toString).collect(Collectors.joining(", "));
        List<String> parts = new ArrayList<>();
        if(!vis.trim().isEmpty()) parts.add(vis);
        if(isAbstract) parts.add("abstract");
        parts.add(name.trim()+"("+args+")");
        return String.join(" ", parts);
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
            try {
                CastExpression.cast(args.get(i).type, values[i], context);
            } catch (Exception e) {
                return false;
            }
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

    public boolean checkForArguments(FunctionMethod that) {
        if (this == that) return true;
        if(!Objects.equals(name, that.name)) return false;
        List<Type> myArgs = getArgumentTypes();
        List<Type> oArgs = that.getArgumentTypes();
        if(myArgs.size() != oArgs.size()) return false;
        for (int i = 0; i < myArgs.size(); i++) {
            if(myArgs.get(i).clazz == null && myArgs.get(i).primitive == null) return false;
            if(oArgs.get(i).clazz == null && oArgs.get(i).primitive == null) return false;
            if(!myArgs.get(i).canBe(oArgs.get(i))) return false;
        }
        return getReturnType().canBe(that.getReturnType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public List<LoadedAnnotation> getAnnotations() {
        return annotations;
    }
    public boolean isAnnotated(RLClass reflectionClass) {
        for (LoadedAnnotation annotation : annotations) {
            if(annotation.ci.getRLClass().check(reflectionClass)){
                return true;
            }
        }
        return false;
    }

    public LoadedAnnotation getAnnotation(RLClass reflectionClass) {
        for (LoadedAnnotation annotation : annotations) {
            if(annotation.ci.getRLClass().check(reflectionClass)){
                return annotation;
            }
        }
        return null;
    }
}

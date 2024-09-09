package org.restudios.relang.parser.ast.types.nodes;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.ast.types.Node;
import org.restudios.relang.parser.ast.types.Primitives;
import org.restudios.relang.parser.ast.types.nodes.expressions.CastExpression;
import org.restudios.relang.parser.ast.types.nodes.expressions.IdentifierExpression;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.*;
import org.restudios.relang.parser.ast.types.values.values.sll.classes.RLArray;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Type extends Node {
    public Expression type;
    public RLClass clazz;
    public boolean isPrimitive;
    public Primitives primitive;
    public final List<Type> subTypes;
    private boolean isInstance;
    private Type(Token token, ArrayList<Type> subTypes) {
        super(token);
        this.subTypes = subTypes;
    }

    public Type(Token token, Primitives primitive) {
        this(token, new ArrayList<>());
        this.primitive = primitive;
        isPrimitive = true;
    }
    public Type(Token token, ArrayList<Type> subTypes, RLClass clazz) {
        this(token, subTypes);
        isPrimitive = false;
        this.clazz = clazz;
        type = null;
    }

    public Type(Token token, Expression type, RLClass clazz, boolean isPrimitive, Primitives primitive, List<Type> subTypes) {
        super(token);
        this.type = type;
        this.clazz = clazz;
        this.isPrimitive = isPrimitive;
        this.primitive = primitive;
        this.subTypes = subTypes;
    }

    public boolean isCustomType() {
        return !isPrimitive || primitive == Primitives.TYPE;
    }
    public Type(Token token, ArrayList<Type> subTypes, Expression type) {
        this(token, subTypes);
        isPrimitive = false;
        this.type = type;
        clazz = null;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Type clone(){
        return new Type(token, type, clazz, isPrimitive, primitive, subTypes.stream().map(Type::clone).collect(Collectors.toList()));
    }

    public boolean isInstance() {
        return isInstance;
    }

    public Type setInstance(boolean instance) {
        isInstance = instance;
        return this;
    }

    public static Type clazz(RLClass clazz){
        return new Type(null, clazz.subTypes(), clazz);
    }
    public static Type clazz(String name, Context context){
        RLClass c = context.getClass(name);
        if(c == null){
            throw new RLException("Class "+name+" not found", Type.internal(context), context);
        }
        c.loadClassData(context);
        return clazz(c);
    }
    public static Type nullPointer(Context context){
        return clazz("NullPointerException", context);
    }
    public static Type obj(Context context){
        return clazz(DynamicSLLClass.OBJECT, context);
    }
    public static Type castException(Context context){
        return clazz("CastException", context);
    }
    public static Type arrayOutOfBounds(Context context){
        return clazz("ArrayOutOfBoundsException", context);
    }
    public static Type numberFormat(Context context){
        return clazz("NumberFormatException", context);
    }
    public static Type internal(Context context){
        return clazz("InternalException", context);
    }
    public static Type illegalArgument(Context context){
        return clazz("IllegalArgumentException", context);
    }
    public static Type primitive(Primitives primitives){
        return new Type(null, primitives);
    }
    public static Type integer(){
        return primitive(Primitives.INTEGER);
    }
    public static List<Type> types(Type... types){
        return new ArrayList<>(Arrays.asList(types));
    }

    public boolean canBe(Type type){
        return canBe(type, false);
    }
    public boolean canBe(Type type, boolean checkOverloads){
        if(type.clazz != null && type.clazz.getName().equals(DynamicSLLClass.OBJECT)) return true;

        if(isRunnable()){
            if(type.clazz != null){
                if(type.clazz.isInterface()){
                    if(type.clazz.getAbstractMethods().size() == 1){

                        //FunctionMethod fm = type.clazz.getAbstractMethods().get(0);
                        // TODO 06.09.2024 14:34 Check lambda
                        return true;

                    }
                }
            }
        }

        if(isPrimitive){
            if(primitive == Primitives.NULL){
                return true;
            }
            if(!type.isPrimitive)return false;
            if(this.primitive == type.primitive)return true;
            if(primitive == null)return false;
            switch (primitive){
                case INTEGER:
                case CHAR:
                case FLOAT:
                    switch (type.primitive){
                        case INTEGER:
                        case CHAR:
                        case FLOAT:
                            return true;
                        default:
                            return false;
                    }
            }
            return false;
        }else{

            if(checkOverloads){
                if(clazz != null && clazz.findExplicitOperator(type) != null) return true;
                if(type.clazz != null && type.clazz.findImplicitOperator(this) != null) return true;
            }

            if(type.isPrimitive)return false;
            return clazz
                    .isAssignableFrom(
                    type.clazz);
        }
    }
    public String displayName(){
        if(isPrimitive){
            return primitive.name().toLowerCase();
        }else{
            if(clazz == null && type == null){
                return "unk";
            }
            return (clazz == null ? type.token.string : clazz.getName())+stringSubTypes();
        }
    }
    public String stringSubTypes(){
        String result = "";

        if(!subTypes.isEmpty()){
            result += "<";
            result += String.join(", ", subTypes.stream().map(Type::displayName).collect(Collectors.toCollection(ArrayList::new)));
            result += ">";
        }

        return result;
    }

    public boolean like(Type o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(o.isPrimitive != isPrimitive) return false;
        if(isPrimitive){
            return primitive == o.primitive;
        }else{
            return clazz.check(o.clazz);
        }
    }

    @Override
    public int hashCode() {
        if(isPrimitive){
            return Objects.hash(primitive);
        }else{
            return Objects.hash(clazz);
        }
    }

    public void init(Context context) {
        if(clazz != null) return;
        if (type == null) return;
        Value v = type.eval(context);
        if (v instanceof Variable) {
            Variable variable = (Variable) v;
            ClassInstance clazz = context.thisClass();
            if (clazz == null) {
                return;
                //throw new RLException("Cannot to use custom types outside a class", type.token.getFrom(), Type.internal(context));;
            }
            String name = variable.getName();
            for (CustomTypeValue subType : clazz.getSubTypes()) {
                if (subType.name.equals(name)) {
                    this.clazz = subType.value.clazz;
                    this.type = subType.value.type;
                    this.primitive = subType.value.primitive;
                    this.isPrimitive = subType.value.isPrimitive;
                    return;
                }
            }
        }
        if (v instanceof TypeValue) {
            TypeValue tv = (TypeValue) v;
            Type t = tv.value;
            t.init(context);
            if (t.isCustomType()) {
                v = t.clazz;
                isPrimitive = false;
            } else {
                this.clazz = null;
                this.type = null;
                this.primitive = t.primitive;
                this.isPrimitive = true;
                return;
            }
        }
        v = v.finalExpression();
        if (v instanceof NullValue) {
            clazz = null;
            type = null;
            isPrimitive = true;
            primitive = Primitives.NULL;
            return;
        }
        if (!(v instanceof RLClass)) {
            throw new RLException("Variable type must be a primitive type or a class", Type.internal(context), context);
        }
        clazz = (RLClass) v;
        type = null;

    }

    @Override
    public String toString() {
        return displayName();
    }

    public void initClassOrType(Context context) {
        if((primitive == Primitives.NULL || primitive == null) && clazz == null){
            if(token != null && context.containsClass(token.string)){
                clazz = context.getClass(token.string);
                type = new IdentifierExpression(token, token.string);
                isPrimitive = false;
                primitive = Primitives.NULL;
            }else{
                ClassInstance ci = context.thisClass();
                if(ci != null){
                    for (CustomTypeValue subType : ci.getSubTypes()) {
                        if(token == null) continue;
                        if(subType.name.equals(token.string)){
                            clazz = subType.value.clazz;
                            type = subType.value.type;
                            isPrimitive = subType.value.isPrimitive;
                            primitive = subType.value.primitive;
                        }
                    }
                }
            }
        }
    }
    public void initClassOrType(AnalyzerContext context) {
        if((primitive == Primitives.NULL || primitive == null) && clazz == null){
            if(token != null && context.containsClass(token.string)){
                clazz = context.getClass(token.string);
                type = new IdentifierExpression(token, token.string);
                isPrimitive = false;
                primitive = null;
            }else{
                RLClass ci = context.handlingClass;
                if(ci != null){
                    for (CustomTypeValue subType : ci.getSubTypes()) {
                        if(token == null) continue;
                        if(subType.name.equals(token.string)){
                            clazz = subType.value.clazz;
                            type = subType.value.type;
                            isPrimitive = true;
                            primitive = Primitives.TYPE;
                        }
                    }
                }
            }
        }
    }
    public void initClassOrType(AnalyzerContext context, Type handling) {
        if((primitive == Primitives.NULL || primitive == null) && clazz == null){
            if(token != null && context.containsClass(token.string)){
                clazz = context.getClass(token.string);
                type = new IdentifierExpression(token, token.string);
                isPrimitive = false;
                primitive = null;
            }else {
                Type ci = handling;
                for (int i = 0; i < ci.clazz.getSubTypes().size(); i++) {
                    if (token == null) continue;
                    String name = ci.clazz.getSubTypes().get(i).getName();
                    Type value = handling.subTypes.get(i);

                    if (name.equals(token.string)) {
                        clazz = value.clazz;
                        type = value.type;
                        isPrimitive = true;
                        primitive = Primitives.TYPE;
                    }
                }

            }
        }
    }

    public Value getReflectionClass(Context context) {
        init(context);
        initClassOrType(context);
        ClassInstance ci = context.getClass(DynamicSLLClass.REFL_TYPE).instantiate(context, new ArrayList<>());
        if(this.clazz != null){
            ci.getContext().getVariable("clazz").setValueForce(this.clazz.getReflectionClass(context));
            ci.getContext().getVariable("primitive").setValueForce(new NullValue());
        }else{
            ci.getContext().getVariable("clazz").setValueForce(new NullValue());
            try {
                ci.getContext().getVariable("primitive").setValueForce(primitive.getReflectionClass(context));
            } catch (Exception e) {
                ci.getContext().getVariable("primitive").setValueForce(Primitives.NULL.getReflectionClass(context));

            }
        }
        RLArray arr = new RLArray(Type.clazz(DynamicSLLClass.REFL_TYPE, context), context);
        List<Value> subtypes = this.subTypes.stream().map(t -> t.getReflectionClass(context)).collect(Collectors.toList());
        for (Value subtype : subtypes) {
            arr.add(subtype);
        }
        ci.getContext().getVariable("subtypes").setValueForce(arr);
        return ci;
    }

    public boolean tokenEquality(Type returnType) {
        if(this.token != null && returnType.token != null){
            return this.token.string.equals(returnType.token.string);
        }
        return this.token == returnType.token;
    }

    public Type firstTypeOrVoid() {
        if(subTypes.isEmpty()) return Primitives.VOID.type();
        return subTypes.get(0);
    }

    public boolean isRunnable() {
        return this.clazz != null && this.clazz.getName().equals(DynamicSLLClass.RUNNABLE);
    }

    public boolean isString() {
        return this.isCustomType() && this.clazz.getName().equals(DynamicSLLClass.STRING);
    }

    public boolean isArray() {
        return this.clazz != null && this.clazz.getName().equals(DynamicSLLClass.ARRAY);
    }
}

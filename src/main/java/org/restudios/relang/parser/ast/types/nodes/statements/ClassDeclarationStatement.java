package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.DeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.classes.ClassBlock;
import org.restudios.relang.parser.ast.types.nodes.statements.classes.EnumClassBlock;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.ConstructorMethod;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.nativing.DynamicNativeClass;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicValues;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;
import org.restudios.relang.parser.utils.NativeClass;

import java.util.ArrayList;

public class ClassDeclarationStatement extends DeclarationStatement {
    public final String name;
    public final ArrayList<String> subTypes;

    public final ClassType type;
    public final ArrayList<Visibility> visibilities;
    public final ClassBlock body;
    public final Expression extending;
    public final ArrayList<Expression> implementations;

    public ClassDeclarationStatement(Token token, String name, ArrayList<String> subTypes, ClassType type, ArrayList<Visibility> visibilities, ClassBlock body, Expression extending, ArrayList<Expression> implementations) {
        super(token);
        this.name = name;
        this.subTypes = subTypes;
        this.type = type;
        this.visibilities = visibilities;
        this.body = body;
        this.extending = extending;
        this.implementations = implementations;
    }

    public RLClass clazz(Context context){

        RLClass clazz = null;
        for (NativeClass nativeClass : context.getNativeClasses()) {
            if (nativeClass.getName().equals(name)) {
                clazz = new DynamicNativeClass(nativeClass, name, type, visibilities);
            }
        }
        if(clazz == null) {
            if (DynamicValues.sllClasses.contains(name)) {
                clazz = new DynamicSLLClass(name, type, visibilities);
            } else {
                if (type == ClassType.ENUM) {
                    clazz = new RLEnumClass(name, type, visibilities, ((EnumClassBlock) body).createValues());
                } else {
                    clazz = new RLClass(name, type, visibilities);
                }
            }
        }
        clazz.setDeclarationStatement(this);
        context.putClass(clazz);
        return clazz;
    }
    public void doLoad(RLClass clazz, Context context){
        Value ext = null;
        if(extending != null){
            ext = extending.eval(context);
        }else if(!name.equals(DynamicSLLClass.OBJECT) && !name.equals(DynamicSLLClass.ENUM) && (type == ClassType.ENUM)){
            ext = context.getClass(DynamicSLLClass.ENUM);
        }else if(!name.equals(DynamicSLLClass.OBJECT) && (type != ClassType.INTERFACE)){
            ext = context.getClass(DynamicSLLClass.OBJECT);
        }
        if(!(ext instanceof RLClass) && ext != null){
            throw new RLException("Class can extend class only", Type.internal(context), context);
        }
        ArrayList<RLClass> implementations = new ArrayList<>();
        for (Expression implementation : this.implementations) {
            Value im = implementation.eval(context);
            if(!(im instanceof RLClass)){
                throw new RLException("Class can implement class only", Type.internal(context), context);
            }
            RLClass cls = (RLClass) im;
            cls.loadClassData(context);
            implementations.add(cls);
        }
        RLClass extended = (RLClass)ext;
        if(extended != null){
            extended.loadClassData(context);
        }
        ArrayList<UnInitializedVariable> variables = new ArrayList<>();
        for (VariableDeclarationStatement variable : body.variables) {
            variables.add(new UnInitializedVariable(variable.type, variable.variable, variable.value, variable.visibilities));
        }
        ArrayList<FunctionMethod> methods = new ArrayList<>();
        for (MethodDeclarationStatement method : body.methods) {
            methods.add(method.method());
        }
        ArrayList<ConstructorMethod> constructors = new ArrayList<>();
        for (ConstructorDeclarationStatement constructor : body.constructors) {
            constructors.add((ConstructorMethod) constructor.method());
        }
        ArrayList<RLClass> subClasses = new ArrayList<>();
        for (ClassDeclarationStatement aClass : body.classes) {
            RLClass cls = aClass.clazz(context);
            cls.loadClassData(context);
            subClasses.add(cls);
        }
        ArrayList<FunctionMethod> operatorsOverloading = new ArrayList<>();
        for (OperatorOverloadStatement constructor : body.operators) {
            operatorsOverloading.add(constructor.method(context));
        }
        ArrayList<CustomTypeValue> types = new ArrayList<>();
        for (String subType : this.subTypes) {
            context.getClass(DynamicSLLClass.OBJECT).loadClassData(context);
            types.add(new CustomTypeValue(subType, context.getClass(DynamicSLLClass.OBJECT).type()));
        }
        clazz.loadClassData(context, types, extended, implementations, methods, constructors, subClasses, variables, operatorsOverloading);
        clazz.validate(context);
    }
    RLClass clazz;
    @Override
    public void execute(Context context) {
        clazz = clazz(context);
        doLoad(clazz, context);
    }

    @Override
    public void prepare(Context context) {
        clazz = clazz(context);
    }

    @Override
    public void validate(Context context) {
        doLoad(clazz, context);
    }

    @Override
    public String toString() {
        return "class "+this.name+" -> \n"+body.toString();
    }
}

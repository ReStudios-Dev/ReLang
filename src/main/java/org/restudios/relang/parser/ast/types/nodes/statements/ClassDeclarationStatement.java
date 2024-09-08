package org.restudios.relang.parser.ast.types.nodes.statements;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.ClassType;
import org.restudios.relang.parser.ast.types.Visibility;
import org.restudios.relang.parser.ast.types.nodes.DeclarationStatement;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.extra.AnnotationDefinition;
import org.restudios.relang.parser.ast.types.nodes.extra.LoadedAnnotation;
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
import java.util.List;
import java.util.stream.Collectors;

public class ClassDeclarationStatement extends DeclarationStatement {
    public final String name;
    public final ArrayList<String> subTypes;

    public final ClassType type;
    public final ArrayList<Visibility> visibilities;
    public final ClassBlock body;
    public final Expression extending;
    public final ArrayList<Expression> implementations;
    public final List<AnnotationDefinition> annotations;
    public final boolean isNative;

    public ClassDeclarationStatement(Token token, String name, boolean isNative, ArrayList<String> subTypes, ClassType type, ArrayList<Visibility> visibilities, ClassBlock body, Expression extending, ArrayList<Expression> implementations, List<AnnotationDefinition> annotations) {
        super(token);
        this.name = name;
        this.subTypes = subTypes;
        this.type = type;
        this.visibilities = visibilities;
        this.body = body;
        this.extending = extending;
        this.implementations = implementations;
        this.annotations = annotations;
        this.isNative = isNative;
    }

    @Override
    public void analyze(AnalyzerContext context) {
        if(extending != null){
            Type ext = extending.predictType(context);
            if(!ext.isCustomType()) throw new AnalyzerError("Cannot extend non class", extending.token);
            RLClass ec = ext.clazz;
            if(type == ClassType.INTERFACE && !ec.isInterface()) {
                throw new AnalyzerError("Interfaces cannot extend non interface", extending.token);
            }
            if(type != ClassType.INTERFACE && ec.isInterface()){
                throw new AnalyzerError("Regular classes cannot extend interfaces", extending.token);
            }
        }
        for (Expression implementation : implementations) {
            if(type == ClassType.INTERFACE) throw new AnalyzerError("Interfaces cannot implement other classes", implementation.token);
            Type imp = implementation.predictType(context);
            if(!imp.isCustomType()) throw new AnalyzerError("Cannot implement non class", implementation.token);
            if(!imp.clazz.isInterface()) throw new AnalyzerError("Cannot implement non interface", implementation.token);
        }

        AnalyzerContext initContext = context.create();
        initContext.setHandlingClass(context.getClass(name));
        //initContext.putVariable("this", context.getClass(name).type());
        for (ClassDeclarationStatement aClass : body.classes) {
            aClass.analyze(initContext);
        }
        for (MethodDeclarationStatement method : body.methods) {
            method.analyze(initContext);
        }
        for (ConstructorDeclarationStatement constructor : body.constructors) {
            constructor.analyze(initContext);
        }
        for (VariableDeclarationStatement variable : body.variables) {
            variable.analyze(initContext);
        }
        for (OperatorOverloadStatement operator : body.operators) {
            operator.analyze(initContext);
        }

    }

    @Override
    public Type predictType(AnalyzerContext c) {
        return super.predictType(c);
    }

    @Override
    public void analyzerPrepare(AnalyzerContext context) {
    }

    public RLClass clazz(Context context){

        RLClass clazz = null;
        for (NativeClass nativeClass : context.getNativeClasses()) {
            if (nativeClass.getName().equals(name)) {
                clazz = new DynamicNativeClass(nativeClass, name, type, visibilities);
            }
        }
        if(clazz == null) {
            if(this.isNative){
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
        if(clazz.isLoaded()) return;
        clazz.setLoaded();
        for (AnnotationDefinition annotation : annotations) {
            clazz.getAnnotations().add(annotation.eval(context));
        }
        if(clazz.getSuperClass() != null) clazz.getSuperClass().getDeclaration().doLoad(clazz.getSuperClass(), context);
        for (RLClass superInterface : clazz.getSuperInterfaces()) {
            superInterface.getDeclaration().doLoad(superInterface, context);
        }
        for (RLClass superInterface : clazz.getSubClasses()) {
            superInterface.getDeclaration().doLoad(superInterface, context);
        }
        context.getClass(DynamicSLLClass.OBJECT).getDeclaration().doLoad(context.getClass(DynamicSLLClass.OBJECT), context);
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

    public void preload(RLClass clazz, Context context) {
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
            cls.preloadClassData(context);
            implementations.add(cls);
        }
        RLClass extended = (RLClass)ext;
        if(extended != null){
            extended.preloadClassData(context);
        }
        ArrayList<UnInitializedVariable> variables = new ArrayList<>();
        for (VariableDeclarationStatement variable : body.variables) {
            variables.add(new UnInitializedVariable(variable.type, variable.variable, variable.value, variable.visibilities));
        }
        ArrayList<FunctionMethod> methods = new ArrayList<>();
        for (MethodDeclarationStatement method : body.methods) {
            methods.add(method.method(context));
        }
        ArrayList<ConstructorMethod> constructors = new ArrayList<>();
        for (ConstructorDeclarationStatement constructor : body.constructors) {
            constructors.add((ConstructorMethod) constructor.method(context));
        }
        ArrayList<RLClass> subClasses = new ArrayList<>();
        for (ClassDeclarationStatement aClass : body.classes) {
            RLClass cls = aClass.clazz(context);
            cls.preloadClassData(context);
            subClasses.add(cls);
        }
        ArrayList<FunctionMethod> operatorsOverloading = new ArrayList<>();
        for (OperatorOverloadStatement constructor : body.operators) {
            operatorsOverloading.add(constructor.method(context));
        }
        ArrayList<CustomTypeValue> types = new ArrayList<>();
        for (String subType : this.subTypes) {
            context.getClass(DynamicSLLClass.OBJECT).preloadClassData(context);
            types.add(new CustomTypeValue(subType, context.getClass(DynamicSLLClass.OBJECT).type()));
        }
        clazz.loadClassData(context, types, extended, implementations, methods, constructors, subClasses, variables, operatorsOverloading);

    }
}

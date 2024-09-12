package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.analyzer.AnalyzerContext;
import org.restudios.relang.parser.analyzer.AnalyzerError;
import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.nodes.statements.ClassDeclarationStatement;
import org.restudios.relang.parser.ast.types.values.*;
import org.restudios.relang.parser.ast.types.values.values.CustomTypeValue;
import org.restudios.relang.parser.ast.types.values.values.EnumItemValue;
import org.restudios.relang.parser.ast.types.values.values.TypeValue;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.exceptions.RLException;
import org.restudios.relang.parser.tokens.Token;

import java.util.Map;

public class IdentifierExpression extends Expression {
    public final String value;
    public final boolean variable;
    public IdentifierExpression(Token token, String value) {
        super(token);
        this.value = value;
        variable = true;
    }

    @Override
    public Value eval(Context context) {
        return find(context, true);
    }

    @Override
    public Type predictType(AnalyzerContext context) {
        Type t = context.getVariable(value);
        if(value.equals("this")) {
            Type ta = context.getHandlingClass().type();
            ta.setInstance(true);
            return ta;
        }
        if(t != null){
            t.initClassOrType(context);
            t.setInstance(true);
            return t;
        }
        RLClass cds = context.getClass(value);
        if(cds != null){
            return cds.type();
        }
        if(context.getHandlingClass() != null){
            for (UnInitializedVariable unInitializedVariable : context.getHandlingClass().getVariables()) {
                if(unInitializedVariable.getName().equals(value)) {
                    t = unInitializedVariable.getType();
                    t.initClassOrType(context);
                    t.setInstance(true);
                    return t;
                }
            }
            for (CustomTypeValue subType : context.getHandlingClass().getSubTypes()) {
                if(subType.name.equals(value)){
                    return new TypeValue(subType.name, subType.value, context.getHandlingClass()).type();
                }
            }
        }
        throw new AnalyzerError("Method, class or variable named \""+value+"\" not found", token);
    }

    public Value find(Context context, boolean includeVariables){
        if(includeVariables && context.containsVariable(value)){
            return context.getVariable(value);
        }
        if(context.containsClass(value)){
            return context.getClass(value);
        }
        ClassInstance ci = context.thisClass();
        if(ci != null){
            for (CustomTypeValue subType : ci.getSubTypes()) {
                if(subType.name.equals(value)){
                    return new TypeValue(subType.name, subType.value, ci.getRLClass());
                }
            }
        }
        for (Map.Entry<EnumItemValue, RLEnumClass> en : context.getEnumerations().entrySet()) {
            if(en.getKey().name().equals(value)){
                return en.getKey();
            }
        }
        throw new RLException("Could not find class or variable "+value, Type.internal(context), context);
    }

    public Value fromClassInstance(ClassInstance l) {
        return eval(l.getContext());
    }

    @Override
    public String toString() {
        return value;
    }
}

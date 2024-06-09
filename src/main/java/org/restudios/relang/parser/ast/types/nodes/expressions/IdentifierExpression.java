package org.restudios.relang.parser.ast.types.nodes.expressions;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.RLEnumClass;
import org.restudios.relang.parser.ast.types.values.Context;
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
                    return new TypeValue(subType.value);
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
}

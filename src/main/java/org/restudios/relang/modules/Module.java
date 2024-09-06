package org.restudios.relang.modules;

import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.Value;

public class Module {
    public void onClassInstantiate(ClassInstance instance, Context context, Value... constructorArguments){

    }
    public Value methodNotImplemented(ClassInstance instance, Context context, FunctionMethod method){
        return null;
    }
}

package org.restudios.relang.parser.ast.types.values.values.sll.dynamic;

import org.restudios.relang.parser.ast.types.nodes.Type;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.FunctionMethod;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.utils.NativeMethodArguments;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DynamicMethod extends FunctionMethod {
    private final String name;
    public final boolean staticMethod;
    public final LinkedHashMap<String, Type> arguments;
    private final Function<NativeMethodArguments, Value> handler;

    public DynamicMethod(String name, boolean staticMethod, LinkedHashMap<String, Type> arguments, Function<NativeMethodArguments, Value> handler, ClassInstance clazz) {
        super(clazz.getRLClass().findRawMethod(name, arguments, clazz.getContext()));
        this.staticMethod = staticMethod;
        this.name = name;
        this.arguments = arguments;
        this.handler = handler;
    }

    public Value execute(NativeMethodArguments arguments) {
        return handler.apply(arguments);
    }

    @Override
    public Value execute(Context context, Context callContext, Value... values) {
        NativeMethodArguments args = new NativeMethodArguments();
        ArrayList<String> names = new ArrayList<>(arguments.keySet());
        for (int i = 0; i < names.size(); i++) {
            args.getValues().put(names.get(i), values[i]);
        }
        return execute(args);
    }

    public String getName() {
        return name;
    }

    @Override
    public Value handle(Context context, Context callContext) {return null;}

    public LinkedHashMap<String, Type> getAArguments() {
        return arguments;
    }
}

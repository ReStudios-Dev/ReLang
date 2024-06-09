package org.restudios.relang.parser.utils;

import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.Value;
import org.restudios.relang.parser.ast.types.values.values.sll.dynamic.DynamicSLLClass;

import java.util.ArrayList;
@SuppressWarnings("unused")
public class RLStackTraceElement {
    private final String source;
    private final int line;
    private final int col;
    private final int position;
    private final String method;

    public RLStackTraceElement(String source, int line, int col, int position, String method) {
        this.source = source;
        this.line = line;
        this.col = col;
        this.position = position;
        this.method = method;
    }

    public String getSource() {
        return source;
    }

    public int getLine() {
        return line;
    }

    public int getCol() {
        return col;
    }

    public int getPosition() {
        return position;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return source+":"+line+":"+col+"@"+method;
    }

    public ClassInstance instance(Context context){
        RLClass clazz = context.getClass(DynamicSLLClass.STACK_TRACE_ELEMENT);
        clazz.loadClassData(context);
        return clazz.instantiate(context, new ArrayList<>(), Value.value(source, context), Value.value(line), Value.value(col), Value.value(method, context));
    }
}

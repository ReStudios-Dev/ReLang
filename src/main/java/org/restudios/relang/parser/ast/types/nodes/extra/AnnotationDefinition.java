package org.restudios.relang.parser.ast.types.nodes.extra;

import org.restudios.relang.parser.ast.types.nodes.Expression;
import org.restudios.relang.parser.ast.types.values.ClassInstance;
import org.restudios.relang.parser.ast.types.values.Context;
import org.restudios.relang.parser.ast.types.values.RLClass;
import org.restudios.relang.parser.ast.types.values.values.Value;

import java.util.List;

public class AnnotationDefinition {

    private final Expression annotation;
    private final List<Expression> expressions;

    public AnnotationDefinition(Expression annotation, List<Expression> expressions) {
        this.annotation = annotation;
        this.expressions = expressions;
    }

    public Expression getName() {
        return annotation;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AnnotationDefinition{");
        sb.append("annotation=").append(annotation);
        sb.append(", expressions=").append(expressions);
        sb.append('}');
        return sb.toString();
    }

    public LoadedAnnotation eval(Context context) {
        Value v = getName().eval(context).finalExpression();
        if(!(v instanceof RLClass)){
            throw new RuntimeException("Annotation can be only regular class");
        }
        RLClass cl = (RLClass) v;
        Value[] args = new Value[getExpressions().size()];
        for (int i = 0; i < args.length; i++) {
            args[i] = getExpressions().get(i).eval(context).finalExpression();
        }
        ClassInstance ci = cl.instantiate(context, args);
        return new LoadedAnnotation(ci);
    }
}

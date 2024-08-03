package org.restudios.relang.parser.ast.types.nodes.extra;

import org.restudios.relang.parser.ast.types.nodes.Expression;

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
}

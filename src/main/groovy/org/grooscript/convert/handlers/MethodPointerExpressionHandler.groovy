package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.MethodPointerExpression

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 08/03/14
 */
class MethodPointerExpressionHandler extends BaseHandler {

    void handle(MethodPointerExpression expression) {
        if (factory.isThis(expression.expression) &&
                context.currentVariableScopingHasMethod(expression.methodName.text)) {
            out.addScript(GS_OBJECT)
        } else {
            factory.visitNode(expression.expression)
        }
        out.addScript('[')
        factory.visitNode(expression.methodName)
        out.addScript(']')
    }
}

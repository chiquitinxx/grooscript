package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.stmt.AssertStatement

import static org.grooscript.JsNames.GS_ASSERT

/**
 * User: jorgefrancoleza
 * Date: 4/12/14
 */
class UnaryMinusExpressionHandler extends BaseHandler {

    void handle(UnaryMinusExpression expression) {
        out.addScript('-(')
        conversionFactory.visitNode(expression.expression)
        out.addScript(')')
    }
}

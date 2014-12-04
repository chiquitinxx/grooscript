package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.UnaryPlusExpression

/**
 * User: jorgefrancoleza
 * Date: 4/12/14
 */
class UnaryPlusExpressionHandler extends BaseHandler {

    void handle(UnaryPlusExpression expression) {
        out.addScript('+(')
        conversionFactory.visitNode(expression.expression)
        out.addScript(')')
    }
}

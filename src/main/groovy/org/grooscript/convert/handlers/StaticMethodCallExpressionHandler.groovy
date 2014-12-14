package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.*

/**
 * User: jorgefrancoleza
 * Date: 14/12/14
 */
class StaticMethodCallExpressionHandler extends BaseHandler {

    void handle(StaticMethodCallExpression expression) {
        def owner = expression.ownerType.name
        if (owner == 'java.lang.Integer' && expression.method == 'parseInt') {
            out.addScript('parseInt')
        } else if (owner == 'java.lang.Float' && expression.method == 'parseFloat') {
            out.addScript('parseFloat')
        } else {
            out.addScript("${conversionFactory.reduceClassName(expression.ownerType.name)}.${expression.method}")
        }
        conversionFactory.visitNode(expression.arguments)
    }
}

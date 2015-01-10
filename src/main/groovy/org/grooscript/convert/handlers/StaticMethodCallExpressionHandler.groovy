package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.*

import static org.grooscript.JsNames.GS
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
        } else if (owner == 'org.grooscript.GrooScript' && expression.method in ['toGroovy', 'toJavascript']) {
            out.addScript("$GS${expression.method}")
        } else {
            out.addScript("${conversionFactory.reduceClassName(expression.ownerType.name)}.${expression.method}")
        }
        conversionFactory.visitNode(expression.arguments)
    }
}

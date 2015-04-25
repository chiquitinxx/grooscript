package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.*

/**
 * User: jorgefrancoleza
 * Date: 14/12/14
 */
class StaticMethodCallExpressionHandler extends BaseHandler {

    void handle(StaticMethodCallExpression expression) {
        def ownerType = expression.ownerType.name
        def specialStatic = SPECIAL_STATIC_METHOD_CALLS.find {
            it.type == ownerType && it.method == expression.method
        }
        if (ownerType == 'org.grooscript.GrooScript' && expression.method == 'nativeJs') {
            conversionFactory.outFirstArgument(expression)
        } else {
            if (specialStatic) {
                out.addScript(specialStatic.function)
            } else {
                out.addScript("${conversionFactory.reduceClassName(ownerType)}.${expression.method}")
            }
            conversionFactory.visitNode(expression.arguments)
        }
    }
}

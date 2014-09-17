package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.BooleanExpression

/**
 * User: jorgefrancoleza
 * Date: 17/09/14
 */
class BooleanExpressionHandler extends BaseHandler {

    void handle(BooleanExpression expression) {
        //Groovy truth is a bit different, empty collections return false, we fix that here
        conversionFactory.handExpressionInBoolean(expression.expression)
    }
}

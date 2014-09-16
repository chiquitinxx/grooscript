package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.EmptyExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.AssertStatement

import static org.grooscript.JsNames.GS_ASSERT

/**
 * User: jorgefrancoleza
 * Date: 16/09/14
 */
class AssertStatementHandler extends BaseHandler {

    void handle(AssertStatement statement) {
        Expression expression = statement.booleanExpression
        out.addScript(GS_ASSERT)
        out.addScript('(')
        conversionFactory.visitNode(expression)
        if (statement.getMessageExpression() && !(statement.messageExpression instanceof EmptyExpression)) {
            out.addScript(', ')
            conversionFactory.visitNode(statement.messageExpression)
        }
        out.addScript(')')
    }
}

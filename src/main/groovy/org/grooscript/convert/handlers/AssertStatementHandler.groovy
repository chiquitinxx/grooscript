package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.AssertStatement

import static org.grooscript.JsNames.GS_ASSERT

/**
 * User: jorgefrancoleza
 * Date: 16/09/14
 */
class AssertStatementHandler extends BaseHandler {

    void handle(AssertStatement statement) {
        BooleanExpression expression = statement.booleanExpression
        out.addScript(GS_ASSERT)
        out.addScript('(')
        conversionFactory.visitNode(expression)
        if (statement.messageExpression &&
               !(statement.messageExpression instanceof ConstantExpression &&
                       statement.messageExpression.value == null)) {
            out.addScript(', ')
            conversionFactory.visitNode(statement.messageExpression)
        } else {
            out.addScript(', "')
            out.addScript('Assertion fails: ' +
                    expression.text.replaceAll('"','\\\\"').replaceAll(/\n/,'\\n'))
            out.addScript('"')
        }
        out.addScript(')')
    }
}

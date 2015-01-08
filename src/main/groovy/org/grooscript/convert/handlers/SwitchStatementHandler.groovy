package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.SwitchStatement

import static org.grooscript.JsNames.GS_EQUALS
import static org.grooscript.JsNames.SWITCH_VAR_NAME

/**
 * User: jorgefrancoleza
 * Date: 09/02/14
 */
class SwitchStatementHandler extends BaseHandler {

    void handle(SwitchStatement statement) {
        def varName = "$SWITCH_VAR_NAME${context.switchCount++}"

        out.addScript('var '+varName+' = ')
        conversionFactory.visitNode(statement.expression)
        out.addScript(';', true)

        def first = true

        statement.caseStatements?.each { it ->
            if (first) {
                out.addScript("if (")
                first = false
            } else {
                out.addScript("} else if (")
            }
            getSwitchExpression(it.expression,varName)
            out.addScript(') {')
            out.indent++
            out.addLine()
            conversionFactory.visitNode(it?.code)
            out.indent--
            out.removeTabScript()
        }
        if (statement.defaultStatement) {
            out.addScript('} else {')
            out.indent++
            out.addLine()
            conversionFactory.visitNode(statement.defaultStatement)
            out.indent--
            out.removeTabScript()
        }

        out.addScript('}')
        context.switchCount--
    }

    private getSwitchExpression(Expression expression, String varName) {

        if (expression instanceof ClosureExpression) {
            context.addClosureSwitchInitialization = true
            conversionFactory.visitNode(expression, true)
            out.addScript('()')
        } else {
            out.addScript("${GS_EQUALS}(${varName}, ")
            conversionFactory.visitNode(expression)
            out.addScript(')')
        }
    }
}

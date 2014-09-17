package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.ConstantExpression

/**
 * User: jorgefrancoleza
 * Date: 17/09/14
 */
class ConstantExpressionHandler extends BaseHandler {

    void handle(ConstantExpression expression) {
        if (expression.value instanceof String) {
            //println 'Value->'+expression.value+'<'+expression.value.endsWith('\n')
            String value = ''
            if (expression.value.startsWith('\n')) {
                value = '\\n'
            }
            def list = []
            expression.value.eachLine {
                if (it) list << it
            }
            value += list.join('\\n')
            value = value.replaceAll('"','\\\\"')
            //println 'After->'+value+'<'+value.endsWith('\n')
            if (expression.value.endsWith('\n') && !value.endsWith('\n') && value != '\\n') {
                value += '\\n'
            }
            out.addScript('"'+value+'"')
        } else {
            out.addScript(expression.value)
        }
    }

    void handle(ConstantExpression expression, boolean addStuff) {
        if (expression.value instanceof String && addStuff) {
            handle(expression)
        } else {
            out.addScript(expression.value)
        }
    }
}

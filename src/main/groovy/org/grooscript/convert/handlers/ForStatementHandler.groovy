package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.stmt.ForStatement

/**
 * User: jorgefrancoleza
 * Date: 19/08/14
 */
class ForStatementHandler extends BaseHandler {

    void handle(ForStatement statement) {
        context.variableScoping.push([])
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //We change this for in...  for a call lo closure each, that works fine in javascript
            conversionFactory.visitNode(statement?.collectionExpression)
            out.addScript('.each(function(')
            conversionFactory.visitNode(statement.variable)
            context.addToActualScope(statement.variable.name)

        } else {
            out.addScript 'for ('
            conversionFactory.visitNode(statement?.collectionExpression)
        }
        out.addScript ') {'
        out.indent++
        out.addLine()

        conversionFactory.visitNode(statement?.loopBlock)

        out.indent--
        out.removeTabScript()
        out.addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            out.addScript(')')
        }
        context.variableScoping.pop()
    }
}

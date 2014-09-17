package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * User: jorgefrancoleza
 * Date: 17/09/14
 */
class ExpressionStatementHandler extends BaseHandler {

    void handle(ExpressionStatement statement) {
        conversionFactory.visitNode(statement.expression)
    }
}

package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.ArrayExpression

import static org.grooscript.JsNames.*
/**
 * User: jorgefrancoleza
 * Date: 09/02/14
 */
class MethodPointerExpressionHandler extends BaseHandler {

    void handle(ArrayExpression expression) {
        out.addScript("${GS_LIST}()")
    }
}

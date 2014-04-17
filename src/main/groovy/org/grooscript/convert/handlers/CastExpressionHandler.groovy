package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.VariableExpression

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 09/02/14
 */
class CastExpressionHandler extends BaseHandler {

    void handle(CastExpression expression) {
        if (expression.type.name == 'java.util.Set' && expression.expression instanceof ListExpression) {
            out.addScript("${GS_SET}(")
            factory.visitNode(expression.expression)
            out.addScript(')')
        } else {
            if (expression.expression instanceof MapExpression || expression.expression instanceof ListExpression) {
                factory.visitNode(expression.expression)
            } else if (expression.expression instanceof GStringExpression) {
                factory.visitNode(expression.expression)
            } else if (expression.expression instanceof VariableExpression &&
                factory.isTraitClass(expression.type.name) &&
                expression.expression.variable == '$self') {
                    out.addScript('$self')
            } else {
                throw new Exception('Casting not supported for: ' +expression.type.name +
                        ' with value:' + expression.expression.type.name)
            }
        }
    }
}

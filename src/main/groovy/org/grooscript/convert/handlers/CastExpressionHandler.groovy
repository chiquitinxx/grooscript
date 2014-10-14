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

    private static final WRONG_NAMES = ['String', 'String;', 'List', 'Map']

    void handle(CastExpression expression) {
        if (expression.type.name == 'java.util.Set' && expression.expression instanceof ListExpression) {
            out.addScript("${GS_SET}(")
            conversionFactory.visitNode(expression.expression)
            out.addScript(')')
        } else if (expression.type.name == 'char') {
            out.addScript("${GS_AS_CHAR}(")
            conversionFactory.visitNode(expression.expression)
            out.addScript(')')
        } else {
            if (expression.expression instanceof MapExpression || expression.expression instanceof ListExpression) {
                conversionFactory.visitNode(expression.expression)
                addAsTypeFunction(expression)
            } else if (expression.expression instanceof GStringExpression) {
                conversionFactory.visitNode(expression.expression)
                addAsTypeFunction(expression)
            } else if (expression.expression instanceof VariableExpression &&
                conversionFactory.isTraitClass(expression.type.name) &&
                expression.expression.variable == '$self') {
                    out.addScript('$self')
            } else {
                conversionFactory.visitNode(expression.expression)
                addAsTypeFunction(expression)
            }
        }
    }

    private addAsTypeFunction(CastExpression expression) {
        if (traits.isTrait(expression.type) ||
                (!expression.type.nameWithoutPackage in WRONG_NAMES && !expression.type.isInterface())) {
            out.addScript(".${GS_AS_TYPE}(${expression.type.nameWithoutPackage})")
        }
    }
}

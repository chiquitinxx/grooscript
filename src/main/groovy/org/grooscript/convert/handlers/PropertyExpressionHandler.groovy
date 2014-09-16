package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.AttributeExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 26/01/14
 */
class PropertyExpressionHandler extends BaseHandler {

    void handle(PropertyExpression expression) {
        //If metaClass property we ignore it, javascript permits add directly properties and methods
        if (expression.property instanceof ConstantExpression && expression.property.value == 'metaClass') {
            if (expression.objectExpression instanceof VariableExpression) {

                if (expression.objectExpression.name == 'this') {
                    out.addScript('this')
                } else {
                    //I had to add variable = ... cause gSmetaClass changing object and sometimes variable don't change
                    out.addScript("(${expression.objectExpression.name} = ${GS_META_CLASS}(")
                    conversionFactory.visitNode(expression.objectExpression)
                    out.addScript('))')
                }
            } else {
                if (expression.objectExpression instanceof ClassExpression &&
                        (expression.objectExpression.type.name.startsWith('java.') ||
                                expression.objectExpression.type.name.startsWith('groovy.'))) {
                    throw new Exception("Not allowed access metaClass of Groovy " +
                            "or Java types (${expression.objectExpression.type.name})")
                }
                out.addScript("${GS_META_CLASS}(")
                conversionFactory.visitNode(expression.objectExpression)
                out.addScript(')')
            }
        } else if (expression.property instanceof ConstantExpression && expression.property.value == 'class') {
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".${CLASS}")
        } else {

            if (isKnownProperty(expression)) {
                conversionFactory.processKnownPropertyExpression(expression)
            } else {
                out.addScript("${GS_GET_PROPERTY}(")
                if (expression.objectExpression instanceof VariableExpression &&
                        expression.objectExpression.name == 'this') {
                    out.addScript("${GS_THIS_OR_OBJECT}(this,${GS_OBJECT})")
                } else {
                    conversionFactory.processObjectExpressionFromProperty(expression)
                }

                out.addScript(',')

                conversionFactory.processPropertyExpressionFromProperty(expression)

                //If is a safe expression as item?.data, we add one more parameter
                if (expression.isSafe()) {
                    out.addScript(',true')
                }

                out.addScript(')')
            }
        }
    }

    private isKnownProperty(PropertyExpression propertyExpression) {
        propertyExpression instanceof AttributeExpression ||
            (propertyExpression.propertyAsString &&
                    context.currentClassMethodConverting == "get${propertyExpression.propertyAsString.capitalize()}")
    }
}

package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.AttributeExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.RangeExpression
import org.codehaus.groovy.ast.expr.VariableExpression

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 21/01/14
 */
class BinaryExpressionHandler extends BaseHandler {

    private static final ASSIGN_OPERATORS = ['=', '+=', '-=']
    private static final OPERATORS = [
            '+': GS_PLUS,
            '-': GS_MINUS,
            '*': GS_MULTIPLY,
            '/': GS_DIV,
            '==': GS_EQUALS,
            '<=>': GS_SPACE_SHIP,
            'in': GS_IN,
            '**': GS_POWER,
            '%': GS_MOD,
    ]
    
    void handle(BinaryExpression expression) {

        //println 'Binary->'+expression.text + ' - '+expression.operation.text
        //Getting a range from a list
        if (expression.operation.text == '[' && expression.rightExpression instanceof RangeExpression) {
            out.addScript("${GS_RANGE_FROM_LIST}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(", ")
            conversionFactory.visitNode(expression.rightExpression.getFrom())
            out.addScript(", ")
            conversionFactory.visitNode(expression.rightExpression.getTo())
            out.addScript(')')
        //leftShift and rightShift function
        } else if (expression.operation.text == '<<' || expression.operation.text == '>>') {
            def nameFunction = expression.operation.text == '<<' ? 'leftShift' : 'rightShift'
            out.addScript("${GS_METHOD_CALL}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(",'${nameFunction}', ${GS_LIST}([")
            upgradedExpresion(expression.rightExpression)
            out.addScript(']))')
            //Regular Expression exact match all
        } else if (expression.operation.text == '==~') {
            out.addScript("${GS_EXACT_MATCH}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                out.addScript('/')
                conversionFactory.visitNode(expression.rightExpression, false)
                out.addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            out.addScript(')')
            //A matcher of regular expresion
        } else if (expression.operation.text == '=~') {
            out.addScript("${GS_REG_EXP}(")
            //println 'rx->'+expression.leftExpression
            upgradedExpresion(expression.leftExpression)
            out.addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                out.addScript('/')
                conversionFactory.visitNode(expression.rightExpression, false)
                out.addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            out.addScript(')')
            //instanceof
        } else if (expression.operation.text == 'instanceof') {
            out.addScript("${GS_INSTANCE_OF}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(', "')
            upgradedExpresion(expression.rightExpression)
            out.addScript('")')
        //Basic operators
        } else if (expression.operation.text in OPERATORS.keySet()) {
            writeFunctionWithLeftAndRight(OPERATORS[expression.operation.text], expression)
        } else {

            //Execute setter if available
            if (expression.leftExpression instanceof PropertyExpression &&
                    (expression.operation.text in ASSIGN_OPERATORS) &&
                    !(expression.leftExpression instanceof AttributeExpression)) {

                if (expression.leftExpression.objectExpression instanceof VariableExpression &&
                        expression.leftExpression.objectExpression.variable == 'this' &&
                        expression.leftExpression.propertyAsString &&
                        context.currentClassMethodConverting ==
                            "set${expression.leftExpression.propertyAsString.capitalize()}") {
                    conversionFactory.processKnownPropertyExpression(expression.leftExpression)
                    out.addScript(" ${expression.operation.text} ")
                    upgradedExpresion(expression.rightExpression)
                } else {

                    PropertyExpression pe = (PropertyExpression) expression.leftExpression
                    out.addScript("${GS_SET_PROPERTY}(")
                    upgradedExpresion(pe.objectExpression)
                    out.addScript(',')
                    upgradedExpresion(pe.property)
                    out.addScript(',')
                    assignExpressionValue(expression)
                    out.addScript(')')
                }
            //Unknown variable inside a with block
            } else if (expression.leftExpression instanceof VariableExpression &&
                    (expression.operation.text in ASSIGN_OPERATORS) &&
                    !context.allActualScopeContains(expression.leftExpression.name) &&
                    !context.variableScopingContains(expression.leftExpression.name) &&
                    context.insideWith) {
                out.addScript("${GS_SET_PROPERTY}(this, '${expression.leftExpression.name}',")
                assignExpressionValue(expression)
                out.addScript(')')
            } else {
                //If we are assigning a variable, and don't exist in scope, we add to it
                if (expression.operation.text in ASSIGN_OPERATORS && 
                        expression.leftExpression instanceof VariableExpression && 
                        !context.allActualScopeContains(expression.leftExpression.name) &&
                        !context.variableScopingContains(expression.leftExpression.name)) {
                    context.addToActualScope(expression.leftExpression.name)
                }

                //If is a boolean operation, we have to apply groovyTruth
                //Left
                applyGroovyTruthIfNecesary(expression.operation, expression.leftExpression)
                //Operator
                out.addScript(' '+expression.operation.text+' ')
                //Right
                applyGroovyTruthIfNecesary(expression.operation, expression.rightExpression)

                if (expression.operation.text=='[') {
                    out.addScript(']')
                }
            }
        }
    }

    //Adding () for operators order, can spam loads of ()
    private upgradedExpresion(expresion) {
        if (expresion instanceof BinaryExpression) {
            out.addScript('(')
        }
        conversionFactory.visitNode(expresion)
        if (expresion instanceof BinaryExpression) {
            out.addScript(')')
        }
    }

    private writeFunctionWithLeftAndRight(functionName, expression) {
        out.addScript("${functionName}(")
        upgradedExpresion(expression.leftExpression)
        out.addScript(', ')
        upgradedExpresion(expression.rightExpression)
        out.addScript(')')
    }

    private assignExpressionValue(expression) {
        if (expression.operation.text == '+=') {
            conversionFactory.visitNode(expression.leftExpression)
            out.addScript(' + ')
        } else if (expression.operation.text == '-=') {
            conversionFactory.visitNode(expression.leftExpression)
            out.addScript(' - ')
        }
        upgradedExpresion(expression.rightExpression)
    }
    
    private applyGroovyTruthIfNecesary(operation, expression) {
        if (operation.text in ['&&', '||']) {
            out.addScript '('
            conversionFactory.handExpressionInBoolean(expression)
            out.addScript ')'
        } else {
            upgradedExpresion(expression)
        }        
    }
}

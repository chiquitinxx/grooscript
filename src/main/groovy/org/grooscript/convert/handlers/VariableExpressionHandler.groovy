package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.VariableExpression

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 18/01/14
 */
class VariableExpressionHandler extends BaseHandler {

    void handle(VariableExpression expression, isDeclaringVariable = false) {
        //println "name:${expression.name} - ${expression.isThisExpression()}"
        if (context.variableScoping.peek().contains(expression.name) &&
                !(context.allActualScopeContains(expression.name))) {
                out.addScript(addPrefixOrPostfixIfNeeded("${GS_OBJECT}."+expression.name))
        } else if (context.variableStaticScoping.peek().contains(expression.name) &&
                !(context.allActualScopeContains(expression.name))) {
            out.addScript(addPrefixOrPostfixIfNeeded(context.classNameStack.peek()+'.'+expression.name))
        } else {
            if (context.traitFieldScopeContains(expression.name)) {
                out.addScript("${GS_OBJECT}.get${expression.name.capitalize()}()")
            } else if (context.isVariableWithMissingScope(expression) && !isDeclaringVariable) {
                out.addScript("${GS_FIND_SCOPE}('${addPrefixOrPostfixIfNeeded(expression.name)}', this)")
            } else {
                out.addScript(addPrefixOrPostfixIfNeeded(expression.name))
            }
        }
    }

    private addPrefixOrPostfixIfNeeded(name) {
        if (context.prefixOperator) {
            name = context.prefixOperator + name
        }
        if (context.postfixOperator) {
            name = name + context.postfixOperator
        }
        name
    }
}

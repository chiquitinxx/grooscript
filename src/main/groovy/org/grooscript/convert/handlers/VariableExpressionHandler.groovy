/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.VariableExpression

import static org.grooscript.JsNames.*

class VariableExpressionHandler extends BaseHandler {

    void handle(VariableExpression expression, isDeclaringVariable = false) {
        //println "name:${expression.name} - ${expression.isThisExpression()}"
        if (context.actualScope.peek().contains(expression.name)) {
            out.addScript(addPrefixOrPostfixIfNeeded(expression.name))
        } else if (context.variableScoping.peek().contains(expression.name) &&
                !(context.allActualScopeContains(expression.name))) {
            addObjectVariable(expression.name)
        } else if (context.variableStaticScoping.peek().contains(expression.name) &&
                !(context.allActualScopeContains(expression.name))) {
            out.addScript(addPrefixOrPostfixIfNeeded(context.classNameStack.peek()+'.'+expression.name))
        } else {
            if (context.traitFieldScopeContains(expression.name)) {
                out.addScript("${GS_OBJECT}.get${expression.name.capitalize()}()")
            } else if (context.isVariableWithMissingScope(expression) && !isDeclaringVariable) {
                out.addScript("${GS_FIND_SCOPE}('${addPrefixOrPostfixIfNeeded(expression.name)}', this")
                if (!context.classNameStack.empty() && context.classNameStack.peek() && !context.staticProcessNode) {
                    out.addScript(", ${GS_OBJECT}")
                }
                out.addScript(')')
            } else if (!isDeclaringVariable && context.variableStaticScoping && context.classNameStack &&
                    !context.actualScope.peek().contains(expression.name) &&
                    context.variableStaticScoping.peek() && expression.name in context.variableStaticScoping.peek()) {
                out.addScript(addPrefixOrPostfixIfNeeded("${context.classNameStack.peek()}."+expression.name))
            } else if (!context.variableScoping.peek().contains(expression.name) &&
                    context.variableScopingContains(expression.name)) {
                addObjectVariable(expression.name)
            } else {
                out.addScript(addPrefixOrPostfixIfNeeded(expression.name))
            }
        }
    }

    private addObjectVariable(String name) {
        out.addScript(addPrefixOrPostfixIfNeeded("${context.staticProcessNode ? 'this' : GS_OBJECT}." + name))
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

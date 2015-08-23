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

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.EmptyExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression

class DeclarationExpressionHandler extends BaseHandler {

    void handle(DeclarationExpression expression) {

        if (isBaseScriptDeclaration(expression.annotations)) {

            context.addToActualScope(expression.variableExpression.name)

            out.addScript('var ')
            conversionFactory.getConverter('VariableExpression').handle(expression.variableExpression, true)
            out.addScript(' = ' + expression.leftExpression.type.nameWithoutPackage + '();', true)

            conversionFactory.getConverter('VariableExpression').handle(expression.variableExpression, true)
            out.addScript('.withz(function() {')
            out.indent ++
            context.processingBaseScript = true

        } else if (expression.isMultipleAssignmentDeclaration()) {
            TupleExpression tuple = (TupleExpression)(expression.getLeftExpression())
            def number = 0;
            tuple.expressions.each { Expression expr ->
                //println 'Multiple->'+expr
                if (expr instanceof VariableExpression && expr.name!='_') {
                    context.addToActualScope(expr.name)
                    out.addScript('var ')
                    conversionFactory.getConverter('VariableExpression').handle(expr, true)
                    out.addScript(' = ')
                    conversionFactory.visitNode(expression.rightExpression)
                    out.addScript(".getAt(${number})")
                    if (number < tuple.expressions.size()) {
                        out.addScript(';')
                    }
                }
                number++
            }
        } else {
            context.addToActualScope(expression.variableExpression.name)

            out.addScript('var ')
            conversionFactory.getConverter('VariableExpression').handle(expression.variableExpression, true)

            if (!(expression.rightExpression instanceof EmptyExpression)) {
                out.addScript(' = ')
                conversionFactory.visitNode(expression.rightExpression)
            } else {
                out.addScript(' = null')
            }
        }
    }

    private isBaseScriptDeclaration(annotations) {
        boolean isBaseScript = false
        annotations.each { AnnotationNode it ->
            //If dont have to convert then exit
            if (it.getClassNode().name == 'groovy.transform.BaseScript') {
                isBaseScript = true
            }
        }
        return isBaseScript
    }
}

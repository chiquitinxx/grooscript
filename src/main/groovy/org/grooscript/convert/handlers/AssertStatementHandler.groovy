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

import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.AssertStatement

import static org.grooscript.JsNames.GS_ASSERT

class AssertStatementHandler extends BaseHandler {

    void handle(AssertStatement statement) {
        BooleanExpression expression = statement.booleanExpression
        out.addScript(GS_ASSERT)
        out.addScript('(')
        conversionFactory.visitNode(expression)
        if (statement.messageExpression &&
               !(statement.messageExpression instanceof ConstantExpression &&
                       statement.messageExpression.value == null)) {
            out.addScript(', ')
            conversionFactory.visitNode(statement.messageExpression)
        } else {
            out.addScript(', "')
            out.addScript('Assertion fails: ' +
                    expression.text.replaceAll('"','\\\\"').replaceAll(/\n/,'\\n'))
            out.addScript('"')
        }
        out.addScript(')')
    }
}

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

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ForStatement

class ForStatementHandler extends BaseHandler {

    void handle(ForStatement statement) {
        context.variableScoping.push([])
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //We change this for in...  for a call lo closure each, that works fine in javascript
            conversionFactory.visitNode(statement?.collectionExpression)
            out.addScript('.each(function(')
            conversionFactory.visitNode(statement.variable)
            context.addToActualScope(statement.variable.name)

        } else {
            out.addScript 'for ('
            conversionFactory.visitNode(statement?.collectionExpression)
        }
        out.addScript ') {'
        out.indent++
        out.addLine()

        conversionFactory.visitNode(statement?.loopBlock)

        if (!(statement.loopBlock instanceof BlockStatement)) {
            out.addScript(';', true)
        }

        out.indent--
        out.removeTabScript()
        out.addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            out.addScript(')')
        }
        context.variableScoping.pop()
    }
}

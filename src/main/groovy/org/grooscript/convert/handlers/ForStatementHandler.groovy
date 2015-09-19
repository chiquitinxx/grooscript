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

    private static counter = 0

    void handle(ForStatement statement) {
        context.variableScoping.push([])
        out.addScript 'for ('
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //Example: i = 0, name = names[i];i < names.length;name = names[++i]
            def countName = "_i${counter++}"
            def varName = statement.variable.name
            out.addScript("${countName} = 0, ${varName} = ")
            addCollectionExpression(statement)
            out.addScript("[0]; ${countName} < ")
            addCollectionExpression(statement)
            out.addScript(".length; ${varName} = ")
            addCollectionExpression(statement)
            out.addScript("[++${countName}]")
            //Add var to context
            context.addToActualScope(varName)
        } else {
            addCollectionExpression(statement)
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
        context.variableScoping.pop()
    }

    private addCollectionExpression(ForStatement statement) {
        conversionFactory.visitNode(statement?.collectionExpression)
    }
}

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
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.TryCatchStatement

class TryCatchStatementHandler extends BaseHandler {

    void handle(TryCatchStatement statement) {

        putTryCode(statement)

        if (filledBlock(statement?.catchStatements[0])) {
            putCatchCode(statement)
        }

        if (filledBlock(statement.finallyStatement)) {
            out.block('finally') {
                conversionFactory.visitNode(statement.finallyStatement)
            }
        }
    }

    private putTryCode(TryCatchStatement statement) {
        out.addScript('try {')
        out.indent++
        out.addLine()

        conversionFactory.visitNode(statement?.tryStatement)

        out.indent--
        out.removeTabScript()
        out.addScript('}', true)
    }

    private putCatchCode(TryCatchStatement statement) {

        out.addScript('catch (')
        if (statement?.catchStatements[0]) {
            conversionFactory.visitNode(statement?.catchStatements[0].variable)
        } else {
            out.addScript('e')
        }
        out.addScript(') {')
        out.indent++
        out.addLine()
        //Only process first catch
        processCatchStatement(statement?.catchStatements[0])

        out.indent--
        out.removeTabScript()
        out.addScript('}', true)
    }

    private processCatchStatement(CatchStatement statement) {
        conversionFactory.visitNode(statement.code, false)
    }

    private filledBlock(statement) {
        statement && !(statement instanceof EmptyStatement)
    }

    private boolean hasExplicitReturn(Statement statement) {
        if (statement instanceof BlockStatement) {
            statement.statements.any {
                hasExplicitReturn it
            }
        } else {
            statement instanceof ReturnStatement
        }
    }
}

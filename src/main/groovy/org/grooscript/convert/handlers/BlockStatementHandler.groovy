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
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.grooscript.util.GsConsole

class BlockStatementHandler extends BaseHandler {

    /**
     * Process an AST Block
     * @param block
     * @param addReturn put 'return ' before last statement
     * @return
     */
    void handle(BlockStatement block, boolean addReturn = false) {
        if (block) {
            def number = 1
            //println 'Block->'+block
            if (block instanceof EmptyStatement) {
                GsConsole.debug "BlockEmpty -> ${block.text}"
            } else {
                //println '------------------------------Block->'+block.text
                block.getStatements()?.each { statement ->
                    def lookingForIf = false
                    def position
                    context.returnScoping.push(false)
                    if (addReturn && ((number++) == block.getStatements().size())
                            && conversionFactory.statementThatCanReturn(statement)) {

                        //this statement can be a complex statement with a return
                        //Go looking for a return statement in last statement
                        position = out.savePoint
                    }
                    if (addReturn && (number - 1) == block.getStatements().size() &&
                            !context.lookingForReturnStatementInIf && statement instanceof IfStatement) {
                        context.lookingForReturnStatementInIf = true
                        lookingForIf = true
                    }
                    def oldlookingForReturnStatementInIf = context.lookingForReturnStatementInIf
                    if ((number - 1) != block.getStatements().size()) {
                        context.lookingForReturnStatementInIf = false
                    }
                    visitStatement(statement)
                    context.lookingForReturnStatementInIf = oldlookingForReturnStatementInIf
                    if (lookingForIf) {
                        context.lookingForReturnStatementInIf = false
                    }
                    if (addReturn && position) {
                        if (!context.returnScoping.peek()) {
                            //No return statement, then we want add return
                            out.addScriptAt('return ', position)
                        }
                    }
                    context.returnScoping.pop()
                }
            }
        }
    }

    private void visitStatement(Statement statement) {

        //println "statement (${statement.class.simpleName})->"+statement+' - '+statement.text
        conversionFactory.visitNode(statement)

        //Adds ;
        if (out.resultScript && !(statement instanceof BlockStatement) && !(statement instanceof EmptyStatement)) {
            out.resultScript += ';'
            out.addLine()
        }
    }
}

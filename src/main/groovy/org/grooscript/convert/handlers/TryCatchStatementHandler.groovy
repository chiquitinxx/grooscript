package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.TryCatchStatement

/**
 * User: jorgefrancoleza
 * Date: 21/01/14
 */
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

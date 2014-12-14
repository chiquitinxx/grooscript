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

    private static final RETURN_COUNT = 0

    void handle(TryCatchStatement statement) {

        def count = RETURN_COUNT++

        if (filledFinally(statement)) {
            out.addScript("var gS${count} = (function() {")
            out.indent++
            out.addLine()
        }

        putTryCatchCode(statement)

        if (filledFinally(statement)) {
            out.indent--
            out.removeTabScript()
            out.addScript('})();', true)
        }
        if (filledFinally(statement)) {
            conversionFactory.visitNode(statement.finallyStatement)
            out.addScript("if (gS${count}) { return gS${count} };", true)
        }
    }

    private putTryCatchCode(TryCatchStatement statement) {
        //Try block
        out.addScript('try {')
        out.indent++
        out.addLine()

        conversionFactory.visitNode(statement?.tryStatement)

        out.indent--
        out.removeTabScript()
        //Catch block
        out.addScript('} catch (')
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

    private finallyWithReturn(TryCatchStatement statement) {
        filledFinally(statement) && hasExplicitReturn(statement.finallyStatement)
    }

    private filledFinally(TryCatchStatement statement) {
        statement && statement.finallyStatement && !(statement.finallyStatement instanceof EmptyStatement)
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

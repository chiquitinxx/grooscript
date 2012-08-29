package org.yila.gscript

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.yila.gscript.util.Util

/**
 * JFL 27/08/12
 */
class GsConverter {

    //Indent for pretty print
    def indent
    def static final TAB = '  '

    //We get this function names from functions.groovy
    def assertFunction

    def GsConverter() {
        initFunctionNames()
    }

    def initFunctionNames() {
        def clos = new GroovyShell().evaluate('{ gscript ->\n'+Util.getNameFunctionsText()+'\n return gscript}')
        this.with clos
    }

    /**
     * Converts Groovy script to Javascript
     * @param String script in groovy
     * @return String sript in javascript
     */
    def toJs(String script) {
        def result
        //Script not empty plz!
        if (script) {
            def list = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS,script)
            result = processAstListToJs(list)
        }
        result
    }

    /**
     * Process an AST List from Groovy code to javascript script
     * @param list
     * @return
     */
    def processAstListToJs(list) {
        def result
        indent = 0
        if (list && list.size()>0) {
            //println '->'+list
            if (list.get(0) instanceof BlockStatement) {
                result = processBlockStament(list.get(0))
            }
        }
        //println 'res->'+ result
        result
    }

    /**
     * Process an AST Block
     * @param block
     * @return
     */
    def processBlockStament(block) {
        def result
        if (block) {
            block.getStatements().each { it ->
                result = addLine(result,processStatement(it))
                //println 'pRes->'+ result
            }
        }
        //println 'res->'+ result
        result
    }

    /**
     * Add a line to javascript script
     * @param script
     * @param line
     * @return
     */
    def addLine(script,line) {
        //println "sc(${script}) line(${line})"
        if (script) {
            script += '\n'
        } else {
            script = ''
        }
        indent.times { script += TAB }
        script += line
        //println "sc(${script}) line(${line})"
        return script
    }

    def processStatement(statement) {
        def result
        //println 'statement->'+statement
        if (statement instanceof AssertStatement) {
            //Assert have a boolean expression
            //TODO Ignoring messageExpression in AssertStatement
            //result = 'gSassert('+processExpression(statement.booleanExpression)+');'

            result = assertFunction +'('+processExpression(statement.booleanExpression)+');'
        }
        //Adds ;
        //if (result) {
        //    result += ';'
        //}
        result
    }

    def processExpression(Expression e) {
        def result

        //println 'Expression->'+e.class.name
        if (e instanceof BooleanExpression) {
            //println 'Adding->'+e.text
            result = e.text
        }

        result
    }
}

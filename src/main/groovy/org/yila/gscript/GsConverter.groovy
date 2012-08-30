package org.yila.gscript

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.expr.*
import org.yila.gscript.util.Util
import org.yila.gscript.util.GsConsole

/**
 * JFL 27/08/12
 */
class GsConverter {

    //Indent for pretty print
    def indent
    def static final TAB = '  '
    def resultScript

    //We get this function names from functions.groovy
    def assertFunction

    def GsConverter() {
        initFunctionNames()
    }

    def initFunctionNames() {
        def clos = new GroovyShell().evaluate('{ gscript ->\n'+Util.getNameFunctionsText()+'\n return gscript}')
        this.with clos
        //GroovyShell gs = new GroovyShell()
        //Util.getNameFunctionsText().eachLine { gs.evaluate(it) }
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
        resultScript = ''
        if (list && list.size()>0) {
            //println '->'+list
            if (list.get(0) instanceof BlockStatement) {
                processBlockStament(list.get(0))
                result = resultScript
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
        if (block) {
            block.getStatements().each { it ->
                processStatement(it)
            }
        }
    }

    /**
     * Add a line to javascript script
     * @param script
     * @param line
     * @return
     */
    def addLine() {
        //println "sc(${script}) line(${line})"
        if (resultScript) {
            resultScript += '\n'
        } else {
            resultScript = ''
        }
        indent.times { resultScript += TAB }
    }

    def addScript(text) {
        //println 'adding ->'+text
        resultScript += text
    }

    /**
     * Process a statement, adding ; at the end
     * @param statement
     */
    def void processStatement(statement) {

        //println "statement (${statement.class.simpleName})->"+statement

        "process${statement.class.simpleName}"(statement)

        //Adds ;
        if (resultScript) {
            resultScript += ';'
        }
        addLine()
    }

    def processAssertStatement(statement) {
        Expression e = statement.booleanExpression
        addScript(assertFunction)
        addScript('(')
        "process${e.class.simpleName}"(e)
        addScript(')')
    }

    def processBooleanExpression(BooleanExpression b) {
        //println 'BooleanExpression->'+e
        //println 'BooleanExpression Inside->'+e.expression
        //addScript('('+e.text+')')
        Expression e = b.expression
        "process${e.class.simpleName}"(e)
    }

    def processExpressionStatement(ExpressionStatement statement) {
        Expression e = statement.expression
        "process${e.class.simpleName}"(e)
    }

    def processDeclarationExpression(DeclarationExpression e) {
        //println '->'+e.text
        //println 'l->'+e.leftExpression
        //println 'r->'+e.rightExpression
        //println 'v->'+e.variableExpression
        addScript('var ')
        processVariableExpression(e.variableExpression)
        if (!e.rightExpression instanceof  EmptyExpression) {
            addScript(' = ')
            "process${e.rightExpression.class.simpleName}"(e.rightExpression)
        }
    }

    def processVariableExpression(VariableExpression v) {
        addScript(v.name)
    }

    /**
     *
     * @param b
     * @return
     */
    def processBinaryExpression(BinaryExpression b) {

        //Adding () for operators order, can spam loads of ()
        //Left
        if (b.leftExpression instanceof BinaryExpression) {
            addScript('(')
        }
        "process${b.leftExpression.class.simpleName}"(b.leftExpression)
        if (b.leftExpression instanceof BinaryExpression) {
            addScript(')')
        }
        //Operator
        addScript(' '+b.operation.text+' ')
        //Right
        if (b.rightExpression instanceof BinaryExpression) {
            addScript('(')
        }
        "process${b.rightExpression.class.simpleName}"(b.rightExpression)
        if (b.rightExpression instanceof BinaryExpression) {
            addScript(')')
        }
    }

    def processConstantExpression(ConstantExpression c) {
        if (c.value instanceof String) {
            addScript('"'+c.value+'"')
        } else {
            addScript(c.value)
        }

    }

    /**
     * Finally GString is something like String + Value + String + Value + String....
     * So we convert to "  " + value + "    " + value ....
     * @param e
     * @return
     */
    def processGStringExpression(GStringExpression e) {

        def number = 0
        e.getStrings().each {   exp ->
            if (number>0) {
                addScript(' + ')
            }
            //addScript('"')
            "process${exp.class.simpleName}"(exp)
            //addScript('"')

            if (e.getValues().size() > number) {
                addScript(' + ')
                "process${e.getValue(number).class.simpleName}"(e.getValue(number))
            }
            number++
        }
    }

    def processNotExpression(NotExpression n) {
        addScript('!')
        "process${n.expression.class.simpleName}"(n.expression)
    }

    def methodMissing(String name, Object args) {
        if (name?.startsWith('process')) {
            GsConsole.error('Conversion not supported for '+name.substring(7))
        } else {
            GsConsole.error('Error methodMissing '+name)
        }

    }

}

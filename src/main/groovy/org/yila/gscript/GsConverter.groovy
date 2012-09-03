package org.yila.gscript

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.expr.*
import org.yila.gscript.util.Util
import org.yila.gscript.util.GsConsole
import org.codehaus.groovy.ast.*

/**
 * JFL 27/08/12
 */
class GsConverter {

    //Indent for pretty print
    def indent
    def static final TAB = '  '
    def resultScript
    def Stack<String> classNameStack = new Stack<String>()
    //Use for variable scoping
    def Stack variableScoping = new Stack()

    def classVariableNames
    //def methodVariableNames
    //def scriptScope

    //We get this function names from functions.groovy
    def assertFunction
    def printlnFunction

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
            //println 'Size('+list.size+')->'+list
            variableScoping.clear()
            variableScoping.push([])
            list.each { it ->
                if (it instanceof BlockStatement) {
                    //scriptScope = true
                    processBlockStament(it,false)
                } else if (it instanceof ClassNode) {
                    //scriptScope = false
                    processClassNode(it)
                } else {
                    GsConsole.error("AST Node not supported (${it.class.simpleName}).")
                }
            }
            result = resultScript
        }
        //println 'res->'+ result
        result
    }

    def processClassNode(ClassNode node) {

        //Starting class conversion

        //Ignoring annotations
        //node?.annotations?.each {}

        //Ignoring modifiers
        //visitModifiers(node.modifiers)

        //print "class $node.name"

        //Push name in stack
        classNameStack.push(node.name)
        variableScoping.push([])
        classVariableNames = []

        addScript("function gsCreate$node.name() {")

        indent ++
        addLine()

        addScript('var object = inherit(gsClass);')
        addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass
        //println 'Superclass->'+node.superClass.class

        //Adding initial values of properties
        node?.properties?.each { it-> //println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (it.initialExpression) {
                addScript("object.${it.name} = ")
                "process${it.initialExpression.class.simpleName}"(it.initialExpression)
                addScript(';')
                addLine()
            }

            //We add variable names of the class
            variableScoping.peek().add(it.name)
            classVariableNames.add(it.name)
        }
        //Ignoring fields
        //node?.fields?.each { println 'field->'+it  }

        //Constructors
        //If no constructor with 1 parameter, we create 1 that recive a map, for put value on properties
        boolean has1parameterConstructor = false
        node?.declaredConstructors?.each { //println 'declaredConstructor->'+it;
            if (it.parameters?.size()==1) {
                has1parameterConstructor = true
            }
            processMethodNode(it,true)
        }
        if (!has1parameterConstructor) {
            addScript("object.${node.name}1 = function(map) { gSpassMapToObject(map,this); return this;};")
            addLine()
        }

        //Methods
        node?.methods?.each { //println 'method->'+it;
            processMethodNode(it,false)
        }

        indent --
        addScript("return object;")
        addLine()
        addScript('}')
        addLine()

        //Remove variable class names from the list
        variableScoping.pop()

        //Pop name in stack
        classNameStack.pop()

        //Finish class conversion
    }

    def processMethodNode(MethodNode method,isConstructor) {

        //Starting method conversion
        //Ignoring annotations
        //node?.annotations?.each {

        //Ignoring modifiers
        //visitModifiers(node.modifiers)

        //Ignoring init methods
        //if (node.name == '<init>') {
        //} else if (node.name == '<clinit>') {
        //visitType node.returnType

        def name =  method.name
        //Constructor method
        if (isConstructor) {
            //Add number of params to constructor name
            //BEWARE Atm only accepts constructor with different number or arguments
            name = classNameStack.peek() + (method.parameters?method.parameters.size():'0')
        }
        addScript("object.$name = function(")

        boolean first = true
        variableScoping.push([])
        method.parameters?.each { param ->
            if (!first) {
                addScript(', ')
            }
            variableScoping.peek().add(param.name)
            addScript(param.name)
            first = false
        }
        addScript(') {')

        indent++
        addLine()

        //println 'Method '+name+' Code:'+method.code
        if (method.code instanceof BlockStatement) {
            processBlockStament(method.code,false)
        } else {
            GsConsole.error("Method Code not supported (${method.code.class.simpleName})")
        }

        /*
        print " $node.name("
        visitParameters(node.parameters)
        print ")"
        if (node.exceptions) {
            boolean first = true
            print ' throws '
            node.exceptions.each {
                if (!first) {
                    print ', '
                }
                first = false
                visitType it
            }
        }
        print " {"
        printLineBreak()

        indented {
            node?.code?.visit(this)
        }
        printLineBreak()
        print '}'
        printDoubleBreak()
        */

        //Delete method variable names
        variableScoping.pop()
        //method.parameters?.each { param ->
        //    methodVariableNames.remove(param.name)
        //}

        indent--
        if (isConstructor) {
            addScript('return this;')
            addLine()
        } else {
            removeTabScript()
        }
        addScript('}')
        addLine()
    }

    /**
     * Process an AST Block
     * @param block
     * @param addReturn put 'return ' before last stament
     * @return
     */
    def processBlockStament(block,addReturn) {
        if (block) {
            def number = 1
            block.getStatements()?.each { it ->
                if (addReturn && ((number++)==block.getStatements().size()) && !(it instanceof ReturnStatement)) {
                    //this statement can be a complex statement with a return
                    //So maybe dont have to add return
                    //TODO we have a test that fails on this, continue tomorrow
                    addScript('return ')
                }
                processStatement(it)
            }
        }
    }

    //???? there are both used
    def processBlockStatement(block) {
        processBlockStament(block,false)
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
        //indent.times { resultScript += TAB }
        resultScript += text
    }

    def removeTabScript() {
        resultScript = resultScript[0..resultScript.size()-1-TAB.size()]
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
        //println 'l->'+e.leftExpression
        //println 'r->'+e.rightExpression
        //println 'v->'+e.variableExpression
        addScript('var ')
        processVariableExpression(e.variableExpression,true)

        variableScoping.peek().add(e.variableExpression.name)

        if (!(e.rightExpression instanceof  EmptyExpression)) {
            addScript(' = ')
            "process${e.rightExpression.class.simpleName}"(e.rightExpression)
        }
    }

    def processVariableExpression(VariableExpression v,declaringVariable) {

        //println "name:${v.name} - class:${classVariableNames} - scope:${variableScoping.peek()} - decl:${declaringVariable}"
        if (!variableScoping.peek().contains(v.name) && classVariableNames?.contains(v.name) && !declaringVariable) {
            addScript('this.'+v.name)
        } else {
            addScript(v.name)
        }
    }

    def processVariableExpression(VariableExpression v) {
        processVariableExpression(v,false)
    }

    /**
     *
     * @param b
     * @return
     */
    def processBinaryExpression(BinaryExpression b) {

        //println 'Binary->'+b.text
        //Getting a range from a list
        if (b.operation.text=='[' && b.rightExpression instanceof RangeExpression) {
            addScript('gSrangeFromList(')
            upgradedExpresion(b.leftExpression)
            addScript(", ")
            "process${b.rightExpression.getFrom().class.simpleName}"(b.rightExpression.getFrom())
            addScript(", ")
            "process${b.rightExpression.getTo().class.simpleName}"(b.rightExpression.getTo())
            addScript(')')
        //Adding items
        } else if (b.operation.text=='<<') {

            upgradedExpresion(b.leftExpression)
            addScript('.add(')
            upgradedExpresion(b.rightExpression)
            addScript(')')
        } else {

            //Left
            upgradedExpresion(b.leftExpression)
            //Operator
            //println 'Operator->'+b.operation.text
            addScript(' '+b.operation.text+' ')
            //Right
            upgradedExpresion(b.rightExpression)
            if (b.operation.text=='[') {
                addScript(']')
            }
        }
    }

    //Adding () for operators order, can spam loads of ()
    def upgradedExpresion(expresion) {
        if (expresion instanceof BinaryExpression) {
            addScript('(')
        }
        "process${expresion.class.simpleName}"(expresion)
        if (expresion instanceof BinaryExpression) {
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

    def processConstantExpression(ConstantExpression c,boolean addStuff) {
        if (c.value instanceof String && addStuff) {
            processConstantExpression(c)
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

    def processConstructorCallExpression(ConstructorCallExpression cce) {

        //No super
        //if (expression?.isSuperCall()) {
        //No this?
        //} else if (expression?.isThisCall()) {

        //addScript("gsCreate${cce.type.name}")
        //"process${cce.arguments.class.simpleName}"(cce.arguments)

        //Constructor have name witn number of params on it
        addScript("gsCreate${cce.type.name}().${cce.type.name}${cce.arguments.expressions.size()}")
        "process${cce.arguments.class.simpleName}"(cce.arguments)
    }

    def processArgumentListExpression(ArgumentListExpression al) {
        addScript '('
        int count = al?.expressions?.size()
        al.expressions?.each {
            "process${it.class.simpleName}"(it)
            count--
            if (count) addScript ', '
        }
        addScript ')'
    }

    def processPropertyExpression(PropertyExpression pe) {
        "process${pe.objectExpression.class.simpleName}"(pe.objectExpression)
        addScript('.')
        "process${pe.property.class.simpleName}"(pe.property,false)

    }

    def processMethodCallExpression(MethodCallExpression mc) {
        //println "MCE ${mc.objectExpression} - ${mc.methodAsString}"
        //Change println for javascript function
        if (mc.methodAsString == 'println') {
            addScript(printlnFunction)
        //Remove call method call from closures
        } else if (mc.methodAsString == 'call') {
            "process${mc.objectExpression.class.simpleName}"(mc.objectExpression)
        } else {
            "process${mc.objectExpression.class.simpleName}"(mc.objectExpression)
            addScript(".${mc.methodAsString}")
        }
        "process${mc.arguments.class.simpleName}"(mc.arguments)
    }

    def processPostfixExpression(PostfixExpression p) {
        "process${p.expression.class.simpleName}"(p.expression)
        addScript(p.operation.text)
    }

    def processReturnStatement(ReturnStatement r) {
        addScript('return ')
        "process${r.expression.class.simpleName}"(r.expression)
    }

    def processClosureExpression(ClosureExpression c) {

        addScript("function(")

        boolean first = true
        variableScoping.push([])
        c.parameters?.each { param ->
            if (!first) {
                addScript(', ')
            }
            variableScoping.peek().add(param.name)
            addScript(param.name)
            first = false
        }
        addScript(') {')
        indent++
        addLine()

        //println 'Method '+name+' Code:'+method.code
        if (c.code instanceof BlockStatement) {
            processBlockStament(c.code,true)
        } else {
            GsConsole.error("Closure Code not supported (${c.code.class.simpleName})")
        }

        indent--
        removeTabScript()
        addScript('}')

    }

    def processIfStatement(IfStatement is) {
        addScript('if (')
        "process${is.booleanExpression.class.simpleName}"(is.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        processBlockStament(is.ifBlock,false)
        indent--
        removeTabScript()
        addScript('}')
        if (is.elseBlock) {
            addScript(' else {')
            indent++
            addLine()
            processBlockStament(is.elseBlock,false)
            indent--
            removeTabScript()
            addScript('}')
        }
    }

    def processMapExpression(MapExpression me) {
        addScript('gSmap()')
        me.mapEntryExpressions?.each { ep ->
            addScript(".add(");
            "process${ep.keyExpression.class.simpleName}"(ep.keyExpression)
            addScript(",");
            "process${ep.valueExpression.class.simpleName}"(ep.valueExpression)
            addScript(")");
        }
    }

    def processListExpression(ListExpression l) {
        addScript('gSlist([')
        //println 'List->'+l.expressions
        //l.each { println it}
        def first = true
        l?.expressions?.each { it ->
            if (!first) {
                addScript(' , ')
            } else {
                first = false
            }
            "process${it.class.simpleName}"(it)
        }
        addScript('])')
    }

    def processRangeExpression(RangeExpression r) {
        addScript('gSrange(')

        //println 'Is inclusive->'+r.isInclusive()
        "process${r.from.class.simpleName}"(r.from)
        addScript(", ")
        "process${r.to.class.simpleName}"(r.to)
        addScript(', '+r.isInclusive())
        addScript(')')
    }

    def processForStatement(ForStatement statement) {

        //????
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //println 'DUMMY!-'+statement.variable
            //We change this for in...  for a call lo closure each, that works fine in javascript
            //"process${statement.variable.class.simpleName}"(statement.variable)
            //addScript ' in '

            "process${statement?.collectionExpression?.class.simpleName}"(statement?.collectionExpression)
            addScript('.each(function(')
            "process${statement.variable.class.simpleName}"(statement.variable)

            /*
            gSrange(5, 9).each(function(element) {
                console.log('it element->'+element);
                log += element;
            })
            */
        } else {
            addScript 'for ('
            //println 'collectionExpression-'+ statement?.collectionExpression.text
            "process${statement?.collectionExpression?.class.simpleName}"(statement?.collectionExpression)
        }
        addScript ') {'
        indent++
        addLine()

        "process${statement?.loopBlock?.class.simpleName}"(statement?.loopBlock)

        indent--
        removeTabScript()
        addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            addScript(')')
        }
    }

    def processClosureListExpression(ClosureListExpression expression) {
        //println 'ClosureListExpression-'+expression.text
        boolean first = true
        expression?.expressions?.each { it ->
            if (!first) {
                addScript(' ; ')
            }
            first = false
            "process${it.class.simpleName}"(it)
        }
    }

    def processParameter(Parameter parameter) {
        addScript(parameter.name)
    }

    def methodMissing(String name, Object args) {
        if (name?.startsWith('process')) {
            GsConsole.error('Conversion not supported for '+name.substring(7))
        } else {
            GsConsole.error('Error methodMissing '+name)
        }

    }

}

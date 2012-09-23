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
    def String resultScript
    def Stack<String> classNameStack = new Stack<String>()
    def Stack<String> superNameStack = new Stack<String>()
    //Use for variable scoping
    def Stack variableScoping = new Stack()
    def actualScope = []
    def String gSgotResultStatement = 'gSgotResultStatement'
    def String superMethodBegin = 'super_'

    def inheritedVariables = [:]
    //def methodVariableNames
    //def scriptScope

    //We get this function names from functions.groovy
    def assertFunction
    def printlnFunction

    //When true, we dont add this no variables
    //TODO remove this variable properly
    //def dontAddMoreThis

    def GsConverter() {
        initFunctionNames()
    }

    def initFunctionNames() {
        //def clos = new GroovyShell().evaluate('{ gscript ->\n'+Util.getNameFunctionsText()+'\n return gscript}')
        //this.with clos
        def clos = new GroovyShell().evaluate('{ it ->\n'+Util.getNameFunctionsText()+'\n return}')
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
        def phase = 0
        if (script) {

            try {
                def list = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS,script)
                phase++
                result = processAstListToJs(list)
            } catch (e) {
                if (phase==0) {
                    throw new Exception("Compiler ERROR on Script")
                } else {
                    throw e
                }
            }
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
            //Store all classes here
            def classList = []
            //We process blocks at the end
            def listBlocks = []
            list.each { it ->
                //println 'it->'+it
                if (it instanceof BlockStatement) {
                    //scriptScope = true
                    listBlocks << it
                    //processBlockStament(it,false)
                } else if (it instanceof ClassNode) {
                    //scriptScope = false
                    classList << it
                    //processClassNode(it)
                } else {
                    GsConsole.error("AST Node not supported (${it.class.simpleName}).")
                }
            }
            //Process list of classes
            if (classList) {
                processClassList(classList)
            }
            //Process blocks after
            listBlocks?.each { it->
                processBlockStament(it,false)
            }

            result = resultScript
        }
        //println 'res->'+ result
        result
    }

    //Process list of classes in correct order, inheritance order
    //Save list of variables for inheritance
    def processClassList(List<ClassNode> list) {

        def finalList = []
        def extraClasses = []
        while ((finalList.size()+extraClasses.size())<list.size()) {

            list.each { ClassNode it ->
                //println 'it->'+it.name+' super - '+it.superClass.name+
                if (it.superClass.name=='java.lang.Object')  {
                    if (!finalList.contains(it.name)) {
                        //println 'Adding '+it.name
                        finalList.add(it.name)
                    }
                } else {
                    //Expando allowed
                    if (it.superClass.name=='groovy.lang.Script') {
                        extraClasses.add(it.name)
                    } else {
                        //Looking for superclass, only accepts superclass a class in same script
                        if (it.superClass.name.indexOf('.')>=0) {
                            throw new Exception('Inheritance not Allowed on '+it.superClass.class.name)
                        }

                        //If father in the list, we can add it
                        if (finalList.contains(it.superClass.name)) {
                            //println 'Adding 2 '+it.name
                            finalList.add(it.name)
                        }
                    }
                }

            }
        }
        //Finally process classes in order
        finalList.each { String nameClass ->
            //println 'Class->'+nameClass
            processClassNode(list.find { ClassNode it ->
                return it.name == nameClass
            })
        }
        //Expandos - Nothing to do!
        extraClasses.each { String nameClass ->
            //println 'Class->'+nameClass
            processScriptClassNode(list.find { ClassNode it ->
                return it.name == nameClass
            })
        }
    }

    /**
     * Create code the js class definition, for execute constructor
     * @param numberArguments
     * @param paramList
     * @return
     */
    def addConditionConstructorExecution(numberArguments,paramList) {

        addScript("if (arguments.length==${numberArguments}) {")
        addScript("gSobject.${classNameStack.peek()}${numberArguments}")

        addScript '('
        def count = 0
        paramList?.each { param ->
            //"process${param.class.simpleName}"(param)
            //count--
            if (count>0) addScript ', '
            addScript("arguments[${count}]")
            count++
        }
        addScript ')'

        addScript('; }')
        addLine()
    }

    def translateClassName(String name) {
        def result = name
        def i
        while ((i = result.indexOf('.'))>=0) {
            result = result.substring(i+1)
        }

        result
    }

    def processScriptClassNode(ClassNode node) {

        //Push name in stack
        variableScoping.push([])

        addLine()

        //Adding initial values of properties
        /*
        node?.properties?.each { it->
            println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (it.initialExpression) {
                addScript("gSobject.${it.name} = ")
                "process${it.initialExpression.class.simpleName}"(it.initialExpression)
                addScript(';')
                addLine()
            } else {
                addScript("gSobject.${it.name} = null;")
                addLine()
            }

            //We add variable names of the class
            variableScoping.peek().add(it.name)
        }*/

        //Methods
        node?.methods?.each {

            //println 'method->'+it.name;

            if (it.name!='main' && it.name!='run') {

                //Add too method names to variable scoping
                variableScoping.peek().add(it.name)

                processBasicFunction(it.name,it,false)

                //processMethodNode(it,false)
            }
        }

        addLine()

        //Remove variable class names from the list
        variableScoping.pop()

    }

    def processClassNode(ClassNode node) {

        //Starting class conversion

        //Ignoring annotations
        //node?.annotations?.each {}

        //Ignoring modifiers
        //visitModifiers(node.modifiers)

        //println "class-> $node.name"
        addLine()

        //Push name in stack
        classNameStack.push(node.name)
        variableScoping.push([])

        addScript("function gsCreate${translateClassName(node.name)}() {")

        indent ++
        addLine()

        superNameStack.push(node.superClass.name)

        //Allowed inheritance
        if (node.superClass.name != 'java.lang.Object') {
            //println 'Allowed!'+ node.superClass.class.name
            addScript("var gSobject = gsCreate${translateClassName(node.superClass.name)}();")

            //We add to this class scope variables of fathers
            variableScoping.peek().addAll(inheritedVariables[node.superClass.name])
        } else {
            addScript('var gSobject = inherit(gsClass);')
        }
        addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass

        //Adding initial values of properties
        node?.properties?.each { it-> //println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (it.initialExpression) {
                addScript("gSobject.${it.name} = ")
                "process${it.initialExpression.class.simpleName}"(it.initialExpression)
                addScript(';')
                addLine()
            } else {
                addScript("gSobject.${it.name} = null;")
                addLine()
            }

            //We add variable names of the class
            variableScoping.peek().add(it.name)
        }

        //Save variables from this class for use in 'son' classes
        inheritedVariables.put(node.name,variableScoping.peek())
        //Ignoring fields
        //node?.fields?.each { println 'field->'+it  }

        //Methods
        node?.methods?.each { //println 'method->'+it;

            //Add too method names to variable scoping
            variableScoping.peek().add(it.name)

            processMethodNode(it,false)
        }

        //Constructors
        //If no constructor with 1 parameter, we create 1 that get a map, for put value on properties
        boolean has1parameterConstructor = false
        //boolean has0parameterConstructor = false
        node?.declaredConstructors?.each { //println 'declaredConstructor->'+it;
            def numberArguments = it.parameters?.size()
            if (numberArguments==1) {
                has1parameterConstructor = true
            }
            //if (it.parameters?.size()==0) {
            //    has0parameterConstructor = true
            //}
            processMethodNode(it,true)

            addConditionConstructorExecution(numberArguments,it.parameters)

        }
        if (!has1parameterConstructor) {
            addScript("gSobject.${translateClassName(node.name)}1 = function(map) { gSpassMapToObject(map,this); return this;};")
            addLine()
            addScript("if (arguments.length==1) {gSobject.${translateClassName(node.name)}1(arguments[0]); }")
            addLine()
        }
        //if (!has0parameterConstructor) {
        //    addScript("object.${node.name}0 = function() { };")
        //    addLine()
        //}

        indent --
        addScript("return gSobject;")
        addLine()
        addScript('}')
        addLine()

        //Remove variable class names from the list
        variableScoping.pop()

        //Pop name in stack
        classNameStack.pop()
        superNameStack.pop()

        //Finish class conversion
    }

    def processBasicFunction(name, method, isConstructor) {

        addScript("$name = function(")

        boolean first = true
        actualScope = []
        method.parameters?.each { param ->
            if (!first) {
                addScript(', ')
            }
            actualScope.add(param.name)
            addScript(param.name)
            first = false
        }
        addScript(') {')

        indent++
        addLine()

        //println 'Method '+name+' Code:'+method.code
        if (method.code instanceof BlockStatement) {
            processBlockStament(method.code,true)
        } else {
            GsConsole.error("Method Code not supported (${method.code.class.simpleName})")
        }

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

        processBasicFunction("gSobject.$name",method,isConstructor)
        /*
        addScript("gSobject.$name = function(")

        boolean first = true
        actualScope = []
        method.parameters?.each { param ->
            if (!first) {
                addScript(', ')
            }
            actualScope.add(param.name)
            addScript(param.name)
            first = false
        }
        addScript(') {')

        indent++
        addLine()

        //println 'Method '+name+' Code:'+method.code
        if (method.code instanceof BlockStatement) {
            processBlockStament(method.code,true)
        } else {
            GsConsole.error("Method Code not supported (${method.code.class.simpleName})")
        }
        */

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
        actualScope = []

        //method.parameters?.each { param ->
        //    methodVariableNames.remove(param.name)
        //}

        /*
        indent--
        if (isConstructor) {
            addScript('return this;')
            addLine()
        } else {
            removeTabScript()
        }
        addScript('}')
        addLine()
        */
    }

    /**
     * Process an AST Block
     * @param block
     * @param addReturn put 'return ' before last statement
     * @return
     */
    def processBlockStament(block,addReturn) {
        if (block) {
            def number = 1
            //println 'Block->'+block
            if (block instanceof EmptyStatement) {
                println 'BlockEmpty->'+block.text
                println 'Empty->'+block.getStatementLabel()
            } else {
                block.getStatements()?.each { it ->
                    def position
                    if (addReturn && ((number++)==block.getStatements().size()) && !(it instanceof ReturnStatement)
                            && !(it instanceof IfStatement) ) {
                        //this statement can be a complex statement with a return
                        //Go looking for a return statement in last statement
                        position = getSavePoint()
                        //We use actualScoping for getting return statement in this scope
                        variableScoping.peek().remove(gSgotResultStatement)
                    }
                    processStatement(it)
                    if (position && !variableScoping.peek().contains(gSgotResultStatement)) {
                        //No return statement, then we want add return
                        //println 'Yes!'+position
                        addScriptAt('return ',position)
                    }
                }
            }
        }
    }

    //???? there are both used
    def processBlockStatement(block) {
        processBlockStament(block,false)
    }

    /**
     * Add a line to javascript output
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

    /**
     * Add a text to javascript output
     * @param text
     * @return
     */
    def addScript(text) {
        //println 'adding ->'+text
        //indent.times { resultScript += TAB }
        resultScript += text
    }

    /**
     * Add text to javascript output at some position
     * @param text
     * @param position
     * @return
     */
    def addScriptAt(text,position) {
        resultScript = resultScript.substring(0,position) + text + resultScript.substring(position)
    }

    /**
     * Remove a TAB from current javascript output
     * @return
     */
    def removeTabScript() {
        resultScript = resultScript[0..resultScript.size()-1-TAB.size()]
    }

    /**
     * Get actual position in javascript output
     * @return
     */
    def getSavePoint() {
        return resultScript.size()
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

    def processAssertStatement(AssertStatement statement) {
        Expression e = statement.booleanExpression
        addScript(assertFunction)
        addScript('(')
        "process${e.class.simpleName}"(e)
        if (statement.getMessageExpression() && !(statement.messageExpression instanceof EmptyExpression)) {
            addScript(', ')
            "process${statement.messageExpression.class.simpleName}"(statement.messageExpression)
        }
        addScript(')')
    }

    def processBooleanExpression(BooleanExpression expression) {
        //println 'BooleanExpression->'+e
        //println 'BooleanExpression Inside->'+e.expression

        "process${expression.expression.class.simpleName}"(expression.expression)
    }

    def processExpressionStatement(ExpressionStatement statement) {
        Expression e = statement.expression
        "process${e.class.simpleName}"(e)
    }

    def processDeclarationExpression(DeclarationExpression expression) {
        //println 'l->'+e.leftExpression
        //println 'r->'+e.rightExpression
        //println 'v->'+e.variableExpression

        actualScope.add(expression.variableExpression.name)

        addScript('var ')
        processVariableExpression(expression.variableExpression)



        if (!(expression.rightExpression instanceof EmptyExpression)) {
            addScript(' = ')
            "process${expression.rightExpression.class.simpleName}"(expression.rightExpression)
        } else {
            addScript(' = null')
        }
    }

    def processVariableExpression(VariableExpression expression) {

        //println "name:${v.name} - class:${classVariableNames} - scope:${variableScoping.peek()} - decl:${declaringVariable}"
        //if (!variableScoping.peek().contains(v.name) && !declaringVariable &&!dontAddMoreThis && variableScoping.size()>1) {
        if (variableScoping.peek().contains(expression.name) && !actualScope.contains(expression.name)) {
            addScript('gSobject.'+expression.name)
        } else {
            addScript(expression.name)
        }
    }

    /**
     *
     * @param b
     * @return
     */
    def processBinaryExpression(BinaryExpression expression) {

        //println 'Binary->'+expression.text
        //Getting a range from a list
        if (expression.operation.text=='[' && expression.rightExpression instanceof RangeExpression) {
            addScript('gSrangeFromList(')
            upgradedExpresion(expression.leftExpression)
            addScript(", ")
            "process${expression.rightExpression.getFrom().class.simpleName}"(expression.rightExpression.getFrom())
            addScript(", ")
            "process${expression.rightExpression.getTo().class.simpleName}"(expression.rightExpression.getTo())
            addScript(')')
        //Adding items
        } else if (expression.operation.text=='<<') {
            //We call add function
            upgradedExpresion(expression.leftExpression)
            addScript('.add(')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        //Regular Expression exact match all
        } else if (expression.operation.text=='==~') {
            addScript('gSexactMatch(')
            upgradedExpresion(expression.leftExpression)
            addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                addScript('/')
                processConstantExpression(expression.rightExpression,false)
                addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            addScript(')')
        //A matcher of regular expresion
        } else if (expression.operation.text=='=~') {
            addScript('gSregExp(')
            //println 'rx->'+expression.leftExpression
            upgradedExpresion(expression.leftExpression)
            addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                addScript('/')
                processConstantExpression(expression.rightExpression,false)
                addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            addScript(')')
        } else {

            //Left
            upgradedExpresion(expression.leftExpression)
            //Operator
            //println 'Operator->'+b.operation.text
            addScript(' '+expression.operation.text+' ')
            //Right
            upgradedExpresion(expression.rightExpression)
            if (expression.operation.text=='[') {
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

    def processConstantExpression(ConstantExpression expression) {
        //println 'ConstantExpression->'+expression.text
        if (expression.value instanceof String) {
            def String value = ''
            expression.value.eachLine { if (it) value += it }
            addScript('"'+value+'"')
        } else {
            addScript(expression.value)
        }

    }

    def processConstantExpression(ConstantExpression expression,boolean addStuff) {
        if (expression.value instanceof String && addStuff) {
            processConstantExpression(expression)
        } else {
            addScript(expression.value)
        }

    }

    /**
     * Finally GString is something like String + Value + String + Value + String....
     * So we convert to "  " + value + "    " + value ....
     * @param e
     * @return
     */
    def processGStringExpression(GStringExpression expression) {

        def number = 0
        expression.getStrings().each {   exp ->
            if (number>0) {
                addScript(' + ')
            }
            //addScript('"')
            "process${exp.class.simpleName}"(exp)
            //addScript('"')

            if (expression.getValues().size() > number) {
                addScript(' + ')
                "process${expression.getValue(number).class.simpleName}"(expression.getValue(number))
            }
            number++
        }
    }

    def processNotExpression(NotExpression expression) {
        addScript('!')
        "process${expression.expression.class.simpleName}"(expression.expression)
    }

    def processConstructorCallExpression(ConstructorCallExpression expression) {

        //Super expression in constructor is allowed
        if (expression?.isSuperCall()) {

            addScript("this.${superNameStack.peek()}${expression.arguments.expressions.size()}")
        } else if (expression.type.name=='java.util.Date') {
            addScript('gSdate')
        } else if (expression.type.name=='groovy.util.Expando') {
            addScript('gScreateExpando')
        } else {
            //Constructor have name with number of params on it
            //addScript("gsCreate${expression.type.name}().${expression.type.name}${expression.arguments.expressions.size()}")
            def name = translateClassName(expression.type.name)
            addScript("gsCreate${name}")
        }
        "process${expression.arguments.class.simpleName}"(expression.arguments)
    }

    def processArgumentListExpression(ArgumentListExpression expression) {
        addScript '('
        int count = expression?.expressions?.size()
        expression.expressions?.each {
            "process${it.class.simpleName}"(it)
            count--
            if (count) addScript ', '
        }
        addScript ')'
    }

    def processPropertyExpression(PropertyExpression expression) {

        //println 'Pe->'+expression.objectExpression

        //If metaClass property we ignore it, javascript permits add directly properties and methods
        if (expression.property instanceof ConstantExpression && expression.property.value == 'metaClass') {
            if (expression.objectExpression instanceof VariableExpression) {
                //I had to add variable = ... cause gSmetaClass changing object and sometimes variable don't change
                addScript("(${expression.objectExpression.name} = gSmetaClass(")
                "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
                addScript('))')
            } else {
                addScript('gSmetaClass(')
                "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
                addScript(')')
            }
        } else {

            if (expression.objectExpression instanceof VariableExpression) {
                if (expression.objectExpression.name == 'this') {
                    //dontAddMoreThis = true
                }
            }

            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
            if (expression.property instanceof GStringExpression) {
                addScript('[')
                "process${expression.property.class.simpleName}"(expression.property)
                addScript(']')
            } else {
                addScript('.')
                "process${expression.property.class.simpleName}"(expression.property,false)
            }
        }

        //dontAddMoreThis = false

    }

    def processMethodCallExpression(MethodCallExpression expression) {
        //println "MCE ${expression.objectExpression} - ${expression.methodAsString}"
        if (expression.objectExpression instanceof VariableExpression) {
            if (expression.objectExpression.name == 'this') {
                //dontAddMoreThis = true
            }
        }

        //Change println for javascript function
        if (expression.methodAsString == 'println' || expression.methodAsString == 'print') {
            addScript(printlnFunction)
        //Remove call method call from closures
        } else if (expression.methodAsString == 'call') {
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
        //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='super') {
            addScript("${superMethodBegin}${expression.methodAsString}")
        //Function times, with a number, have to put (number) in javascript
        } else if (expression.methodAsString == 'times' && expression.objectExpression instanceof ConstantExpression) {
            addScript('(')
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
            addScript(')')
            addScript(".${expression.methodAsString}")
        } else {
            //A lot of times come with this
            //println 'Maybe this->'+expression.objectExpression
            if (expression.objectExpression instanceof VariableExpression &&
                    expression.objectExpression.name == 'this' &&
                    variableScoping.peek()?.contains(expression.methodAsString)) {
                //Remove this and put gSobject for variable scoping
                addScript("gSobject.${expression.methodAsString}")
            } else {
                "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
                addScript(".${expression.methodAsString}")
            }
        }
        "process${expression.arguments.class.simpleName}"(expression.arguments)

        //dontAddMoreThis = false
    }

    def processPostfixExpression(PostfixExpression expression) {
        "process${expression.expression.class.simpleName}"(expression.expression)
        addScript(expression.operation.text)
    }

    def processPrefixExpression(PrefixExpression expression) {
        addScript(expression.operation.text)
        "process${expression.expression.class.simpleName}"(expression.expression)
    }

    def processReturnStatement(ReturnStatement statement) {
        variableScoping.peek().add(gSgotResultStatement)
        addScript('return ')
        "process${statement.expression.class.simpleName}"(statement.expression)
    }

    def processClosureExpression(ClosureExpression expression) {

        addScript("function(")

        boolean first = true
        actualScope = []
        //If no parameters, we add it by defaul
        if (!expression.parameters || expression.parameters.size()==0) {
            addScript('it')
            actualScope.add('it')
        } else {
            expression.parameters?.each { param ->
                if (!first) {
                    addScript(', ')
                }
                actualScope.add(param.name)
                addScript(param.name)
                first = false
            }
        }
        addScript(') {')
        indent++
        addLine()

        //println 'Method '+name+' Code:'+method.code
        if (expression.code instanceof BlockStatement) {
            processBlockStament(expression.code,true)
        } else {
            GsConsole.error("Closure Code not supported (${c.code.class.simpleName})")
        }

        indent--
        actualScope = []
        removeTabScript()
        addScript('}')

    }

    def processIfStatement(IfStatement statement) {
        addScript('if (')
        "process${statement.booleanExpression.class.simpleName}"(statement.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        if (statement.ifBlock instanceof BlockStatement) {
            processBlockStament(statement.ifBlock,false)
        } else {
            //println 'if2->'+ statement.ifBlock.text
            "process${statement.ifBlock.class.simpleName}"(statement.ifBlock)
            addLine()
        }

        indent--
        removeTabScript()
        addScript('}')
        if (statement.elseBlock && !(statement.elseBlock instanceof EmptyStatement)) {
            //println 'Else->'+statement.elseBlock.text
            addScript(' else {')
            indent++
            addLine()
            if (statement.elseBlock instanceof BlockStatement) {
                processBlockStament(statement.elseBlock,false)
            } else {
                //println 'if2->'+ statement.ifBlock.text
                "process${statement.elseBlock.class.simpleName}"(statement.elseBlock)
                addLine()
            }
            indent--
            removeTabScript()
            addScript('}')
        }
    }

    def processMapExpression(MapExpression expression) {
        addScript('gSmap()')
        expression.mapEntryExpressions?.each { ep ->
            addScript(".add(");
            "process${ep.keyExpression.class.simpleName}"(ep.keyExpression)
            addScript(",");
            "process${ep.valueExpression.class.simpleName}"(ep.valueExpression)
            addScript(")");
        }
    }

    def processListExpression(ListExpression expression) {
        addScript('gSlist([')
        //println 'List->'+l.expressions
        //l.each { println it}
        def first = true
        expression?.expressions?.each { it ->
            if (!first) {
                addScript(' , ')
            } else {
                first = false
            }
            "process${it.class.simpleName}"(it)
        }
        addScript('])')
    }

    def processRangeExpression(RangeExpression expression) {
        addScript('gSrange(')

        //println 'Is inclusive->'+r.isInclusive()
        "process${expression.from.class.simpleName}"(expression.from)
        addScript(", ")
        "process${expression.to.class.simpleName}"(expression.to)
        addScript(', '+expression.isInclusive())
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

    def processTryCatchStatement(TryCatchStatement statement) {
        //Try block
        addScript('try {')
        indent++
        addLine()

        "process${statement?.tryStatement.class.simpleName}"(statement?.tryStatement)

        indent--
        removeTabScript()
        //Catch block
        addScript('} catch (')
        if (statement?.catchStatements[0]) {
            "process${statement?.catchStatements[0].variable.class.simpleName}"(statement?.catchStatements[0].variable)
        } else {
            addScript('e')
        }
        addScript(') {')
        indent++
        addLine()
        //Only process first catch
        "process${statement?.catchStatements[0]?.class.simpleName}"(statement?.catchStatements[0])

        indent--
        removeTabScript()
        addScript('}')
    }

    def processCatchStatement(CatchStatement statement) {
        processBlockStament(statement.code,false)
    }

    def processTernaryExpression(TernaryExpression expression) {
        //println 'Ternary->'+expression.text
        addScript('(')
        "process${expression.booleanExpression.class.simpleName}"(expression.booleanExpression)
        addScript(' ? ')
        "process${expression.trueExpression.class.simpleName}"(expression.trueExpression)
        addScript(' : ')
        "process${expression.falseExpression.class.simpleName}"(expression.falseExpression)
        addScript(')')
    }

    def processSwitchStatement(SwitchStatement statement) {

        addScript('switch (')
        "process${statement.expression.class.simpleName}"(statement.expression)
        addScript(') {')
        indent++
        addLine()
        statement.caseStatements?.each { it ->
            "process${it.class.simpleName}"(it)
            //addScript('break;')
        }
        if (statement.defaultStatement) {
            addScript('default :')
            "process${statement.defaultStatement.class.simpleName}"(statement.defaultStatement)
        }
        indent--
        removeTabScript()
        addScript('}')
        //addLine()
    }

    def processCaseStatement(CaseStatement statement) {
        addScript 'case '
        "process${statement?.expression.class.simpleName}"(statement?.expression)
        addScript ':'
        indent++
        addLine()
        "process${statement?.code.class.simpleName}"(statement?.code)
        indent--
        removeTabScript()
        //addLine()
    }

    def processBreakStatement(BreakStatement statement) {
        addScript('break')
        //addLine()
    }

    def processWhileStatement(WhileStatement statement) {
        addScript('while (')
        "process${statement.booleanExpression.class.simpleName}"(statement.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        "process${statement.loopBlock.class.simpleName}"(statement.loopBlock)
        indent--
        removeTabScript()
        addScript('}')
    }

    def methodMissing(String name, Object args) {
        def message
        if (name?.startsWith('process')) {
            message = 'Conversion not supported for '+name.substring(7)
        } else {
            message = 'Error methodMissing '+name
        }
        GsConsole.error(message)
        throw new Exception(message)

    }

}

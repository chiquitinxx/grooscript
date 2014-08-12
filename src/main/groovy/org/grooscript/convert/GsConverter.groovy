package org.grooscript.convert

import org.grooscript.GrooScript

import static org.grooscript.JsNames.*

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.grooscript.util.GsConsole
import org.grooscript.util.Util

/**
 * JFL 27/08/12
 */
class GsConverter {

    ConversionFactory conversionFactory
    Context context
    Out out
    Functions functions

    //Adds a console info if activated
    def consoleInfo = false

    //Conversion Options
    boolean convertDependencies = true
    Closure customization = null
    def classPath = null
    List<String> mainContextScope
    String initialText
    String finalText
    String includeJsLib

    /**
     * Converts Groovy script to Javascript
     * @param String script in groovy
     * @return String script in javascript
     */
    def toJs(String script) {
        def result
        //Script not empty plz!
        def phase = 0
        if (script) {
            try {
                if (consoleInfo) {
                    GsConsole.message('Getting ast from code...')
                }
                def (astList, nativeFunctions) = new AstTreeGenerator(consoleInfo: consoleInfo,
                        convertDependencies: convertDependencies,
                        classPath: classPath, customization: customization).fromText(script)

                if (consoleInfo) {
                    GsConsole.message('Processing AST...')
                }

                phase++
                result = processAstListToJs(astList, nativeFunctions + Util.getNativeFunctions(script))

                if (consoleInfo) {
                    GsConsole.message('Code processed.')
                }
            } catch (e) {
                e.printStackTrace()
                GsConsole.error("Error getting AST from script " +
                        "(Groovy: ${Util.groovyVersion} Grooscript: ${Util.grooscriptVersion}): " + e.message)
                if (phase == 0) {
                    throw new Exception("Compiler ERROR on Script -" + e.message)
                } else {
                    throw new Exception("Compiler END ERROR on Script -" + e.message)
                }
            }
        }

        completeJsResult(result)
    }

    private completeJsResult(String result) {
        if (initialText) {
            result = initialText + '\n' + result
        }
        if (finalText) {
            result = result + '\n' + finalText
        }
        if (includeJsLib) {
            def file = GrooScript.classLoader.getResourceAsStream("META-INF/resources/${includeJsLib}.js")
            if (file) {
                result = file.text + '\n' + result
            }
        }
        result
    }

    /**
     * Process an AST List from Groovy code to javascript script
     * @param list
     * @return
     */
    def processAstListToJs(list, nativeFunctions = null) {
        def result
        if (list && list.size() > 0) {
            //println '-----------------Size('+list.size+')->'+list
            conversionFactory = new ConversionFactory()
            conversionFactory.converter = this
            functions = conversionFactory.functions
            context = conversionFactory.context
            context.nativeFunctions = nativeFunctions
            out = conversionFactory.out

            if (mainContextScope) {
                mainContextScope.each { var ->
                    context.addToActualScope(var)
                }
            }

            //Store all methods here
            def methodList = []
            //Store all classes here
            def classList = []
            //We process blocks at the end
            def listBlocks = []
            list.each { it ->
                //println '------------------------------------it->'+it
                if (it instanceof BlockStatement) {
                    listBlocks << it
                } else if (it instanceof ClassNode) {
                    if (!it.isInterface()) {
                        classList << it
                    }
                } else if (it instanceof MethodNode) {
                    methodList << it
                } else {
                    GsConsole.error("AST Node not supported (${it?.class?.simpleName}).")
                }
            }

            //Process list of classes
            if (classList) {
                if (consoleInfo) {
                    GsConsole.message('Processing class list...')
                }
                processClassList(classList)
                if (consoleInfo) {
                    GsConsole.message('Done class list.')
                }
            }

            //Process list of methods
            methodList?.each { MethodNode methodNode ->
                if (consoleInfo) {
                    GsConsole.message('Processing method '+methodNode.name)
                }
                context.variableScoping.peek().add(methodNode.name)
                functions.processBasicFunction("var ${methodNode.name}", methodNode, false)
            }

            //Process blocks after
            listBlocks?.each { it->
                context.processingBaseScript = false
                conversionFactory.visitNode(it, false)
                if (context.processingBaseScript) {
                    out.indent --
                    out.removeTabScript()
                    out.addScript('});', true)
                    context.processingBaseScript = false
                }
            }

            result = out.resultScript
        }
        result
    }

    //Process list of classes in correct order, inheritance order
    //Save list of variables for inheritance
    private processClassList(List<ClassNode> list) {

        def finalList = []
        def extraClasses = []
        def enumClasses = []
        def numberOfElements = 0
        while (numberOfElements < list.size()) {
            list.each { ClassNode classNode ->
                //println 'it->'+it.name+' super - '+it.superClass.name
                if (classNode.superClass.name == 'java.lang.Object')  {
                    if (!finalList.contains(classNode.name)) {
                        //println 'Adding '+it.name+' - '+it.isInterface()
                        finalList.add(classNode.name)
                    }
                } else {
                    if (classNode.superClass.name == 'groovy.lang.Script' && !classNode.scriptBody) {
                        finalList.add(classNode.name)
                    } else if (classNode.superClass.name == 'groovy.lang.Script') {
                        extraClasses.add(classNode.name)
                    } else if (classNode.superClass.name == 'groovy.util.Expando') {
                        finalList.add(classNode.name)
                    } else {
                        //If father in the list, we can add it
                        if (classNode.scriptBody) {
                            extraClasses.add(classNode.name)
                        } else if (finalList.contains(classNode.superClass.name)) {
                            //println 'Adding 2 '+it.name+' - '+it.isInterface()
                            finalList.add(classNode.name)
                        } else {
                            //Looking for superclass, only accepts superclass a class in same script
                            if (classNode.superClass.name.startsWith('java.') ||
                                classNode.superClass.name.startsWith('groovy.')) {
                                if (classNode.superClass.name == 'java.lang.Enum') {
                                    enumClasses.add(classNode.name)
                                } else {
                                    throw new Exception('Inheritance not Allowed on ' + classNode.name)
                                }
                            }
                        }
                    }
                }
            }
            numberOfElements = finalList.size() + extraClasses.size() + enumClasses.size()
            if (numberOfElements == 0 && list.size() == 1) {
                finalList.add(list.first().name)
            }
        }
        //Finally process classes in order
        finalList.unique().each { String nameClass ->
            if (consoleInfo) {
                GsConsole.message('  Processing class ' + nameClass)
            }
            conversionFactory.visitNode(list.find { ClassNode it ->
                return it.name == nameClass
            })
            if (consoleInfo) {
                GsConsole.message('  Processing class done.')
            }
        }
        //Scripts
        extraClasses.unique().each { String nameClass ->
            def classNode = list.find { ClassNode it ->
                return it.name == nameClass
            }
            processScriptClassNode(classNode)
        }
        //Enums!
        enumClasses.each { String nameClass ->
            processEnum(list.find { ClassNode it ->
                return it.name == nameClass
            })
        }
    }

    private processScriptClassNode(ClassNode node) {

        //Push name in stack
        context.variableScoping.push([])
        context.actualScope.push([])

        out.addLine()

        //Methods
        node?.methods?.each {
            if (it.name!='main' && it.name!='run') {
                //Add too method names to variable scoping
                context.variableScoping.peek().add(it.name)
                functions.processBasicFunction(it.name, it, false)
            }
        }

        out.addLine()

        //Remove variable class names from the list
        context.variableScoping.pop()
        context.actualScope.pop()

    }

    private processConstructorNode(ConstructorNode method, isConstructor) {
        conversionFactory.getConverter('MethodNode').handle(method, isConstructor)
    }

    private processAssertStatement(AssertStatement statement) {
        Expression e = statement.booleanExpression
        out.addScript(GS_ASSERT)
        out.addScript('(')
        conversionFactory.visitNode(e)
        if (statement.getMessageExpression() && !(statement.messageExpression instanceof EmptyExpression)) {
            out.addScript(', ')
            conversionFactory.visitNode(statement.messageExpression)
        }
        out.addScript(')')
    }

    private processBooleanExpression(BooleanExpression expression) {
        //Groovy truth is a bit different, empty collections return false, we fix that here
        conversionFactory.handExpressionInBoolean(expression.expression)
    }

    private processExpressionStatement(ExpressionStatement statement) {
        Expression e = statement.expression
        conversionFactory.visitNode(e)
    }

    private processConstantExpression(ConstantExpression expression) {
        //println 'ConstantExpression->'+expression.text+'< '+expression.nullExpression
        if (expression.value instanceof String) {
            //println 'Value->'+expression.value+'<'+expression.value.endsWith('\n')
            String value = ''
            if (expression.value.startsWith('\n')) {
                value = '\\n'
            }
            def list = []
            expression.value.eachLine {
                if (it) list << it
            }
            value += list.join('\\n')
            value = value.replaceAll('"','\\\\"')
            //println 'After->'+value+'<'+value.endsWith('\n')
            if (expression.value.endsWith('\n') && !value.endsWith('\n') && value != '\\n') {
                value += '\\n'
            }
            out.addScript('"'+value+'"')
        } else {
            out.addScript(expression.value)
        }
    }

    private processConstantExpression(ConstantExpression expression, boolean addStuff) {
        if (expression.value instanceof String && addStuff) {
            processConstantExpression(expression)
        } else {
            out.addScript(expression.value)
        }

    }

    /**
     * Finally GString is something like String + Value + String + Value + String....
     * So we convert to "  " + value + "    " + value ....
     * @param e
     * @return
     */
    private processGStringExpression(GStringExpression expression) {

        def number = 0
        expression.getStrings().each {   exp ->
            //println 'Exp->'+exp
            if (number>0) {
                out.addScript(' + ')
            }
            conversionFactory.visitNode(exp)

            if (expression.getValues().size() > number) {
                out.addScript(' + (')
                conversionFactory.visitNode(expression.getValue(number))
                out.addScript(')')
            }
            number++
        }
    }

    private processNotExpression(NotExpression expression) {
        out.addScript('!')
        conversionFactory.visitNode(expression.expression)
    }

    private processArgumentListExpression(ArgumentListExpression expression, boolean withParenthesis) {
        if (withParenthesis) {
            out.addScript '('
        }
        int count = expression?.expressions?.size()
        expression.expressions?.each {
            conversionFactory.visitNode(it)
            count--
            if (count) {
                out.addScript ', '
            }
        }
        if (withParenthesis) {
            out.addScript ')'
        }
    }

    private processArgumentListExpression(ArgumentListExpression expression) {
        processArgumentListExpression(expression, true)
    }

    private addPlusPlusFunction(expression, isBefore) {

        //Only in mind ++ and --
        def plus = true
        if (expression.operation.text=='--') {
            plus = false
        }

        out.addScript("${GS_PLUS_PLUS}(")
        conversionFactory.processObjectExpressionFromProperty(expression.expression)
        out.addScript(',')
        conversionFactory.processPropertyExpressionFromProperty(expression.expression)

        out.addScript(",${plus},${isBefore?'true':'false'})")
    }

    private processPostfixExpression(PostfixExpression expression) {
        if (expression.operation.text in ['++','--'] && expression.expression instanceof PropertyExpression) {
            addPlusPlusFunction(expression, false)
        } else {
            context.postfixOperator = expression.operation.text
            conversionFactory.visitNode(expression.expression)
            context.postfixOperator = ''
        }
    }

    private processPrefixExpression(PrefixExpression expression) {
        if (expression.expression instanceof PropertyExpression) {
            addPlusPlusFunction(expression, true)
        } else {
            context.prefixOperator = expression.operation.text
            conversionFactory.visitNode(expression.expression)
            context.prefixOperator = ''
        }
    }

    private processReturnStatement(ReturnStatement statement) {
        context.returnScoping.add(true)
        out.addScript('return ')
        conversionFactory.visitNode(statement.expression)
    }

    private processClosureExpression(ClosureExpression expression) {
        processClosureExpression(expression, true)
    }

    private processClosureExpression(ClosureExpression expression, boolean addItDefault) {

        out.addScript("function(")

        context.processingClosure = true
        functions.putFunctionParametersAndBody(expression, false, addItDefault)
        context.processingClosure = false

        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processIfStatement(IfStatement statement) {
        out.addScript('if (')
        conversionFactory.visitNode(statement.booleanExpression)
        out.addScript(') {')

        processIfOrElseBlock(statement.ifBlock)

        if (statement.elseBlock && !(statement.elseBlock instanceof EmptyStatement)) {
            //println 'Else->'+statement.elseBlock.text
            out.addScript(' else {')
            processIfOrElseBlock(statement.elseBlock)
        }
    }

    private processIfOrElseBlock(block) {
        out.indent++
        out.addLine()
        if (block instanceof BlockStatement) {
            conversionFactory.visitNode(block, context.lookingForReturnStatementInIf)
        } else {
            if (context.lookingForReturnStatementInIf && conversionFactory.statementThatCanReturn(block)) {
                out.addScript('return ')
            }
            conversionFactory.visitNode(block)
            out.addScript(';', true)
        }
        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processMapExpression(MapExpression expression) {
        out.addScript("${GS_MAP}()")
        expression.mapEntryExpressions?.each { ep ->
            out.addScript(".add(");
            conversionFactory.visitNode(ep.keyExpression)
            out.addScript(",");
            conversionFactory.visitNode(ep.valueExpression)
            out.addScript(")");
        }
    }

    private processListExpression(ListExpression expression) {
        out.addScript("${GS_LIST}([")
        def first = true
        expression?.expressions?.each { it ->
            if (!first) {
                out.addScript(' , ')
            } else {
                first = false
            }
            conversionFactory.visitNode(it)
        }
        out.addScript('])')
    }

    private processRangeExpression(RangeExpression expression) {
        out.addScript("${GS_RANGE}(")
        conversionFactory.visitNode(expression.from)
        out.addScript(", ")
        conversionFactory.visitNode(expression.to)
        out.addScript(', '+expression.isInclusive())
        out.addScript(')')
    }

    private processForStatement(ForStatement statement) {

        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //We change this for in...  for a call lo closure each, that works fine in javascript
            conversionFactory.visitNode(statement?.collectionExpression)
            out.addScript('.each(function(')
            conversionFactory.visitNode(statement.variable)

        } else {
            out.addScript 'for ('
            conversionFactory.visitNode(statement?.collectionExpression)
        }
        out.addScript ') {'
        out.indent++
        out.addLine()

        conversionFactory.visitNode(statement?.loopBlock)

        out.indent--
        out.removeTabScript()
        out.addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            out.addScript(')')
        }
    }

    private processClosureListExpression(ClosureListExpression expression) {
        boolean first = true
        expression?.expressions?.each { it ->
            if (!first) {
                out.addScript(' ; ')
            }
            first = false
            conversionFactory.visitNode(it)
        }
    }

    private processParameter(Parameter parameter) {
        //println 'Initial->'+parameter.getInitialExpression()
        out.addScript(parameter.name)
    }

    private processTryCatchStatement(TryCatchStatement statement) {
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
        conversionFactory.visitNode(statement?.catchStatements[0])

        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processCatchStatement(CatchStatement statement) {
        conversionFactory.visitNode(statement.code, false)
    }

    private processTernaryExpression(TernaryExpression expression) {
        //println 'Ternary->'+expression.text
        out.addScript('(')
        conversionFactory.visitNode(expression.booleanExpression)
        out.addScript(' ? ')
        conversionFactory.visitNode(expression.trueExpression)
        out.addScript(' : ')
        conversionFactory.visitNode(expression.falseExpression)
        out.addScript(')')
    }

    private getSwitchExpression(Expression expression,String varName) {

        if (expression instanceof ClosureExpression) {
            context.addClosureSwitchInitialization = true
            processClosureExpression(expression,true)
            out.addScript('()')
        } else {
            out.addScript("${varName} === ")
            conversionFactory.visitNode(expression)
        }
    }

    private processSwitchStatement(SwitchStatement statement) {

        def varName = SWITCH_VAR_NAME + context.switchCount++

        out.addScript('var '+varName+' = ')
        conversionFactory.visitNode(statement.expression)
        out.addScript(';', true)

        def first = true

        statement.caseStatements?.each { it ->
            if (first) {
                out.addScript("if (")
                first = false
            } else {
                out.addScript("} else if (")
            }
            getSwitchExpression(it.expression,varName)
            out.addScript(') {')
            out.indent++
            out.addLine()
            conversionFactory.visitNode(it?.code)
            out.indent--
            out.removeTabScript()
        }
        if (statement.defaultStatement) {
            out.addScript('} else {')
            out.indent++
            out.addLine()
            conversionFactory.visitNode(statement.defaultStatement)
            out.indent--
            out.removeTabScript()
        }

        out.addScript('}')
        context.switchCount--
    }

    private processCaseStatement(CaseStatement statement) {
        out.addScript 'case '
        conversionFactory.visitNode(statement?.expression)
        out.addScript ':'
        out.indent++
        out.addLine()
        conversionFactory.visitNode(statement?.code)
        out.indent--
        out.removeTabScript()
    }

    private processBreakStatement(BreakStatement statement) {
        if (context.switchCount == 0) {
            out.addScript('break')
        }
    }

    private processWhileStatement(WhileStatement statement) {
        out.addScript('while (')
        conversionFactory.visitNode(statement.booleanExpression)
        out.addScript(') {')
        out.indent++
        out.addLine()
        conversionFactory.visitNode(statement.loopBlock)
        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processTupleExpression(TupleExpression expression, withParenthesis = true) {
        if (withParenthesis) {
            out.addScript('(')
        }
        expression.expressions.each {
            conversionFactory.visitNode(it)
        }
        if (withParenthesis) {
            out.addScript(')')
        }
    }

    private processNamedArgumentListExpression(NamedArgumentListExpression expression) {
        out.addScript("${GS_MAP}()")
        expression.mapEntryExpressions.eachWithIndex { MapEntryExpression exp,i ->
            out.addScript('.add(')
            conversionFactory.visitNode(exp.keyExpression)
            out.addScript(',')
            conversionFactory.visitNode(exp.valueExpression)
            out.addScript(')')
        }
    }

    private processBitwiseNegationExpression(BitwiseNegationExpression expression) {
        out.addScript("/${expression.text}/")
    }

    private processEnum(ClassNode node) {

        out.addLine()

        //Push name in stack
        context.variableScoping.push([])

        out.addScript("var ${node.nameWithoutPackage} = {")

        out.indent ++
        out.addLine()

        //out.addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass

        //Fields
        def number = 0
        node?.fields?.each { it->
            if (!['MIN_VALUE', 'MAX_VALUE', '$VALUES'].contains(it.name)) {
                out.addScript("${it.name} : { ordinal: function() { return ${number++}}, " +
                        "name: function() { return '${it.name}' } },", true)
                context.variableScoping.peek().add(it.name)
            }
        }

        //Methods
        node?.methods?.each { //println 'method->'+it;

            if (!['values', 'next', 'previous', 'valueOf', '$INIT', '<clinit>'].contains(it.name)) {

                context.variableScoping.peek().add(it.name)
                out.addScript("${it.name} : function(")
                functions.putFunctionParametersAndBody(it, false, true)

                out.indent--
                out.removeTabScript()
                out.addScript('},', true)
            }
        }

        out.indent --
        out.addLine()
        out.addScript('}', true)

        //Remove variable class names from the list
        context.variableScoping.pop()
    }

    private processClassExpression(ClassExpression expression) {
        out.addScript(conversionFactory.reduceClassName(expression.text))
    }

    private processThrowStatement(ThrowStatement statement) {
        out.addScript('throw "Exception"')
        //println 'throw expression'+statement.expression.text
    }

    private processStaticMethodCallExpression(StaticMethodCallExpression expression) {
        out.addScript("${conversionFactory.reduceClassName(expression.ownerType.name)}.${expression.method}")
        conversionFactory.visitNode(expression.arguments)
    }

    private processElvisOperatorExpression(ElvisOperatorExpression expression) {
        out.addScript("${GS_ELVIS}(")
        conversionFactory.visitNode(expression.booleanExpression)
        out.addScript(' , ')
        conversionFactory.visitNode(expression.trueExpression)
        out.addScript(' , ')
        conversionFactory.visitNode(expression.falseExpression)
        out.addScript(')')
    }

    private processAttributeExpression(AttributeExpression expression) {
        conversionFactory.getConverter('PropertyExpression').handle(expression)
    }

    private processSpreadExpression(SpreadExpression expression) {
        out.addScript("new ${GS_SPREAD}(")
        conversionFactory.visitNode(expression.expression)
        out.addScript(')')
    }

    private processSpreadMapExpression(SpreadMapExpression expression) {
        out.addScript("'${SPREAD_MAP}'")
    }

    private processEmptyExpression(EmptyExpression expression) {
        //Nothing to do
    }
}
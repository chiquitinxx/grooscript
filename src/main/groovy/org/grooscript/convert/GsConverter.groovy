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
package org.grooscript.convert

import org.grooscript.GrooScript
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.convert.util.RequireJsDependency
import org.grooscript.util.GrooScriptException
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.grooscript.util.GsConsole
import org.grooscript.util.Util

import static org.grooscript.JsNames.*

class GsConverter {

    ConversionFactory conversionFactory
    Context context
    Out out
    Functions functions

    //Conversion Options
    Map<String, Object> conversionOptions

    List<RequireJsDependency> requireJsDependencies = []

    /**
     * Converts Groovy script to Javascript
     * @param String script in groovy
     * @param Map conversion options
     * @return String script in javascript
     */
    String toJs(String script, Map options = null) {
        String result = null
        //Script not empty plz!
        def phase = 0
        if (script && validateConversionOptions(options)) {
            conversionOptions = options ?: GrooScript.defaultConversionOptions
            try {
                if (consoleInfo) {
                    GsConsole.message('Getting ast from code...')
                }
                def (astList, nativeFunctions) = new AstTreeGenerator(consoleInfo: consoleInfo,
                        classpath: conversionOptions[ConversionOptions.CLASSPATH.text],
                        customization: conversionOptions[ConversionOptions.CUSTOMIZATION.text]).fromText(script)

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

        result
    }

    /**
     * Process an AST List from Groovy code to javascript script
     * @param list
     * @return
     */
    String processAstListToJs(list, nativeFunctions = null) {
        def result
        if (list && list.size() > 0) {
            //println '-----------------Size('+list.size+')->'+list
            conversionFactory = new ConversionFactory()
            conversionFactory.converter = this
            functions = conversionFactory.functions
            context = conversionFactory.context
            context.nativeFunctions = nativeFunctions
            out = conversionFactory.out
            if (conversionOptions[ConversionOptions.REQUIRE_JS_MODULE.text] == true) {
                requireJsDependencies = []
                out.indent++
                out.addTab()
            }

            if (conversionOptions[ConversionOptions.MAIN_CONTEXT_SCOPE.text]) {
                conversionOptions[ConversionOptions.MAIN_CONTEXT_SCOPE.text].each { var ->
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

    public void addRequireJsDependency(String path, String name) {
        requireJsDependencies << new RequireJsDependency(path: path, name: name)
    }

    //Process list of classes in correct order, inheritance order
    //Save list of variables for inheritance
    private void processClassList(List<ClassNode> list) {

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
            conversionFactory.visitNode(list.find { ClassNode node ->
                return node.name == nameClass
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

        def wasProcessingClosure = context.processingClosure
        context.processingClosure = true
        functions.putFunctionParametersAndBody(expression, false, addItDefault)
        context.processingClosure = wasProcessingClosure

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

    private boolean isConsoleInfo() {
        conversionOptions && conversionOptions[ConversionOptions.CONSOLE_INFO.text] == true
    }

    private boolean validateConversionOptions(Map options) {
        options ? areCompatibles(options) : true
    }

    private boolean areCompatibles(Map options) {
        if (options[ConversionOptions.REQUIRE_JS_MODULE.text] == true &&
                options[ConversionOptions.INCLUDE_DEPENDENCIES.text] == true
        ) {
            throw new GrooScriptException("Incompatible conversion options " +
                    "(${ConversionOptions.REQUIRE_JS_MODULE.text} - ${ConversionOptions.INCLUDE_DEPENDENCIES.text})")
        }
        true
    }
}
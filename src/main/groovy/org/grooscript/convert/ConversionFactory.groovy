package org.grooscript.convert

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.grooscript.convert.handlers.*
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class ConversionFactory {

    def conversionClasses = [:]
    Context context
    Out out
    Functions functions
    def converter

    Map converters = [
            'VariableExpression': VariableExpressionHandler,
            'ClassNode': ClassNodeHandler,
            'BinaryExpression': BinaryExpressionHandler,
            'MethodCallExpression': MethodCallExpressionHandler,
            'PropertyExpression': PropertyExpressionHandler,
            'BlockStatement': BlockStatementHandler,
            'MethodNode': MethodNodeHandler,
            'ConstructorCallExpression': ConstructorCallExpressionHandler,
            'CastExpression': CastExpressionHandler,
            'ArrayExpression': ArrayExpressionHandler,
            'MethodPointerExpression': MethodPointerExpressionHandler,
            'InnerClassNode': InnerClassNodeHandler
    ]

    ConversionFactory() {
        context = new Context()
        out = new Out()
        functions = new Functions(conversionFactory: this)
    }

    void convert(ASTNode node, otherParam = null) {
        if (!context || !out) {
            throw new GrooScriptException('Need to define context and out in ConversionFactory.')
        }
        visitNode(node, otherParam)
    }

    BaseHandler getConverter(String className) {
        if (!conversionClasses[className]) {
            conversionClasses[className] =
                    improvedConversionHandler(className)
        }
        conversionClasses[className]
    }

    void visitNode(node, otherParam = null) {
        String className = node.class.simpleName
        if (!converters[className]) {
            if (otherParam != null) {
                converter."process${className}"(node, otherParam)
            } else {
                converter."process${className}"(node)
            }
        } else {
            if (otherParam) {
                getConverter(className).handle(node, otherParam)
            } else {
                getConverter(className).handle(node)
            }
        }
    }

    void convertBasicFunction(name, method, isConstructor) {

        out.addScript("$name = function(")

        putFunctionParametersAndBody(method, isConstructor, true)

        out.indent--
        if (isConstructor) {
            out.addScript('return this;', true)
        } else {
            out.removeTabScript()
        }
        out.addScript('}', true)
    }

    void putFunctionParametersAndBody(functionOrMethod, boolean isConstructor, boolean addItDefault) {

        context.actualScope.push([])

        convertFunctionOrMethodParameters(functionOrMethod, addItDefault)

        //println 'Closure '+expression+' Code:'+expression.code
        if (functionOrMethod.code instanceof BlockStatement) {
            visitNode(functionOrMethod.code, !isConstructor)
        } else {
            GsConsole.error("FunctionOrMethod Code not supported (${functionOrMethod.code.class.simpleName})")
        }

        context.actualScope.pop()
    }

    void convertFunctionOrMethodParameters(functionOrMethod, boolean addItInParameter) {

        boolean first = true
        boolean lastParameterCanBeMore = false

        //Parameters with default values if not shown
        def initalValues = [:]

        //If no parameters, we add it by defaul
        if (addItInParameter && (!functionOrMethod.parameters || functionOrMethod.parameters.size()==0)) {
            out.addScript('it')
            context.addToActualScope('it')
        } else {

            functionOrMethod.parameters?.eachWithIndex { Parameter param, index ->

                //If the last parameter is an Object[] then, maybe, can get more parameters as optional
                if (param.type.name=='[Ljava.lang.Object;' && index + 1 == functionOrMethod.parameters.size()) {
                    lastParameterCanBeMore = true
                }
                //println 'pe->'+param.toString()+' - '+param.type.name //+' - '+param.type

                if (param.getInitialExpression()) {
                    //println 'Initial->'+param.getInitialExpression()
                    initalValues.putAt(param.name, param.getInitialExpression())
                }
                if (!first) {
                    out.addScript(', ')
                }
                context.addToActualScope(param.name)
                out.addScript(param.name)
                first = false
            }
        }
        out.addScript(') {')
        out.indent++
        out.addLine()

        //At start we add initialization of default values
        initalValues.each { key, value ->
            out.addScript("if (${key} === undefined) ${key} = ")
            visitNode(value)
            out.addScript(';', true)
        }

        //We add initialization of it inside switch closure function
        if (context.addClosureSwitchInitialization) {
            def name = SWITCH_VAR_NAME + (context.switchCount - 1)
            out.addScript("if (it === undefined) it = ${name};", true)
            context.addClosureSwitchInitialization = false
        }

        if (lastParameterCanBeMore) {
            def Parameter lastParameter = functionOrMethod.parameters.last()
            out.addScript("if (arguments.length==${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([arguments[${functionOrMethod.parameters.size()}-1]]); }", true)
            out.addScript("if (arguments.length<${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([]); }", true)
            out.addScript("if (arguments.length>${functionOrMethod.parameters.size()}) {", true)
            out.addScript("  ${lastParameter.name}=${GS_LIST}([${lastParameter.name}]);", true)
            out.addScript("  for (${COUNT}=${functionOrMethod.parameters.size()};${COUNT} < arguments.length; ${COUNT}++) {", true)
            out.addScript("    ${lastParameter.name}.add(arguments[${COUNT}]);", true)
            out.addScript("  }", true)
            out.addScript("}", true)
        }
    }

    void handExpressionInBoolean(expression) {
        if (expression instanceof VariableExpression || expression instanceof PropertyExpression ||
                (expression instanceof NotExpression && expression.expression &&
                    (expression.expression instanceof VariableExpression || expression.expression instanceof PropertyExpression))) {
            if (expression instanceof NotExpression) {
                out.addScript("!${GS_BOOL}(")
                visitNode(expression.expression)
            } else {
                out.addScript("${GS_BOOL}(")
                visitNode(expression)
            }
            out.addScript(')')
        } else {
            visitNode(expression)
        }
    }

    void processObjectExpressionFromProperty(PropertyExpression expression) {
        if (expression.objectExpression instanceof ClassExpression) {
            out.addScript(expression.objectExpression.type.nameWithoutPackage)
        } else {
            visitNode(expression.objectExpression)
        }
    }

    void processPropertyExpressionFromProperty(PropertyExpression expression) {
        if (expression.property instanceof GStringExpression) {
            visitNode(expression.property)
        } else {
            out.addScript('"')
            visitNode(expression.property, false)
            out.addScript('"')
        }
    }

    boolean statementThatCanReturn(statement) {
        return !(statement instanceof ReturnStatement) &&
                !(statement instanceof IfStatement) && !(statement instanceof WhileStatement) &&
                !(statement instanceof AssertStatement) && !(statement instanceof BreakStatement) &&
                !(statement instanceof CaseStatement) && !(statement instanceof CatchStatement) &&
                !(statement instanceof ContinueStatement) && !(statement instanceof DoWhileStatement) &&
                !(statement instanceof ForStatement) && !(statement instanceof SwitchStatement) &&
                !(statement instanceof ThrowStatement) && !(statement instanceof TryCatchStatement) &&
                !(statement.metaClass.expression && statement.expression instanceof DeclarationExpression)
    }

    boolean isThis(expression) {
        expression instanceof VariableExpression && expression.name == 'this'
    }

    private Object improvedConversionHandler(String className) {
        BaseHandler instanceHandler = converters[className].newInstance()
        instanceHandler.out = out
        instanceHandler.context = context
        instanceHandler.functions = functions
        instanceHandler.factory = this
        instanceHandler
    }

    boolean isValidTraitMethodName(methodName) {
        !['$init$', '$static$init$'].contains(methodName)
    }

    String reduceClassName(String name) {
        def result = name
        def i = result.lastIndexOf('.')
        if (i > 0) {
            result = result.substring(i + 1)
        }
        result
    }
}

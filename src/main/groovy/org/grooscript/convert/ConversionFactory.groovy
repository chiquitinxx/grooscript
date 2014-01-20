package org.grooscript.convert

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.grooscript.convert.handlers.BaseHandler
import org.grooscript.convert.handlers.ClassNodeHandler
import org.grooscript.convert.handlers.VariableExpressionHandler
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class ConversionFactory {

    def conversionClasses = [:]
    def context
    def out
    def converter

    Map converters = [
            'VariableExpression': VariableExpressionHandler,
            'ClassNode': ClassNodeHandler
    ]

    ConversionFactory(Context context, Out out) {
        this.context = context
        this.out = out
    }

    void convert(ASTNode node) {
        if (!context || !out) {
            throw new GrooScriptException('Need to define context and out in ConversionFactory.')
        }
        String className = node.class.simpleName
        getConverter(className).handle(node)
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
                converter.visitNode(node)
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

        putFunctionParametersAndBody(method, isConstructor,true)

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
                if (param.type.name=='[Ljava.lang.Object;' && index+1 == functionOrMethod.parameters.size()) {
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

    private Object improvedConversionHandler(String className) {
        BaseHandler instanceHandler = converters[className].newInstance()
        instanceHandler.out = out
        instanceHandler.context = context
        instanceHandler.factory = this
        instanceHandler
    }
}

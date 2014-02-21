package org.grooscript.convert

import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.grooscript.util.GsConsole

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 26/01/14
 */
class Functions {

    ConversionFactory conversionFactory

    void processBasicFunction(name, method, isConstructor) {

        conversionFactory.out.addScript("$name = function(")

        putFunctionParametersAndBody(method, isConstructor, true)

        conversionFactory.out.indent--
        if (isConstructor) {
            conversionFactory.out.addScript('return this;', true)
        } else {
            conversionFactory.out.removeTabScript()
        }
        conversionFactory.out.addScript('}', true)
    }

    void putFunctionParametersAndBody(functionOrMethod, boolean isConstructor, boolean addItDefault) {

        conversionFactory.context.actualScope.push([])

        processFunctionOrMethodParameters(functionOrMethod, isConstructor, addItDefault)

        //println 'Closure '+expression+' Code:'+expression.code
        if (functionOrMethod.code instanceof BlockStatement) {
            conversionFactory.visitNode(functionOrMethod.code, !isConstructor)
        } else {
            GsConsole.error("FunctionOrMethod Code not supported (${functionOrMethod.code.class.simpleName})")
        }

        conversionFactory.context.actualScope.pop()
    }

    private processFunctionOrMethodParameters(functionOrMethod, boolean isConstructor,boolean addItInParameter) {

        boolean first = true
        boolean lastParameterCanBeMore = false

        //Parameters with default values if not shown
        def initalValues = [:]

        //If no parameters, we add it by defaul
        if (addItInParameter && (!functionOrMethod.parameters || functionOrMethod.parameters.size() == 0)) {
            conversionFactory.out.addScript('it')
            conversionFactory.context.addToActualScope('it')
        } else {

            functionOrMethod.parameters?.eachWithIndex { Parameter param, index ->

                //If the last parameter is an Object[] then, maybe, can get more parameters as optional
                if (param.type.name=='[Ljava.lang.Object;' && index + 1 == functionOrMethod.parameters.size()) {
                    lastParameterCanBeMore = true
                }
                //println 'pe->'+param.toString()+' - '+param.type.name //+' - '+param.type

                if (param.getInitialExpression()) {
                    //println 'Initial->'+param.getInitialExpression()
                    initalValues.putAt(param.name,param.getInitialExpression())
                }
                if (!first) {
                    conversionFactory.out.addScript(', ')
                }
                conversionFactory.context.addToActualScope(param.name)
                conversionFactory.out.addScript(param.name)
                first = false
            }
        }
        conversionFactory.out.addScript(') {')
        conversionFactory.out.indent++
        conversionFactory.out.addLine()

        //At start we add initialization of default values
        initalValues.each { key, value ->
            conversionFactory.out.addScript("if (${key} === undefined) ${key} = ")
            conversionFactory.visitNode(value)
            conversionFactory.out.addScript(';', true)
        }

        //We add initialization of it inside switch closure function
        if (conversionFactory.context.addClosureSwitchInitialization) {
            def name = SWITCH_VAR_NAME + (conversionFactory.context.switchCount - 1)
            conversionFactory.out.addScript("if (it === undefined) it = ${name};", true)
            conversionFactory.context.addClosureSwitchInitialization = false
        }

        if (lastParameterCanBeMore) {
            Parameter lastParameter = functionOrMethod.parameters.last()
            conversionFactory.out.addScript("if (arguments.length == ${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([arguments[${functionOrMethod.parameters.size()} - 1]]); }", true)
            conversionFactory.out.addScript("if (arguments.length < ${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([]); }", true)
            conversionFactory.out.addScript("if (arguments.length > ${functionOrMethod.parameters.size()}) {", true)
            conversionFactory.out.addScript("  ${lastParameter.name}=${GS_LIST}([${lastParameter.name}]);", true)
            conversionFactory.out.addScript("  for (${COUNT}=${functionOrMethod.parameters.size()};${COUNT} < arguments.length; ${COUNT}++) {", true)
            conversionFactory.out.addScript("    ${lastParameter.name}.add(arguments[${COUNT}]);", true)
            conversionFactory.out.addScript("  }", true)
            conversionFactory.out.addScript("}", true)
        }
    }
}

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

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.grooscript.util.GsConsole

import static org.grooscript.JsNames.*

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

    void processFunctionOrMethodParameters(functionOrMethod, boolean isConstructor,boolean addItInParameter) {

        boolean first = true
        boolean lastParameterCanBeMore = false

        //Parameters with default values if not shown
        def initalValues = [:]

        //If no parameters, we add it by defaul
        if (addItInParameter && (!functionOrMethod.parameters || functionOrMethod.parameters.size() == 0)) {
            conversionFactory.out.addScript('it')
            conversionFactory.context.addToActualScope('it')
        } else {

            functionOrMethod.parameters?.each { Parameter param ->

                //If the last parameter is an Object[] then, maybe, can get more parameters as optional
                if (isArray(param) && functionOrMethod.parameters.last() == param) {
                    lastParameterCanBeMore = true
                }

                if (param.getInitialExpression()) {
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
            conversionFactory.out.addScript("if (arguments.length == 1 && arguments[0] instanceof Array) { " +
                    "${lastParameter.name}=${GS_LIST}(arguments[0]); } else ", true)
            conversionFactory.out.addScript("if (arguments.length == ${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([arguments[${functionOrMethod.parameters.size()} - 1]]); } else ", true)
            conversionFactory.out.addScript("if (arguments.length < ${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([]); } else ", true)
            conversionFactory.out.addScript("if (arguments.length > ${functionOrMethod.parameters.size()}) {", true)
            conversionFactory.out.addScript("  ${lastParameter.name}=${GS_LIST}([${lastParameter.name}]);", true)
            conversionFactory.out.addScript("  for (${COUNT}=${functionOrMethod.parameters.size()};${COUNT} < arguments.length; ${COUNT}++) {", true)
            conversionFactory.out.addScript("    ${lastParameter.name}.add(arguments[${COUNT}]);", true)
            conversionFactory.out.addScript("  }", true)
            conversionFactory.out.addScript("}", true)
        }
    }

    boolean haveAnnotationNative(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            //If native then exit
            if (it.getClassNode().nameWithoutPackage == 'GsNative') {
                exit = true
            }
        }
        exit
    }

    void putGsNativeMethod(String name, ClassNode classNode, MethodNode method) {
        conversionFactory.out.addScript("${name} = function(")
        conversionFactory.context.actualScope.push([])
        conversionFactory.functions.processFunctionOrMethodParameters(method, false, false)
        conversionFactory.context.actualScope.pop()
        conversionFactory.out.addScript(conversionFactory.context.getNativeFunction(classNode, method), true)
        conversionFactory.out.indent--
        conversionFactory.out.removeTabScript()
        conversionFactory.out.addScript('}', true)
    }

    private boolean isArray(Parameter param) {
        //'[Ljava.lang.Object;'
        param.type.name.startsWith('[L') && param.type.name.endsWith(';')
    }
}

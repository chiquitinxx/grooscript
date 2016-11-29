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
package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.*
import org.grooscript.convert.ConversionOptions

import static org.grooscript.JsNames.*

class MethodCallExpressionHandler extends BaseHandler {

    static final String SUPER_METHOD_BEGIN = 'super_'
    
    void handle(MethodCallExpression expression) {
        //println "MCE ${expression.objectExpression} - ${expression.methodAsString}"
        String methodName = expression.methodAsString
        def firstArgument = (expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpressions() ? expression.arguments.getExpression(0) : null)

        //Change println for javascript function
        if (methodName == 'println') {
            if (conversionFactory.converter.conversionOptions[ConversionOptions.NASHORN_CONSOLE.text] == true) {
                out.addScript(GS_PRINT_NASHORN)
            } else {
                out.addScript(GS_PRINTLN)
            }
            addParametersWithParenthesis(expression)
        //rehydrate and dehydrate are ignored
        } else if (methodName in ['rehydrate', 'dehydrate'] && expression.objectExpression instanceof ClosureExpression) {
            conversionFactory.visitNode(expression.objectExpression)
        //Remove call method call from closures
        } else if (methodName == 'call') {
            out.addScript("${GS_EXECUTE_CALL}(")
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(", this, ")
            addParametersAsList(expression)
            out.addScript(')')
        //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'super') {
            out.addScript("${SUPER_METHOD_BEGIN}${methodName}")
            addParametersWithParenthesis(expression)
        //Function times, with a number, have to put (number) in javascript
        } else if (['times', 'upto', 'step'].contains(methodName) && expression.objectExpression instanceof ConstantExpression) {
            out.addScript('(')
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(").${methodName}")
            addParametersWithParenthesis(expression)
        //With
        } else if (methodName == 'with' && firstArgument instanceof ClosureExpression) {
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".${WITH}")
            context.insideWith = true
            addParametersWithParenthesis(expression)
            context.insideWith = false
        //Equals
        } else if (methodName == 'equals' && firstArgument) {
            out.addScript("${GS_EQUALS}(")
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(',')
            conversionFactory.visitNode(firstArgument)
            out.addScript(')')
        //WithTraits
        } else if (methodName == 'withTraits' && expression.arguments instanceof ArgumentListExpression) {
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".${WITH_TRAITS}")
            addParametersWithParenthesis(expression)
        //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Math') {
            out.addScript("Math.${methodName}")
            addParametersWithParenthesis(expression)
        //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Class' &&
                methodName == 'forName') {
            out.addScript("${GS_CLASS_FOR_NAME}(")
            addParametersWithoutParenthesis(expression)
            if (firstArgument instanceof ConstantExpression) {
                out.addScript(", ${conversionFactory.reduceClassName(firstArgument.text)}")
            }
            out.addScript(')')
        //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'this' && methodName == 'use') {
            def nameCategory = firstArgument.type.nameWithoutPackage
            out.addScript("${GS_CATEGORY_USE}(\"${nameCategory}\",${nameCategory},")
            conversionFactory.visitNode(expression.arguments.expressions[1])
            out.addScript(')')
        //Mixin Classes
        } else if (expression.objectExpression instanceof ClassExpression && methodName == 'mixin') {
            //println 'Mixin!'
            out.addScript("${GS_MIXIN_CLASS}('${expression.objectExpression.type.nameWithoutPackage}',")
            addMixinParams(expression)
            out.addScript(')')
        //Mixin Objects
        } else if (expression.objectExpression instanceof PropertyExpression &&
                expression.objectExpression.property instanceof ConstantExpression &&
                expression.objectExpression.property.text == 'metaClass' &&
                methodName == 'mixin') {
            out.addScript("${GS_MIXIN_OBJECT}(${expression.objectExpression.objectExpression.text},")
            addMixinParams(expression)
            out.addScript(')')
        //Spread method call [1,2,3]*.toString()
        } else if (expression.isSpreadSafe()) {
            //println 'spreadsafe!'
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".collect(function(it) { return ${GS_METHOD_CALL}(it, '${methodName}', ")
            addParametersAsList(expression)
            out.addScript(');})')
        //Call a method in this, method exist in main context
        } else if (conversionFactory.isThis(expression.objectExpression) &&
                context.firstVariableScopingHasMethod(methodName)) {
            out.addScript(methodName)
            addParametersWithParenthesis(expression)
        //is function
        } else if (methodName == 'is' && firstArgument) {
            out.addScript("${GS_IS}(")
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(',')
            addParametersWithoutParenthesis(expression)
            out.addScript(')')
        //Trait set method
        } else if(conversionFactory.isTraitClass(expression.objectExpression.type.name) &&
                methodName.endsWith('$set')) {
            out.addScript("\$self.${getNameTraitProperty(methodName)} = ")
            addParametersWithoutParenthesis(expression)
        //Trait get method
        } else if(conversionFactory.isTraitClass(expression.objectExpression.type.name) &&
                methodName.endsWith('$get')) {
            out.addScript("\$self.${getNameTraitProperty(methodName)}")
        //Trait get static property
        } else if(expression.objectExpression instanceof TernaryExpression &&
                expression.objectExpression.booleanExpression.expression instanceof BinaryExpression &&
                methodName?.endsWith('$get') &&
                isTraitVariableExpression(expression.objectExpression.booleanExpression.expression.leftExpression)
        ) {
            //traits_Methods__ONE$get
            out.addScript("${GS_GET_PROPERTY}(${traitVariableName(expression.objectExpression.booleanExpression.expression.leftExpression)},'")
            out.addScript(namePropertyFromTrait(methodName))
            out.addScript('\')')
        //Trait set static property in static method
        } else if(methodName?.endsWith('$set') && expression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.variable == '$static$self') {
            //traits_Methods__ONE$set
            out.addScript("${GS_SET_PROPERTY}(\$static\$self,'")
            out.addScript(namePropertyFromTrait(methodName))
            out.addScript('\',')
            addParametersWithoutParenthesis(expression)
            out.addScript(')')
        //Trait set static property in normal method
        } else if(methodName?.endsWith('$set') && expression.objectExpression instanceof PropertyExpression &&
                expression.objectExpression.objectExpression instanceof CastExpression &&
                expression.objectExpression.objectExpression.expression instanceof VariableExpression &&
                expression.objectExpression.objectExpression.expression.variable == '$self') {
            //traits_StaticFields__VALUE$set
            out.addScript("${GS_SET_PROPERTY}(\$self,'")
            out.addScript(namePropertyFromTrait(methodName))
            out.addScript('\',')
            addParametersWithoutParenthesis(expression)
            out.addScript(')')
        //Static method
        } else if (isStaticMethodCall(expression)) {
            def specialStaticCall = SPECIAL_STATIC_METHOD_CALLS.find {
                it.type == expression.objectExpression.type.name && it.method == methodName
            }
            if (expression.objectExpression.type.name == 'org.grooscript.GrooScript' && methodName == 'nativeJs') {
                conversionFactory.outFirstArgument(expression)
            } else if (specialStaticCall) {
                out.addScript("${specialStaticCall.function}")
                addParametersWithParenthesis(expression)
            } else {
                out.addScript("$GS_EXEC_STATIC(")
                conversionFactory.visitNode(expression.objectExpression)
                out.addScript(",'$methodName', this,")
                addParametersAsList(expression)
                out.addScript(')')
            }
        } else {
            //println 'Method->'+methodName+' - '+expression.arguments.class.simpleName
            doFullMethodCall(methodName, expression)
        }
    }

    private addParametersWithParenthesis(expression) {
        conversionFactory.visitNode(expression.arguments)
    }

    private addParametersWithoutParenthesis(expression) {
        def arguments = expression.arguments
        if (context.actualTraitMethod && arguments?.getExpressions()) {
            //Inside a trait method, we remove $self as first argument
            def exp = arguments.getExpression(0)
            if (exp instanceof VariableExpression && exp.variable == '$self' &&
                    expression.methodAsString in context.actualTraitMethod.declaringClass.methods.collect { it.name }) {
                arguments = new ArgumentListExpression(arguments.getExpressions().tail())
            }
        }
        conversionFactory.visitNode(arguments, false)
    }

    private addParametersAsList(expression) {
        def hasSpread = expression.arguments.any {it instanceof SpreadExpression}
        if (hasSpread) {
            out.addScript("${GS_LIST}(")
        }
        out.addScript('[')
        addParametersWithoutParenthesis(expression)
        out.addScript(']')
        if (hasSpread) {
            out.addScript(')')
        }
    }

    private addMixinParams(expression) {
        out.addScript('[')
        ArgumentListExpression args = expression.arguments
        out.addScript args.expressions.inject([]) { item, expr ->
            item << expr.type.nameWithoutPackage
        }.join(',')
        out.addScript(']')
    }

    private putMethodName(MethodCallExpression expression) {
        conversionFactory.visitNode(expression.method)
    }

    private getNameTraitProperty(methodName) {
        methodName.substring(0, methodName.lastIndexOf('$'))
    }

    private boolean isStaticMethodCall(MethodCallExpression expression) {
        boolean staticMethod = false
        if (expression.objectExpression instanceof ClassExpression) {
            ClassNode sourceObject = expression.objectExpression.type
            def method = sourceObject.methods.find { it.name == expression.methodAsString}
            if (method && method.isStatic()) {
                staticMethod = true
            }
        }
        staticMethod
    }

    private doFullMethodCall(String methodName, MethodCallExpression expression) {
        out.addScript("${GS_METHOD_CALL}(")
        //Object
        if (conversionFactory.isThis(expression.objectExpression) &&
                context.currentVariableScopingHasMethod(methodName)) {
            out.addScript(GS_OBJECT)
        } else {
            if (conversionFactory.isThis(expression.objectExpression) && context.staticProcessNode) {
                out.addScript(context.staticProcessNode.nameWithoutPackage)
            } else {
                if (conversionFactory.isThis(expression.objectExpression) && context.actualTraitMethod) {
                    out.addScript(context.actualTraitMethod.parameters[0].name)
                } else {
                    conversionFactory.visitNode(expression.objectExpression)
                }
            }
        }
        out.addScript(',')

        //MethodName
        putMethodName(expression)
        out.addScript(',')

        //Parameters
        addParametersAsList(expression)

        //Helper
        if (conversionFactory.isThis(expression.objectExpression) && !context.mainContext &&
                context.insideClass && !context.currentVariableScopingHasMethod(methodName) &&
                !context.staticProcessNode) {
            out.addScript(", ${context.actualTraitMethod ? '$self' : GS_OBJECT}")
        } else {
            if (expression.safe) {
                out.addScript(', null')
            }
        }

        //Safe navigation
        if (expression.safe) {
            out.addScript(', true')
        }

        out.addScript(')')
    }

    private isTraitVariableExpression(expression) {
        (expression instanceof VariableExpression && expression.variable == '$static$self') ||
            (expression instanceof CastExpression && expression.expression instanceof VariableExpression &&
                    expression.expression.variable == '$self')
    }

    private traitVariableName(expression) {
        expression instanceof VariableExpression ?
                expression.variable :
                traitVariableName(expression.expression)
    }

    private namePropertyFromTrait(methodName) {
        methodName.substring(methodName.lastIndexOf('__') + 2, methodName.size() - 4)
    }
}

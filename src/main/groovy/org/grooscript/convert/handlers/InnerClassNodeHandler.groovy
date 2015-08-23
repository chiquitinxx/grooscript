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

import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement

import static org.grooscript.JsNames.*

class InnerClassNodeHandler extends TraitBaseHandler {

    private static final TARGET = 'target'
    private static final STATIC_SELF = '$static$self'

    void handle(InnerClassNode innerClassNode) {

        if (conversionFactory.isTraitClass(innerClassNode.name)) {
            handleTrait(innerClassNode)
        } else {
            handleInnerClass(innerClassNode)
        }
    }

    private handleInnerClass(InnerClassNode innerClassNode) {
        conversionFactory.getConverter('ClassNode').handle(innerClassNode)
    }

    private handleTrait(InnerClassNode innerClassNode) {
        def className = innerClassNode.outerClass.nameWithoutPackage
        createConstructorWithApplyTryFunction(innerClassNode, className)

        innerClassNode.methods.findAll {
            !it.isAbstract() && !isAccessorOfStaticField(it.name, innerClassNode)
        }.each { methodNode ->
            if (methodNode.name == '$static$init$') {
                initStaticTraitFields(methodNode, innerClassNode)
            } else if (methodNode.code instanceof BlockStatement) {
                context.actualTraitMethod = methodNode
                if (!methodNode.code.isEmpty() || methodNode.name == '$init$') {
                    functions.processBasicFunction("${className}.${methodNode.name}", methodNode, false)
                } else {
                    if (functions.haveAnnotationNative(methodNode.annotations)) {
                        functions.putGsNativeMethod("${className}.${methodNode.name}", innerClassNode, methodNode)
                    }
                }
                context.actualTraitMethod = null
            } else {
                if (methodNode.name.startsWith('get')) {
                    out.addScript("${className}.${methodNode.name} = function(\$self) {" +
                            " return \$self.${getNameOfTraitField(innerClassNode.outerClass, methodNode.name)}; }", true)
                } else if (methodNode.name.startsWith('set')) {
                    out.addScript("${className}.${methodNode.name} = function(\$self, value) {" +
                            " \$self.${getNameOfTraitField(innerClassNode.outerClass, methodNode.name)} = value; }", true)
                }
            }
        }
    }

    private getNameOfTraitField(classNode, String methodName) {
        String propertyName = methodName.substring(3)
        propertyName = propertyName[0].toLowerCase() + propertyName.substring(1)
        org.codehaus.groovy.transform.trait.Traits.remappedFieldName(classNode, propertyName)
    }

    private createConstructorWithApplyTryFunction(InnerClassNode innerClassNode, className) {


        out.addScript("${className} = function() {};", true)
        out.addScript("${className}.${GS_APPLY_TRAIT} = function(${TARGET}) {", true)
        innerClassNode.methods.findAll { !it.isAbstract() }.each { methodNode ->
            if (methodNode.name == '$init$') {
                if (methodNode.code instanceof BlockStatement && !methodNode.code.isEmpty()) {
                    out.addScript("  ${className}.\$init\$(${TARGET});", true)
                }
            } else if (methodNode.name == '$static$init$') {

            } else if (isAccessorOfStaticField(methodNode.name, innerClassNode)) {

            } else {
                def listParams = []
                methodNode.parameters.eachWithIndex{ param, int i -> listParams << "x${i}" }
                def targetParams = listParams.join(', ')
                listParams = [TARGET] + listParams
                def traitParams = listParams.join(', ')
                out.addScript("  ${TARGET}.${methodNode.name} = function(${targetParams}) { " +
                        "return ${className}.${methodNode.name}(${traitParams}); };", true)
            }
        }
        out.addScript("};", true)
    }

    private initStaticTraitFields(MethodNode methodNode, InnerClassNode innerClassNode) {
        out.block ("function ${innerClassNode.outerClass.nameWithoutPackage}\$static\$init\$($STATIC_SELF)") {
            methodNode.code.getStatements()?.each { Statement statement ->
                if (statement instanceof ExpressionStatement &&
                        statement.expression instanceof MethodCallExpression) {
                    def args = statement.expression.arguments
                    if (args instanceof ArgumentListExpression) {
                        putStaticInitialization(args[1], args[2])
                    } else if (statement.expression.method instanceof ConstantExpression) {
                        putStaticInitialization(statement.expression.method, args)
                    }
                }
            }
        }
    }

    private putStaticInitialization(ConstantExpression constantExpression, args) {
        out.addScript("$STATIC_SELF.${propertyNameFromExpression(constantExpression)} = ")
        conversionFactory.visitNode(args)
        out.addScript(";", true)
    }

    private propertyNameFromExpression(ConstantExpression constantExpression) {
        constantExpression.text.substring(
                constantExpression.text.lastIndexOf('__') + 2,
                constantExpression.text.lastIndexOf('$')
        )
    }
}

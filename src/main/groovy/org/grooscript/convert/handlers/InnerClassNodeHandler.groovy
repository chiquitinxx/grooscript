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

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */
class InnerClassNodeHandler extends TraitBaseHandler {

    private static final TARGET = 'target'

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
                if (!methodNode.code.isEmpty()) {
                    functions.processBasicFunction("${className}.${methodNode.name}", methodNode, false)
                } else {
                    if (functions.haveAnnotationNative(methodNode.annotations)) {
                        functions.putGsNativeMethod("${className}.${methodNode.name}", innerClassNode, methodNode)
                    }
                }
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
        if (methodNode.code.getStatements()) {
            out.block ("function ${innerClassNode.outerClass.nameWithoutPackage}\$static\$init\$($TARGET)") {
                methodNode.code.getStatements()?.each { Statement statement ->
                    if (statement instanceof ExpressionStatement &&
                            statement.expression instanceof MethodCallExpression &&
                            statement.expression.methodAsString == 'invokeStaticMethod') {
                        ArgumentListExpression ale = statement.expression.arguments
                        ConstantExpression constantExpression = ale[1]
                        def propertyName = constantExpression.text.substring(
                                constantExpression.text.lastIndexOf('__') + 2,
                                constantExpression.text.lastIndexOf('$')
                        )
                        out.addScript("$TARGET.${propertyName} = ")
                        conversionFactory.visitNode(ale[2])
                        out.addScript(";", true)
                    }
                }
            }
        }
    }
}

package org.grooscript.convert.packages

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.grooscript.convert.ConversionFactory

/**
 * Created by jorgefrancoleza on 19/11/14.
 */
class DojoHandler {

    def packages = []
    def variableName
    ConversionFactory factory

    boolean handle(node) {
        if (node instanceof StaticMethodCallExpression &&
                node.method == 'require' &&
                node.ownerType.name == 'org.grooscript.packages.DojoPackage') {
            packages << node.arguments[0].value.replaceAll('/','.')
            return true
        }
        if (node instanceof MethodCallExpression &&
                node.objectExpression instanceof PropertyExpression &&
                node.objectExpression.text in packages) {
            return true
        }
        return false
    }

    boolean process(node) {
        if (node instanceof StaticMethodCallExpression &&
                node.method == 'require' &&
                node.ownerType.name == 'org.grooscript.packages.DojoPackage') {
            def pack = node.arguments[0].value
            variableName = pack.split('/').last()
            factory.out.indent++
            factory.out.addScript("require([\"${pack}\"], function(${variableName}) {", true)
            factory.visitNode(node.arguments[1].code, false)
            factory.out.indent--
            factory.out.removeTabScript()
            factory.out.addScript("})")
        }
        if (node instanceof MethodCallExpression &&
                node.objectExpression instanceof PropertyExpression &&
                node.objectExpression.text in packages) {
            factory.out.addScript("${variableName}.${node.methodAsString}(")
            factory.convert(node.arguments, false)
            factory.out.addScript(")")
        }
    }
}
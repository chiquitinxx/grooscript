package org.grooscript.convert.packages

import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.grooscript.convert.ConversionFactory

/**
 * Created by jorgefrancoleza on 19/11/14.
 */
class DojoHandler implements PackageHandler {

    def packages = []
    def variableName
    ConversionFactory factory

    boolean handle(node) {
        isRequireDojo(node) || usingDojoFunction(node)
    }

    boolean process(node) {
        if (isRequireDojo(node)) {
            def pack = node.arguments[0].value
            variableName = pack.split('/').last()
            factory.out.indent++
            factory.out.addScript("require([\"${pack}\"], function(${variableName}) {", true)
            factory.visitNode(node.arguments[1].code, false)
            factory.out.indent--
            factory.out.removeTabScript()
            factory.out.addScript("})")
        }
        if (usingDojoFunction(node)) {
            factory.out.addScript("${variableName}.${node.methodAsString}(")
            factory.convert(node.arguments, false)
            factory.out.addScript(")")
        }
    }

    private boolean isRequireDojo(node) {
        def result = node instanceof StaticMethodCallExpression &&
                node.method == 'require' &&
                node.ownerType.name == 'org.grooscript.packages.DojoPackage'
        if (result)
            packages << node.arguments[0].value.replaceAll('/','.')
        result
    }

    private boolean usingDojoFunction(node) {
        node instanceof MethodCallExpression &&
                node.objectExpression instanceof PropertyExpression &&
                node.objectExpression.text in packages
    }
}
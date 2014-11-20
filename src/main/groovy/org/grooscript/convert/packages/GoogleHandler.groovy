package org.grooscript.convert.packages

import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.grooscript.convert.ConversionFactory

/**
 * Created by jorgefrancoleza on 19/11/14.
 */
class GoogleHandler implements PackageHandler {

    def packages = []
    ConversionFactory factory

    boolean handle(node) {
        isRequireGoogle(node) || usingGoogleFunction(node)
    }

    boolean process(node) {
        if (isRequireGoogle(node)) {
            factory.out.addScript("goog.require('${node.rightExpression.arguments[0].value}')")
        }
        if (usingGoogleFunction(node)) {
            factory.out.addScript("${node.objectExpression.text}.${node.methodAsString}(")
            factory.convert(node.arguments, false)
            factory.out.addScript(")")
        }
    }

    private boolean isRequireGoogle(node) {
        def result = node instanceof DeclarationExpression &&
                node.rightExpression instanceof StaticMethodCallExpression &&
                node.rightExpression.method == 'useJsLib' &&
                node.rightExpression.ownerType.name == 'org.grooscript.GrooScript'
        if (result)
            packages << node.rightExpression.arguments[0].value
        result
    }

    private boolean usingGoogleFunction(node) {
        node instanceof MethodCallExpression &&
                node.objectExpression instanceof PropertyExpression &&
                node.objectExpression.text in packages
    }
}
package org.grooscript.convert

import org.codehaus.groovy.ast.ASTNode
import org.grooscript.convert.handlers.BaseHandler
import org.grooscript.convert.handlers.VariableExpressionHandler
import org.grooscript.util.GrooScriptException

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class ConversionFactory {

    def conversionClasses = [:]
    def context
    def out

    def converters = ['VariableExpression': VariableExpressionHandler]

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

    private Object improvedConversionHandler(String className) {
        BaseHandler instanceHandler = converters[className].newInstance()
        instanceHandler.out = out
        instanceHandler.context = context
        instanceHandler.metaClass.visitNode = { ASTNode newNode ->
            this.&convert.rcurry(context, out)
        }
        instanceHandler
    }
}

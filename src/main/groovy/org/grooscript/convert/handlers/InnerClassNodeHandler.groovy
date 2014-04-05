package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.InnerClassNode

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */
class InnerClassNodeHandler extends BaseHandler {

    void handle(InnerClassNode innerClassNode) {

        def className = innerClassNode.outerClass.nameWithoutPackage
        out.addScript("${className} = function() {};", true)

        innerClassNode.methods.each {
            if (factory.isValidTraitMethodName(it.name)) {
                factory.convertBasicFunction("${className}.${it.name}", it, false)
            }
        }
    }
}

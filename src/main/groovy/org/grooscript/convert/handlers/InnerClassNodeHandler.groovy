package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.InnerClassNode
import org.codehaus.groovy.ast.stmt.BlockStatement

/**
 * User: jorgefrancoleza
 * Date: 05/04/14
 */
class InnerClassNodeHandler extends BaseHandler {

    void handle(InnerClassNode innerClassNode) {

        def className = innerClassNode.outerClass.nameWithoutPackage
        out.addScript("${className} = function() {};", true)

        innerClassNode.methods.findAll { factory.isValidTraitMethod(it) }.each {
            if (!it.isAbstract()) {
                functions.processBasicFunction("${className}.${it.name}", it, false)
            }
        }
        def initMethod = innerClassNode.methods.find { it.name == '$init$' && it.code instanceof BlockStatement }
        if (initMethod && !initMethod.code.isEmpty()) {
            functions.processBasicFunction("${className}.\$init\$", initMethod, false)
        }
    }
}

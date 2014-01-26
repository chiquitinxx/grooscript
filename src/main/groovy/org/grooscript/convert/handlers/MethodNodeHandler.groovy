package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.MethodNode

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 26/01/14
 */
class MethodNodeHandler extends BaseHandler {

    void handle(MethodNode method, boolean isConstructor = false) {
        def name =  method.name
        //Constructor method
        if (isConstructor) {
            //Add number of params to constructor name
            //BEWARE Atm only accepts constructor with different number or arguments
            name = context.classNameStack.peek() + (method.parameters ? method.parameters.size() : '0')
        }

        functions.processBasicFunction("${GS_OBJECT}['$name']", method, isConstructor)
    }
}

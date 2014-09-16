package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.ConstructorNode

/**
 * User: jorgefrancoleza
 * Date: 16/09/14
 */
class ConstructorNodeHandler extends BaseHandler {

    void handle(ConstructorNode method, boolean isConstructor) {
        conversionFactory.getConverter('MethodNode').handle(method, isConstructor)
    }
}

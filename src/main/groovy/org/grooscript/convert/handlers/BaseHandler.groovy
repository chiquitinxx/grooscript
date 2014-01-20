package org.grooscript.convert.handlers

import org.grooscript.convert.Context
import org.grooscript.convert.Out

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class BaseHandler {

    Context context
    Out out

    def visitNode(node) {}

    def handle(node) {}
}

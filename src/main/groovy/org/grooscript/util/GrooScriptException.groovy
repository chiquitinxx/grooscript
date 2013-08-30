package org.grooscript.util

/**
 * User: jorgefrancoleza
 * Date: 27/08/13
 */
class GrooScriptException extends Exception {

    GrooScriptException(String message) {
        super(message)
        GsConsole.exception(message)
    }
}

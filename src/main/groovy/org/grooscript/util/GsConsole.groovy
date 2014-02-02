package org.grooscript.util

/**
 * JFL 27/08/12
 */
@SuppressWarnings('Println')
class GsConsole {

    static final DESCRIPTION = '[grooscript]'

    private static getHead(origin) {
        origin?:DESCRIPTION
    }

    static error(message, origin = null) {
        println "\u001B[91m${getHead(origin)} - Error - ${message}\u001B[0m"
    }

    static message(message, origin = null) {
        println "${getHead(origin)} ${message}"
    }

    static info(message, origin = null) {
        println "${getHead(origin)} - Info - ${message}"
    }

    static exception(message, origin = null) {
        println "\u001B[91m${getHead(origin)} - Exception - ${message}\u001B[0m"
    }

    static debug(message, origin = null) {
        println "${getHead(origin)} - Debug - ${message}"
    }
}

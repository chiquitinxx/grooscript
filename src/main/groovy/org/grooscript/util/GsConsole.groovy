package org.grooscript.util

/**
 * JFL 27/08/12
 */
@SuppressWarnings('Println')
class GsConsole {

    static error(message) {
        println "[Grooscript] ERROR - ${message}"
    }

    static message(message) {
        println "[Grooscript] MSG - ${message}"
    }

    static exception(message) {
        println "[Grooscript] EXCEPTION - ${message}"
    }

    static debug(message) {
        println "[Grooscript - Debug] ${message}"
    }
}

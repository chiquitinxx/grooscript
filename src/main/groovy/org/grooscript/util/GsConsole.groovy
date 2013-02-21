package org.grooscript.util

/**
 * JFL 27/08/12
 */
class GsConsole {

    def static error(message) {
        println "[Grooscript] ERROR - ${message}"
    }

    def static message(message) {
        println "[Grooscript] MSG - ${message}"
    }
}

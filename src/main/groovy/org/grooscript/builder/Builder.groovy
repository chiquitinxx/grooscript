package org.grooscript.builder

/**
 * User: jorgefrancoleza
 * Date: 08/06/13
 */
class Builder {

    String html

    Builder() {
        html = ''
    }

    static Map process(Closure closure) {
        def result = [:]
        def builder = new Builder()

        closure.delegate = builder
        closure()

        result.html = builder.html
        result
    }

    def t(String text) {
        html += text
    }

    def methodMissing(String name, args) {

        html += "<${name}"
        if (args && args.size() > 0 && !(args[0] instanceof String) && !(args[0] instanceof Closure)) {
            args[0].each { key, value ->
                html += " ${key}='${value}'"
            }
        }
        html += '>'
        if (args) {
            if (args.size() == 1 && args[0] instanceof String) {
                html += args[0]
            } else {
                def lastArg = args.last()
                if (lastArg instanceof Closure) {
                    lastArg.delegate = this
                    lastArg()
                }
            }
        }
        html += "</${name}>"
    }
}

package org.grooscript.builder

/**
 * User: jorgefrancoleza
 * Date: 08/06/13
 */
class HtmlBuilder {

    String html

    HtmlBuilder() {
        html = ''
    }

    static String build(@DelegatesTo(HtmlBuilder) Closure closure) {
        def builder = new HtmlBuilder()

        closure.delegate = builder
        closure()

        builder.html
    }

    def yield(String text) {
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
                yield args[0]
            } else {
                def lastArg = args.last()
                if (lastArg instanceof Closure) {
                    lastArg.delegate = this
                    lastArg()
                }
                if (lastArg instanceof String && args.size() > 1) {
                    yield lastArg
                }
            }
        }
        html += "</${name}>"
    }
}

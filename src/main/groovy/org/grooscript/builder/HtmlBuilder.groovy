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
        def mc = new ExpandoMetaClass(HtmlBuilder, false, true)
        mc.initialize()
        def builder = new HtmlBuilder()
        builder.metaClass = mc
        closure.delegate = builder
        closure()

        builder.html
    }

    def yield(String text) {
        text.each { ch ->
            switch (ch) {
                case '&':
                    html += "&amp;"
                    break
                case '<':
                    html += "&lt;"
                    break
                case '>':
                    html += "&gt;"
                    break
                case '"':
                    html += "&quot;"
                    break
                case '\'':
                    html +=  "&apos;"
                    break
                default:
                    html += ch
                    break
            }
        }
    }

    def yieldUnescaped(String text) {
        html += text
    }

    def comment(String text) {
        html += '<!--' + text + '-->'
    }

    def newLine() {
        html += '\n'
    }

    def methodMissing(String name, args) {
        this.metaClass."${name}" = { ...ars -> tagSolver(name, ars)}
        this.invokeMethod(name, args)
    }

    def tagSolver = { String name, args ->
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

package org.grooscript.builder

/**
 * User: jorgefrancoleza
 * Date: 08/06/13
 */
class HtmlBuilder {

    private String htmCd

    HtmlBuilder() {
        htmCd = ''
    }

    static String build(@DelegatesTo(HtmlBuilder) Closure closure) {
        def mc = new ExpandoMetaClass(HtmlBuilder, false, true)
        mc.initialize()
        def builder = new HtmlBuilder()
        builder.metaClass = mc
        closure.delegate = builder
        closure()

        builder.htmCd
    }

    def yield(String text) {
        text.each { ch ->
            switch (ch) {
                case '&':
                    htmCd += "&amp;"
                    break
                case '<':
                    htmCd += "&lt;"
                    break
                case '>':
                    htmCd += "&gt;"
                    break
                case '"':
                    htmCd += "&quot;"
                    break
                case '\'':
                    htmCd +=  "&apos;"
                    break
                default:
                    htmCd += ch
                    break
            }
        }
    }

    def yieldUnescaped(String text) {
        htmCd += text
    }

    def comment(String text) {
        htmCd += '<!--' + text + '-->'
    }

    def newLine() {
        htmCd += '\n'
    }

    def methodMissing(String name, args) {
        this.metaClass."${name}" = { ...ars -> tagSolver(name, ars)}
        this.invokeMethod(name, args)
    }

    def tagSolver = { String name, args ->
        htmCd += "<${name}"
        if (args && args.size() > 0 && !(args[0] instanceof String) && !(args[0] instanceof Closure)) {
            args[0].each { key, value ->
                htmCd += " ${key}='${value}'"
            }
        }
        htmCd += !args ? '/>' : '>'
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
            htmCd += "</${name}>"
        }
    }
}

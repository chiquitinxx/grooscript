package org.grooscript.builder

import org.grooscript.test.ConversionMixin
import org.grooscript.util.Util
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 08/06/13
 */
@Mixin(ConversionMixin)
class TestHtmlBuilder extends Specification {

    static final TEXT = 'text'

    void 'process with the builder'() {
        given:
        def result = HtmlBuilder.build {
            body {
                p TEXT
            }
        }

        expect:
        result == "<body><p>${TEXT}</p></body>"

        and: 'works in javascript'
        !checkBuilderCodeAssertsFails('''
            def result = HtmlBuilder.build {
                body {
                    p 'hola'
                }
            }

            assert result == "<body><p>hola</p></body>"
        ''', false)
    }

    void 'works with tag options and yield function'() {
        given:
        def result = HtmlBuilder.build {
            body {
                p(class:'salute') {
                    yield 'hello'
                }
            }
        }

        expect:
        result == "<body><p class='salute'>hello</p></body>"
    }

    void 'yield escaped chars'() {
        given:
        def result = HtmlBuilder.build {
            yield '<hello " \' & >'
        }

        expect:
        result == '&lt;hello &quot; &apos; &amp; &gt;'
    }

    void 'yield unescaped chars'() {
        given:
        def result = HtmlBuilder.build {
            yieldUnescaped '<hello " \' & >'
        }

        expect:
        result == '<hello " \' & >'
    }

    void 'works with code inside the closure'() {
        //tag::htmlBuilder[]
        given:
        def result = HtmlBuilder.build {
            body {
                ul(class: 'list', id: 'mainList') {
                    2.times { number ->
                        li number + 'Hello!'
                    }
                }
            }
        }

        expect:
        result == "<body><ul class='list' id='mainList'><li>0Hello!</li><li>1Hello!</li></ul></body>"
        //end::htmlBuilder[]
    }

    void 'last param is a string'() {
        given:
        def result = HtmlBuilder.build {
            p([class: 'text'], 'Hello!')
        }

        expect:
        result == "<p class='text'>Hello!</p>"
    }

    void 'test yield in js'() {
        expect:
        !checkBuilderCodeAssertsFails('''
            def result = HtmlBuilder.build {
                yield '<hello " \\' & >'
            }

            assert result == "&lt;hello &quot; &apos; &amp; &gt;"
        ''')
    }

    void 'comment'() {
        given:
        def result = HtmlBuilder.build {
            comment 'Is a <little> comment'
        }

        expect:
        result == '<!--Is a <little> comment-->'
    }

    void 'new line'() {
        given:
        def result = HtmlBuilder.build {
            p 'a'
            newLine()
            p 'b'
        }

        expect:
        result == "<p>a</p>${Util.LINE_SEPARATOR}<p>b</p>"
    }
}

package org.grooscript.builder

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 08/06/13
 */
@Mixin([ConversionMixin])
class TestBuilder extends Specification {

    static final TEXT = 'text'

    void 'process with the builder'() {
        given:
        def result = Builder.build {
            body {
                p TEXT
            }
        }

        expect:
        result == "<body><p>${TEXT}</p></body>"

        and: 'works in javascript'
        !checkBuilderCodeAssertsFails('''
            def result = Builder.build {
                body {
                    p 'hola'
                }
            }

            assert result == "<body><p>hola</p></body>"
        ''',false)
    }

    void 'works with tag options and t function'() {
        given:
        def result = Builder.build {
            body {
                p(class:'salute') {
                    t 'hello'
                }
            }
        }

        expect:
        result == "<body><p class='salute'>hello</p></body>"
    }

    void 'works with code inside the closure'() {
        given:
        def result = Builder.build {
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
    }
}

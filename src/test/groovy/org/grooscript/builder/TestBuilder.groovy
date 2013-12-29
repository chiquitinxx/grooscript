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
        def result = Builder.process {
            body {
                p TEXT
            }
        }

        expect:
        result.html == "<body><p>${TEXT}</p></body>"

        and: 'works in javascript'
        !checkBuilderCodeAssertsFails('''
            def result = Builder.process {
                body {
                    p 'hola'
                }
            }

            assert result.html == "<body><p>hola</p></body>"
        ''',false)
    }

    void 'works with tag options and t function'() {
        given:
        def result = Builder.process {
            body {
                p(class:'salute') {
                    t 'hello'
                }
            }
        }

        expect:
        result.html == "<body><p class='salute'>hello</p></body>"
    }
}

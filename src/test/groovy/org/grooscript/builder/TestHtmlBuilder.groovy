/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.builder

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

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
        ''')
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
        result == "<p>a</p>\n<p>b</p>"
    }

    void 'empty tags'() {
        given:
        def result = HtmlBuilder.build {
            script(src: 'aFile.js')
            br()
        }

        expect:
        result == "<script src='aFile.js'></script><br/>"
    }

    void 'properties and content as text'() {
        given:
        def result = HtmlBuilder.build {
            button(class: 'aClass', 'a')
        }

        expect:
        result == '<button class=\'aClass\'>a</button>'
    }
}

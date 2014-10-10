package org.grooscript.templates

import org.grooscript.builder.HtmlBuilder
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 11/10/14
 */
class TemplatesSpec extends Specification {

    def 'convert template'() {
        expect:
        templates.applyTemplate('one.gtpl') == '<p>Hello!</p>'
    }

    def 'convert template with model'() {
        expect:
        templates.applyTemplate('two.tpl', [list: [1, 2], message: 'Msg']) ==
                '<ul><li>1</li><li>2</li></ul><p>Msg</p>'
    }

    def 'convert template using other tamplate'() {
        expect:
        templates.applyTemplate('three.gtpl', [list: [1, 1, 1]]) ==
                '<ul><p>Hello!</p><p>Hello!</p><p>Hello!</p></ul>'
    }

    Templates templates = new Templates()

    def setup() {
        templates.templates = [
            'one.gtpl': { model = [:] ->
                HtmlBuilder.build {
                    p 'Hello!'
                }
            },
            'two.tpl': { model = [:] ->
                HtmlBuilder.build {
                    ul {
                        model.list.each {
                            li it.toString()
                        }
                    }
                    p message
                }
            },
            'three.gtpl': { model = [:] ->
                HtmlBuilder.build {
                    ul {
                        model.list.each {
                            yieldUnescaped Templates.applyTemplate('one.gtpl', model)
                        }
                    }
                }
            }
        ]
    }
}

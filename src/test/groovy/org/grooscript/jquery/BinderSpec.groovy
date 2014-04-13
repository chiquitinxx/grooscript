package org.grooscript.jquery

import org.codehaus.groovy.runtime.MethodClosure
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 06/02/14
 */
class BinderSpec extends Specification {

    private static final SELECTOR = 'selector'

    Binder binder
    JQuery jQuery
    Item item

    class Item {
        def id
        def name
        def group
        def buttonClick() {
            println 'Button clicked!'
        }
    }

    def setup() {
        jQuery = Mock(JQuery)
        binder = new Binder(jQuery: jQuery)
        item = new Item()
    }

    @Unroll
    def 'bind all properties'() {

        when:
        binder.bindAllProperties(item, closure)

        then:
        1 * jQuery.existsId('id') >> true
        1 * jQuery.bind('#id', item, 'id', closure)
        1 * jQuery.existsName('name') >> true
        1 * jQuery.bind('[name=\'name\']', item, 'name', closure)
        1 * jQuery.existsGroup('group') >> true
        1 * jQuery.bind("input:radio[name=group]", item, 'group', closure)
        0 * jQuery.bind(_)

        where:
        closure << [null, { -> true}]
    }

    def 'bind all methods'() {

        when:
        binder.bindAllMethods(item)

        then:
        1 * jQuery.existsId('button') >> true
        1 * jQuery.bindEvent('button', 'click', _ as MethodClosure)
        0 * jQuery.bindEvent(_)
    }

    @Unroll
    def 'bind all'() {
        given:
        def bindedProperties = true
        def bindedMethods = false
        binder.metaClass.bindAllProperties = { itemParam, closureParam ->
            if (itemParam == item && closureParam == closure) {
                bindedProperties = true
            }
        }
        binder.metaClass.bindAllMethods = { itemParam ->
            if (itemParam == item) {
                bindedMethods = true
            }
        }

        when:
        binder(item, closure)

        then:
        bindedProperties
        bindedMethods

        where:
        closure << [null, { -> true}]
    }
}

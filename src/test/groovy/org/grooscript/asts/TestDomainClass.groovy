package org.grooscript.asts

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 28/01/13
 */
class TestDomainClass extends Specification {

    static final NAME = 'name'
    static final VALUE = 'value'

    @DomainClass class AstItem {
        String name
        Integer number

        static constraints = {
            name example:true
        }

    }

    def cleanup() {
        AstItem.lastId = 0
        AstItem.listItems = []
    }

    def 'test ast for domain classes'() {
        given:
        def item = new AstItem()

        expect:
        //println item.properties
        item.properties.containsKey('id')
        AstItem.listItems == []
        AstItem.mapTransactions == [:]
        AstItem.dataHandler == null
        AstItem.lastId == 0
        AstItem.listColumns.size() == 2
        AstItem.listColumns.find{it.name==NAME}.name == NAME
        AstItem.listColumns.find{it.name==NAME}.type == 'java.lang.String'
        AstItem.listColumns.find{it.name==NAME}.constraints == [example:true]
        AstItem.listColumns.find{it.name=='number'}.name == 'number'
        AstItem.listColumns.find{it.name=='number'}.type == 'java.lang.Integer'
        AstItem.listColumns.find{it.name=='number'}.constraints == [:]
        AstItem.version == 0
        //println item.metaClass.methods
        item.metaClass.methods.find { it.name=='save'}
        item.save()
    }

    def 'test save item locally'() {
        when:
        def item = new AstItem()
        item."${NAME}" = VALUE
        def result = item.save()

        then:
        result == 1
        AstItem.listItems.size() == 1
        AstItem.listItems[0]."${NAME}" == VALUE
        AstItem.list() == AstItem.listItems
    }

    def 'test change listener executed'() {
        given:
        def item = new AstItem()
        def value = 15
        item.changeListeners << { it -> println it;value = value * 2}

        when:
        item."$NAME" = VALUE
        item.save()

        then:
        value == 30
    }

}

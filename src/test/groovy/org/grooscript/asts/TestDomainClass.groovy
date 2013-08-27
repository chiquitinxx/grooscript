package org.grooscript.asts

import org.grooscript.util.DataHandler
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

    @DomainClass class AstItemWithBlankValidation {
        String name
        Integer number

        static constraints = {
            name blank:false
        }
    }

    def cleanup() {
        AstItem.lastId = 0
        AstItem.listItems = []
        AstItem.dataHandler = null
        AstItem.mapTransactions = [:]
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
        given:
        AstItem.count() == 0
        def item = new AstItem()

        expect:
        !item.id

        when:
        item."${NAME}" = VALUE
        def result = item.save()

        then:
        result == 1
        item.id == 1
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

    def 'test blank validation'() {
        given:
        AstItemWithBlankValidation.count() == 0
        AstItemWithBlankValidation item = new AstItemWithBlankValidation()

        expect:
        !item.clientValidations()

        and:
        item.hasErrors()
        item.errors == [name:'blank validation on value null']

        when:
        def result = item.save()

        then:
        result == 0
        AstItemWithBlankValidation.count() == 0
    }

    static final long NUMBER_TRANSACTION = 5

    def 'test save new item with dataHandler'() {
        given:
        def (item, dataHandler) = getNewItemWithDataHandler()

        expect:
        item.mapTransactions.size() == 0

        when:
        def numberTransaction = item.save()

        then:
        1 * dataHandler.insert("${this.class.name}\$AstItem",item) >> NUMBER_TRANSACTION
        numberTransaction == NUMBER_TRANSACTION

        and:
        item.mapTransactions.size() == 1
        item.mapTransactions[NUMBER_TRANSACTION] == [item:item,onOk:null,onError:null]
    }

    def 'test success save new item in datahandler'() {
        given:
        def (item, dataHandler) = getNewItemWithDataHandler()
        dataHandler.insert(_,_) >> NUMBER_TRANSACTION
        def number = 4
        def successClosure = { number = number * 2 }
        item.save(successClosure)

        expect:
        AstItem.count() == 0
        AstItem.mapTransactions[NUMBER_TRANSACTION] == [item:item,onOk:successClosure,onError:null]

        when:
        item.processDataHandlerSuccess([number: NUMBER_TRANSACTION, action: 'insert',
                item: [id : 11, name: 'name', number: 12]])

        then:
        AstItem.count() == 1
        number == 4 * 2

        and:
        AstItem.list()[0] == item
        item.id == 11
        item.name == 'name'
        item.number == 12

        and:
        !AstItem.mapTransactions
    }

    def getNewItemWithDataHandler() {
        DataHandler dataHandler = Mock(DataHandler)
        AstItem.dataHandler = dataHandler
        def item = new AstItem()
        [item, dataHandler]
    }
}

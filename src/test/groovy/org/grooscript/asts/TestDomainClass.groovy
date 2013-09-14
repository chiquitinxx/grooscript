package org.grooscript.asts

import org.grooscript.GrooScript
import org.grooscript.test.TestJs
import org.grooscript.util.DataHandler
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 28/01/13
 */
class TestDomainClass extends Specification {

    static final NAME = 'name'
    static final VALUE = 'value'
    static final FAKE_ID = -3464356

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
        GrooScript.clearAllOptions()
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
        item.metaClass.methods.find { it.name=='save'}
        item.metaClass.methods.find { it.name=='delete'}
        item.save()
    }

    def 'test get method'() {
        given:
        def item = getBasicItem()

        expect:
        item.id
        AstItem.get(item.id) == item
        !AstItem.get(FAKE_ID)
    }

    def 'test create new item'() {
        given:
        AstItem.count() == 0
        def item = new AstItem()

        expect:
        !item.id
        AstItem.lastId == 0

        when:
        item."${NAME}" = VALUE
        def result = item.save()

        then:
        result == 1
        item.id == 1
        AstItem.count() == 1
        AstItem.listItems.size() == 1
        AstItem.listItems[0]."${NAME}" == VALUE
        AstItem.list() == AstItem.listItems
        AstItem.lastId == 1
    }

    def 'test update an item'() {
        given:
        def item = getBasicItem()

        expect:
        item.name == NAME

        when:
        item.name = VALUE
        item.save()

        then:
        AstItem.get(item.id).name == VALUE
        AstItem.list()[0] == item
    }

    def 'test delete an item'() {
        given:
        AstItem item = getBasicItem()

        expect:
        AstItem.count() == 1

        when:
        item.delete()

        then:
        AstItem.count() == 0
        !AstItem.list()
        !AstItem.listItems
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
        !item.validate()

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

    def 'test success save new item in dataHandler'() {
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
                item: [id : 11, name: NAME, number: 12, version: 7]])

        then:
        AstItem.count() == 1
        number == 4 * 2

        and:
        AstItem.list()[0] == item
        item.id == 11
        item.name == NAME
        item.number == 12
        item.version == 7

        and:
        !AstItem.mapTransactions
    }

    def 'test update item with dataHandler'() {
        given:
        def (item, dataHandler) = getItemWithDataHandler()

        expect:
        item.name == NAME

        when:
        item.name = VALUE
        def numberTransaction = item.save()

        then:
        1 * dataHandler.update("${this.class.name}\$AstItem",item) >> NUMBER_TRANSACTION
        numberTransaction == NUMBER_TRANSACTION

        and:
        item.mapTransactions[NUMBER_TRANSACTION] == [item:item,onOk:null,onError:null]
    }

    def 'test success save update item with dataHandler'() {
        given:
        def (item, dataHandler) = getItemWithDataHandler()
        AstItem.mapTransactions[NUMBER_TRANSACTION] = [item:item,onOk:null,onError:null]

        expect:
        item.name == NAME
        AstItem.count() == 1

        when:
        item.processDataHandlerSuccess([number: NUMBER_TRANSACTION, action: 'update',
                item: [id : item.id, name: VALUE, number: 12, version: 8]])

        then:
        AstItem.count() == 1
        AstItem.list()[0] == item

        and:
        item.id == old(item.id)
        item.name == VALUE
        item.number == 12
        item.version == 8

        and:
        !AstItem.mapTransactions
    }

    def 'test delete item with dataHandler'() {
        given:
        def (item, dataHandler) = getItemWithDataHandler()
        def number = 5

        expect:
        item.name == NAME

        when:
        def numberTransaction = item.delete()

        then:
        1 * dataHandler.delete("${this.class.name}\$AstItem",item) >> NUMBER_TRANSACTION
        numberTransaction == NUMBER_TRANSACTION

        and:
        item.mapTransactions[NUMBER_TRANSACTION] == [item:item,onOk:null,onError:null]
    }

    def 'test success delete item with dataHandler'() {
        given:
        def (item, dataHandler) = getItemWithDataHandler()
        AstItem.mapTransactions[NUMBER_TRANSACTION] = [item:item,onOk:null,onError:null]

        expect:
        AstItem.count() == 1

        when:
        item.processDataHandlerSuccess([number: NUMBER_TRANSACTION, action: 'delete',
                item: [id : item.id, name: VALUE, number: 12]])

        then:
        AstItem.count() == 0

        and:
        !AstItem.mapTransactions
    }

    def getBasicItem() {
        def item = new AstItem(name: NAME)
        item.save()
        item
    }

    def getNewItemWithDataHandler() {
        DataHandler dataHandler = Mock(DataHandler)
        AstItem.dataHandler = dataHandler
        def item = new AstItem()
        [item, dataHandler]
    }

    def getItemWithDataHandler() {
        def item = getBasicItem()
        DataHandler dataHandler = Mock(DataHandler)
        AstItem.dataHandler = dataHandler

        [item, dataHandler]
    }

    def 'test convert a basic domain class'() {
        given:
        GrooScript.clearAllOptions()
        GrooScript.setOwnClassPath(['src/test/resources'])
        GrooScript.setConversionProperty('customization', {
            ast(org.grooscript.asts.DomainClass)
        })

        when:
        def result = GrooScript.convert(TestJs.getGroovyTestScript('asts/DomainClass').text)
        println result

        then:
        noExceptionThrown()
        !result.contains('mapping')
        !result.contains('transients')
    }
}

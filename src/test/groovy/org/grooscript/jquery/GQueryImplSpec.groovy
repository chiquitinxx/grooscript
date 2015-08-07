package org.grooscript.jquery

import org.grooscript.rx.Observable
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 1/11/14
 */
class GQueryImplSpec extends Specification {

    def 'bind all properties'() {
        given:
        GroovySpy(GQueryList, global: true)
        def item = new Expando(namep: 'nameValue', idp: 'idValue', groupp: 'groupValue')
        def binded = 0
        hasResults.bind(item, 'namep', _) >> { binded++; hasResults }
        hasResults.bind(item, 'idp', _) >> { binded++; hasResults }
        hasResults.bind(item, 'groupp', _) >> { binded++; hasResults }

        when:
        gQueryImpl.bindAllProperties(item)

        then:
        1 * GQueryList.of('#namep') >> hasNotResults
        2 * GQueryList.of('#idp') >> hasResults
        1 * GQueryList.of('#groupp') >> hasNotResults
        2 * GQueryList.of("[name='namep']") >> hasResults
        1 * GQueryList.of("[name='idp']") >> hasNotResults
        1 * GQueryList.of("[name='groupp']") >> hasNotResults
        1 * GQueryList.of("input:radio[name='namep']") >> hasNotResults
        1 * GQueryList.of("input:radio[name='idp']") >> hasNotResults
        2 * GQueryList.of("input:radio[name='groupp']") >> hasResults
        binded == 3
        0 * _
    }

    def 'bind methods'() {
        given:
        GroovySpy(GQueryList, global: true)
        def instance = new WithEvens()
        int click = 0, submit = 0, change = 0
        hasResults.onEvent('click', _) >> { click++; hasResults }
        hasResults.onEvent('submit', _) >> { submit++; hasResults }
        hasResults.onChange(_) >> { change++; hasResults }

        when:
        gQueryImpl.attachMethodsToDomEvents(instance)

        then:
        2 * GQueryList.of('#id1') >> hasResults
        2 * GQueryList.of('#id2') >> hasResults
        2 * GQueryList.of('#id3') >> hasResults
        click == 1 && submit == 1 && change == 1
    }

    def 'chain methods'() {
        given:
        GroovySpy(GQueryList, global: true)
        def queryList = Mock(GQueryList)
        def cl = { -> println 'Hello!' }

        when:
        def result = gQueryImpl.focusEnd(selector).withResultList(cl).hasResults()

        then:
        1 * GQueryList.of(selector) >> queryList
        1 * queryList.focusEnd() >> queryList
        1 * queryList.withResultList(cl) >> queryList
        1 * queryList.hasResults() >> true
        result == true
    }

    def 'observe event'() {
        given:
        GroovySpy(Observable, global: true)
        GroovySpy(GQueryList, global: true)
        def observable = Stub(Observable)
        def queryList = Mock(GQueryList)
        def nameEvent = 'even'
        def data = [a: 1]

        when:
        def result = gQueryImpl.observeEvent(selector, nameEvent, data)

        then:
        1 * Observable.listen() >> observable
        1 * GQueryList.of(selector) >> queryList
        1 * queryList.on(nameEvent, data, _)
        result == observable
    }

    class WithEvens {
        def id1Click() {}
        def id2Submit() {}
        def id3Change() {}
    }

    private selector = 'select'
    private GQueryImpl gQueryImpl = new GQueryImpl()
    private hasResults = Stub(GQueryList) { it.hasResults() >> true }
    private hasNotResults = Stub(GQueryList) { it.hasResults() >> false }
}

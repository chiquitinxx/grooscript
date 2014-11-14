package org.grooscript.rx

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by jorgefrancoleza on 14/11/14.
 */
class ObservableSpec extends Specification {

    def 'subscribe to listen observable'() {
        given:
        def result = null
        def observable = Observable.listen()
        observable.subscribe { event ->
            result = event
        }

        when:
        observable.produce('hello')

        then:
        observable.subscribers.size() == 1
        result == 'hello'
    }

    @Unroll
    def 'subscribe to list observable'() {
        given:
        def result = ''
        Observable.from(list).subscribe { event ->
            result += event
        }

        expect:
        result == expectedResult

        where:
        list         | expectedResult
        null         | ''
        []           | ''
        [1]          | '1'
        [1, 2]       | '12'
        [1, 'hello'] | '1hello'
    }
}

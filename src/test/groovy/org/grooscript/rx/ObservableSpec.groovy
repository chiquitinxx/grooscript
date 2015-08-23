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
package org.grooscript.rx

import org.grooscript.GrooScript
import spock.lang.Specification
import spock.lang.Unroll

class ObservableSpec extends Specification {

    def 'subscribe to listen observable'() {
        given:
        def result = null
        def observable = Observable.listen()
        observable.subscribe { result = it }

        when:
        observable.produce('hello')

        then:
        observable.subscribers.size() == 1
        result == 'hello'
    }

    def 'map to listen observable'() {
        given:
        def result = null
        def observable = Observable.listen()
        observable.map { it.toUpperCase() }.subscribe { event ->
            result = event
        }

        when:
        observable.produce('hello')

        then:
        result == 'HELLO'
    }

    def 'multiple map to listen observable'() {
        given:
        def result = null
        def observable = Observable.listen()
        observable.map { it.replaceAll('-', '1') }.
                   map { it.replaceAll('1', '&') }.
                   map { it.toUpperCase() }.
                   subscribe { event ->
                    result = event
        }

        when:
        observable.produce('h-e-l-l-o')

        then:
        result == 'H&E&L&L&O'
    }

    @Unroll
    def 'subscribe to list observable'() {
        given:
        def result = ''
        Observable.from(list).subscribe { result += it }

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

    def 'filter to list observable'() {
        given:
        def result = []
        Observable.from([1, 5, 9, 12, 3, 8]).
                filter { it > 5 }.
                subscribe { event ->
            result << event
        }

        expect:
        result == [9, 12, 8]
    }

    def 'filter and map to list observable'() {
        given:
        def result = []
        Observable.from([1, 5, 9, 12, 3, 8]).
                filter { it < 5 }.
                map { 'H' * it }.
                subscribe { event ->
            result << event
        }

        expect:
        result == ['H', 'HHH']
    }

    def 'unsubscribe all'() {
        given:
        def observable = Observable.listen()
        observable.filter { it < 5 }.
                map { 'H' * it }.
                subscribe { event ->
                    println event
                }

        expect:
        observable.subscribers.size() == 1

        when:
        observable.removeSubscribers()

        then:
        observable.subscribers.size() == 0
    }

    def 'use observable in js works'() {
        when:
        def result = GrooScript.evaluateGroovyCode(
            new File('src/main/groovy/org/grooscript/rx/Observable.groovy').text +
'''
def result = []

Observable.from([1, 5, 9, 12, 3, 8]).
            filter { it < 5 }.
            map { 'H' * it }.
            subscribe { event ->
                result << event
            }

assert result == ['H', 'HHH']
'''
        )

        then:
        !result.assertFails
    }
}

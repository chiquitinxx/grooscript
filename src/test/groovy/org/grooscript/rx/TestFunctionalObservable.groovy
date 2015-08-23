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

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript

class TestFunctionalObservable extends FunctionalTest {

    String htmlResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.min.js'))
        result += script(jsFileText('jquery.min.js'))
        result += script(jsFileText('grooscript-tools.js'))
        result += script(GrooScript.convert(jsonResultClass + doRemoteCallCode))
        result += '<p>Hello!</p>'
        result += '</body></html>'
        result
    }

    void testDoJsonRemoteCall() {
        assertScript """
    @org.grooscript.asts.PhantomJsTest(url = '${FunctionalTest.HTML_ADDRESS}', waitSeconds = 1)
    void doTest() {
        assert clicks.number == 7, "Click number is \${clicks.number}"
    }
    doTest()
"""
    }

    private getDoRemoteCallCode() {
        '''
// tag::obsobs[]
import org.grooscript.jquery.GQueryImpl

def clicks = [number: 0]
def gQuery = new GQueryImpl()
gQuery.onReady {
    println 'Ready!'
    def observable = gQuery.observeEvent('p','click', clicks)
    observable.map { event -> 2 }.
      subscribe { num -> clicks.number = clicks.number + num }
    observable.map { event -> 5 }.
      subscribe { num -> clicks.number = clicks.number + num }
    $('p').click()
}
// clicks.number == 7
// end::obsobs[]
'''
    }
}
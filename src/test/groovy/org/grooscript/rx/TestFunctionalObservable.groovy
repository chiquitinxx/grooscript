package org.grooscript.rx

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript
import org.grooscript.JsGenerator

/**
 * Created by jorge on 17/11/14.
 */
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

    void setUp() {
        JsGenerator.generateGrooscriptToolsJs()
        super.setUp()
    }

    void testDoJsonRemoteCall() {
        assertScript '''
    @org.grooscript.asts.PhantomJsTest(url = 'http://localhost:8000/test', waitSeconds = 1)
    void doTest() {
        assert clicks.number == 7, "Click number is ${clicks.number}"
    }
    doTest()
'''
    }

    private getDoRemoteCallCode() {
        '''
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
'''
    }
}
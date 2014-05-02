package org.grooscript.jquery

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript
import org.grooscript.JsGenerator

/**
 * Created by jorge on 15/04/14.
 */
class TestFunctionalGQuery extends FunctionalTest {

    private static final FILE_NAME = 'test.html'

    String htmlResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.min.js'))
        result += script(jsFileText('jquery.min.js'))
        result += script(jsFileText('gQueryImpl.js'))
        result += script(GrooScript.convert(jsonResultClass))
        result += script('var gQuery = GQueryImpl();')
        result += script('$(document).ready(function() { gQuery.doRemoteCall("'+JSON_ADRESS+'", "GET", null, ' +
                'function(res) { result = res },' +
                'function(fail) { result = "FAIL!"}, Result); });')
        result += '<p class="result"></p>'
        result += '</body></html>'
        result
    }

    void setUp() {
        super.setUp()
        JsGenerator.generateJQuery()
    }

    void testDoJsonRemoteCall() {
        assertScript '''
    @org.grooscript.asts.PhantomJsTest(url = 'http://localhost:8000/test', waitSeconds = 1)
    void doTest() {
        assert result.result == true
        assert result.number == 5
        assert result.name == 'George'
        assert result.class.name == 'Result'
    }
    doTest()
'''
    }
}
package org.grooscript.jquery

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript
import org.grooscript.JsGenerator

/**
 * Created by jorge on 15/04/14.
 */
class TestFunctionalGQuery extends FunctionalTest {

    String htmlResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.min.js'))
        result += script(jsFileText('jquery.min.js'))
        result += script(jsFileText('grooscript-tools.js'))
        result += script(GrooScript.convert(jsonResultClass))
        result += script('var gQuery = GQueryImpl(); gQuery.onReady(function() { gQuery.doRemoteCall("'+JSON_ADRESS+'", "GET", null, ' +
                'function(res) { gQuery.html(".result", "OK"); result = res },' +
                'function(fail) { result = "FAIL!"}, Result); });')
        result += '<p class="result"></p>'
        result += '</body></html>'
        println result
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
        assert result.result == true
        assert result.number == 5
        assert result.name == 'George'
        assert result.class.name == 'Result'
        assert $('.result').html() == 'OK', "Wrong result ${$('.result').html()}"
    }
    doTest()
'''
    }
}
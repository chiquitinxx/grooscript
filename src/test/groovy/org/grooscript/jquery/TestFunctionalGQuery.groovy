package org.grooscript.jquery

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript

/**
 * Created by jorge on 15/04/14.
 */
class TestFunctionalGQuery extends FunctionalTest {

    String htmlResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.min.js'))
        result += script(jsFileText('jquery.min.js'))
        result += script(jsFileText('grooscript-tools.js'))
        result += script(GrooScript.convert(jsonResultClass + doRemoteCallCode))
        result += '<p class="result"></p>'
        result += '</body></html>'
        result
    }

    void testDoJsonRemoteCall() {
        assertScript """
    @org.grooscript.asts.PhantomJsTest(url = '${FunctionalTest.HTML_ADDRESS}', waitSeconds = 1)
    void doTest() {
        assert result.result == true
        assert result.number == 5
        assert result.name == 'George'
        assert result.class.name == 'Result'
        assert \$('.result').html() == 'OK', "Wrong result \${\$('.result').html()}"
    }
    doTest()
"""
    }

    private getDoRemoteCallCode() {
        """
//tag::gquery[]
import org.grooscript.jquery.GQueryImpl

def result = new Result()
def gQuery = new GQueryImpl()
gQuery.onReady {
    gQuery.doRemoteCall('${JSON_ADDRESS}', 'GET', null, { res ->
        gQuery('.result').html('OK')
        result = res
    }, {
        result = 'FAIL!'
    }, Result)
}
//end::gquery[]
"""
    }
}
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
package org.grooscript.jquery

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript

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
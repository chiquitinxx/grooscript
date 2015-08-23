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
package org.grooscript.builder

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript

class TestFunctionalBuilder extends FunctionalTest {

    String htmlResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.min.js'))
        result += script(jsFileText('jquery.min.js'))
        result += script(jsFileText('grooscript-tools.js'))
        result += script(GrooScript.convert(startFunction))
        result += script('$(document).ready(function() { $("body").append(getGrooviers()); });')
        result += '</body></html>'
        result
    }

    void testBuilderWorksWithGrooscriptTools() {
        assertScript """
    @org.grooscript.asts.PhantomJsTest(url = '${FunctionalTest.HTML_ADDRESS}', waitSeconds = 1)
    void doTest() {
        assert \$('li').size() == 4, "Incorrect number of li's loaded is: \${\$('li').size()}"
        assert htmlToTry() == "<br/><script src='aFile.js'></script>"
    }
    doTest()
"""
    }

    private getStartFunction() {
        '''
def getGrooviers = { ->
    HtmlBuilder.build {
        ul {
            ['Groovy', 'Grails', 'Gradle', 'Griffon'].each {
                li it
            }
        }
    }
}
def htmlToTry = { ->
    HtmlBuilder.build {
        br()
        script src: 'aFile.js'
    }
}
'''
    }
}

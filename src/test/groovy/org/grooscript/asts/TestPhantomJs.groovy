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
package org.grooscript.asts

import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.grooscript.FunctionalTest

import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.USER_HOME

class TestPhantomJs extends FunctionalTest {

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    void countPTagsInPage() {
        assert $('p').size() == 1, "Number of p's in page is ${$('p').size()}"
        def title = $('title')
        assert title[0].text == 'Title', "Title is ${title[0].text}"
    }

    void testWorksFindingJsFilesInUserHomeDir() {

        removeLocalPhantomJsLibsAndProperties()
        def tempDirectory = new File(localTempFolder)

        countPTagsInPage()

        assert tempDirectory.exists() && tempDirectory.isDirectory()

        countPTagsInPage()
    }

    void testErrorPhantomJsPath() {
        if (System.getenv('PHANTOMJS_HOME')) {
            //If PHANTOMJS_HOME is setted in your system you can't test this. Can't remove environment vars
            assert true
        } else {
            System.properties.remove('PHANTOMJS_HOME')
            try {
                countPTagsInPage()
                fail 'Error not thrown'
            } catch (AssertionError e) {
                assert e.message ==
                        'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder. Expression: false'
            }
        }
    }

    void testPhantomJsFromScript() {
        assertScript """
            import org.grooscript.asts.PhantomJsTest

            @PhantomJsTest(url = '${HTML_ADDRESS}')
            void phantomTest() {
                assert true
            }
            phantomTest()
        """
    }

    void testPhantomJsFromATest() {
        countPTagsInPage()
    }

    void testWithoutUrlParameter() {
        try {
            assertScript '''
                import org.grooscript.asts.PhantomJsTest

                @PhantomJsTest
                void test() {
                    assert true
                }
            '''
            fail 'Url parameter is mandatory'
        } catch (MultipleCompilationErrorsException e) {
            assert e.message.contains('Have to define url parameter')
        }
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    void countPsFailAssert() {
        assert $('p').size() > 5, "Number of links in page is ${$('p').size()}"
    }

    void testPhantomJsFailAssert() {
        try {
            countPsFailAssert()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert true
        } catch (Exception e) {
            fail 'Exception not assert error'
        }
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    void testDirectPhantomJs() {
        gSconsoleInfo = true
        println $('p').text()
        assert $('p').text().contains('Welcome'), "p html is 'Welcome'"
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS, capture = 'local.png')
    void captureImage() {
        console.log('BYE')
    }

    void testCaptureImage() {
        def file = new File('local.png')
        try {
            assert !file.exists()
            captureImage()
            assert file.exists() && file.isFile()
        } finally {
            if (file.exists()) {
                file.delete()
            }
        }
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    void failMethod() {
        console.log(FAIL)
    }

    void testTimeout10Seconds() {
        Date start = new Date()
        try {
            failMethod()
            fail 'Not getting timeout error.'
        } catch (AssertionError e) {
            println 'Time in miliseconds: ' + (new Date().time - start.time)
            assert true, 'Timeout Error'
        }
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    void testExpectedElements(element, expectedSize) {
        assert $(element).size == expectedSize,"Number of '${element}' in page is ${$(element).size()}"
    }

    void testPassParameters() {
        testExpectedElements('p', 1)
        testExpectedElements('body', 1)
    }

    void testAssertError() {
        try {
            testExpectedElements('a', 10000)
            fail 'Must throw assert error'
        } catch (AssertionError e) {
            assert e.message.startsWith('Number of \'a\' in page is ')
        }
    }

    @PhantomJsTest(url = 'http://localhost:7777')
    void wrongUrl() {
        assert true
    }

    void testWrongUrlGetAssertFail() {
        try {
            wrongUrl()
            fail 'Wrong url error not throw'
        } catch (AssertionError e) {
            assert e.message == 'Fail loading url: fail. Expression: false'
        }
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS, waitSeconds = 2)
    void testWaitSeconds() {
        assert $('p').text().contains('Welcome'), "p html is 'Welcome'"
    }

    @PhantomJsTest(url = 'http://localhost:8000/test', info = true)
    void testWithInfo() {
        assert true
    }

    void testWithoutAnnotation() {
        PhantomJsTestImpl.doPhantomJsTest(HTML_ADDRESS, 'function hello() {console.log("Hello!");}', 'hello')
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    def returnMap() {
        [list: [1, 2], str: 'string', number: 9, dec: 8.34, jq: $('p').text()]
    }

    void testReturnMap() {
        def result = returnMap()
        assert result == [ list: [1, 2], str: 'string', number: 9, dec: 8.34, jq: 'Welcome']
    }

    @PhantomJsTest(url = FunctionalTest.HTML_ADDRESS)
    def returnString() {
        $('body').html()
    }

    void testReturnString() {
        def result = returnString()
        assert result == '<p>Welcome</p>'
    }

    String htmlResponse() {
        '<html><head><title>Title</title></head><body><p>Welcome</p></body></html>'
    }

    String getLocalTempFolder() {
        USER_HOME + SEP + '.grooscript'
    }

    void removeLocalPhantomJsLibsAndProperties() {
        System.properties.remove('JS_LIBRARIES_PATH')

        def tempDirectory = new File(localTempFolder)
        if (tempDirectory.exists() && tempDirectory.isDirectory()) {
            tempDirectory.deleteDir()
        }
    }
}

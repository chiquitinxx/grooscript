package org.grooscript.asts

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.grooscript.FunctionalTest

/**
 * User: jorgefrancoleza
 * Date: 15/08/13
 */
class TestPhantomJs extends FunctionalTest {

    String testResponse() {
        '<html><head><title>Title</title></head><body><p>Welcome</p></body></html>'
    }

    @PhantomJsTest(url = 'http://localhost:8000/test')
    void countPTagsInPage() {
        assert $('p').size() == 1,"Number of p's in page is ${$('p').size()}"
        def title = $("title")
        assert title[0].text=='Title',"Title is ${title[0].text}"
        def ps = $('p')
        ps.each {
            println it
        }
    }

    void testWorksFindingJsFilesInUserHomeDir() {

        System.properties.remove('JS_LIBRARIES_PATH')

        def tempDirectory = new File(System.getProperty('user.home')+File.separator+'.grooscript')
        if (tempDirectory.exists() && tempDirectory.isDirectory()) {
            tempDirectory.deleteDir()
        }

        countPTagsInPage()

        assert tempDirectory.exists() && tempDirectory.isDirectory()

        countPTagsInPage()

        tempDirectory.deleteDir()
    }

    void testErrorPhantomJsPath() {
        System.properties.remove('PHANTOMJS_HOME')
        try {
            countPTagsInPage()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert e.message ==
               'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder. Expression: false'
        }
    }

    void testPhantomJsFromScript() {
        assertScript '''
            import org.grooscript.asts.PhantomJsTest

            @PhantomJsTest(url = 'http://localhost:8000/test')
            void phantomTest() {
                assert true
            }
            phantomTest()
        '''
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

    @PhantomJsTest(url = 'http://localhost:8000/test')
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

    @PhantomJsTest(url = 'http://localhost:8000/test')
    void testDirectPhantomJs() {
        gSconsoleInfo = true
        println $('p').text()
        assert $('p').text().contains('Welcome'), "p html is 'Welcome'"
    }

    @PhantomJsTest(url = 'http://localhost:8000/test', capture = 'local.png')
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

    @PhantomJsTest(url = 'http://localhost:8000/test')
    void failMethod() {
        console.log(FAIL)
    }

    void testTimeout10Seconds() {
        Date start = new Date()
        try {
            failMethod()
            fail 'Not getting timeout error.'
        } catch(AssertionError e) {
            println 'Time in miliseconds: ' + (new Date().time - start.time)
            assert true, 'Timeout Error'
        }
    }

    @PhantomJsTest(url = 'http://localhost:8000/test')
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
        } catch(AssertionError e) {
            assert e.message == 'Fail loading url: fail. Expression: false'
        }
    }

    @PhantomJsTest(url = 'http://localhost:8000/test', waitSeconds = 2)
    void testWaitSeconds() {
        assert $('p').text().contains('Welcome'), "p html is 'Welcome'"
    }

    @PhantomJsTest(url = 'http://localhost:8000/test', info = true)
    void testWithInfo() {
        assert true
    }

    void testWithoutAnnotation() {
        PhantomJsTestImpl.doPhantomJsTest(FULL_ADRESS, 'function hello() {console.log("Hello!");}', 'hello')
    }
}

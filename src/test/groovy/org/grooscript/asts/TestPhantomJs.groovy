package org.grooscript.asts

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

/**
 * User: jorgefrancoleza
 * Date: 15/08/13
 */
class TestPhantomJs extends GroovyTestCase {

    static final PHANTOMJS_HOME = '/Applications/phantomjs'
    static final JS_LIBRARIES_PATH = 'src/main/resources/META-INF/resources'
    static final PORT = 8000
    static final ACTION = '/test'
    static final String FULL_ADRESS = "http://localhost:${PORT}${ACTION}"

    HttpServer server

    class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = '<html><head><title>Title</title></head><body><p>Welcome</p></body></html>'
            t.sendResponseHeaders(200, response.length())
            OutputStream os = t.getResponseBody()
            os.write(response.getBytes())
            os.close()
        }
    }

    private startServer() {
        server = HttpServer.create(new InetSocketAddress(PORT), 0)
        server.createContext(ACTION, new MyHandler())
        server.setExecutor(null) // creates a default executor
        server.start()
    }

    private stopServer() {
        server.stop(0)
    }

    void setUp() {
        System.setProperty('PHANTOMJS_HOME',PHANTOMJS_HOME)
        System.setProperty('JS_LIBRARIES_PATH',JS_LIBRARIES_PATH)
        startServer()
    }

    void tearDown() {
        stopServer()
    }

    @PhantomJsTest(url = 'http://localhost:8000/test')
    void countPs() {
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

        countPs()

        assert tempDirectory.exists() && tempDirectory.isDirectory()

        countPs()

        tempDirectory.deleteDir()
    }

    void testErrorPhantomJsPath() {
        System.properties.remove('PHANTOMJS_HOME')
        try {
            countPs()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert e.message ==
               'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder. Expression: false'
        }
    }

    void testPhantomJs() {
        countPs()
    }

    @PhantomJsTest(url = 'http://localhost:8000/test')
    void countPsFailAssert() {
        assert $('p').size() > 5,"Number of links in page is ${$('p').size()}"
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
            assert e.message == 'Fail loading url... Expression: false'
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
        PhantomJsTestImpl.doPhantomJsTest(FULL_ADRESS, 'function hello() {console.log("Hello!");}',
            'hello', 0, null, null, null, true)
    }

    /*
    @PhantomJsTest(url = 'http://localhost:8080/grooscript-vertx/main/vertxEvents', waitSeconds = 2)
    void testWaitSecondsLocal() {
        assert $('#points').html() == '.',"points Html after is ${$('#points').html()}"
    }*/
}

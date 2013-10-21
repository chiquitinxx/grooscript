package org.grooscript.asts

/**
 * User: jorgefrancoleza
 * Date: 15/08/13
 */
class TestPhantomJs extends GroovyTestCase {

    static final PHANTOMJS_HOME = '/Applications/phantomjs'
    static final JS_LIBRARIES_PATH = 'src/main/resources/META-INF/resources'

    void setUp() {
        System.setProperty('PHANTOMJS_HOME',PHANTOMJS_HOME)
        System.setProperty('JS_LIBRARIES_PATH',JS_LIBRARIES_PATH)
    }

    @PhantomJsTest(url = 'http://www.grails.org/')
    void countLinks() {
        assert $('a').size() > 50,"Number of links in page is ${$('a').size()}"
        def title = $("title")
        assert title[0].text=='Grails - The search is over.',"Title is ${title[0].text}"
        def links = $('a')
        links.each {
            println it
        }
    }

    void testWorksFindingJsFilesInUserHomeDir() {

        System.properties.remove('JS_LIBRARIES_PATH')

        def tempDirectory = new File(System.getProperty('user.home')+File.separator+'.grooscript')
        if (tempDirectory.exists() && tempDirectory.isDirectory()) {
            tempDirectory.deleteDir()
        }

        countLinks()

        assert tempDirectory.exists() && tempDirectory.isDirectory()

        countLinks()

        tempDirectory.deleteDir()
    }

    void testErrorPhantomJsPath() {
        System.properties.remove('PHANTOMJS_HOME')
        try {
            countLinks()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert e.message ==
               'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder. Expression: false'
        }
    }

    void testPhantomJs() {
        countLinks()
    }

    @PhantomJsTest(url = 'http://www.grails.org/')
    void countLinksFailAssert() {
        assert $('a').size() < 5,"Number of links in page is ${$('a').size()}"
    }

    void testPhantomJsFailAssert() {
        try {
            countLinksFailAssert()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert true
        } catch (Exception e) {
            fail 'Exception not assert error'
        }
    }

    @PhantomJsTest(url = 'http://groovy.codehaus.org')
    void testDirectPhantomJs() {
        gSconsoleInfo = true
        println $('#moto').text()
        assert $('a').size > 50,"Number of links in page is ${$('a').size()}"
        assert $('#moto').text().contains('A dynamic language'), "Id=Moto contains 'A dynamic language'"
    }

    @PhantomJsTest(url = 'http://groovy.codehaus.org', capture = 'groovy.png')
    void captureImage() {
        console.log('BYE')
    }

    void testCaptureImage() {
        def file = new File('groovy.png')
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

    @PhantomJsTest(url = 'http://groovy.codehaus.org')
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

    @PhantomJsTest(url = 'http://groovy.codehaus.org')
    void testExpectedElements(element, expectedSize) {
        assert $(element).size > expectedSize,"Number of '${element}' in page is ${$(element).size()}"
    }

    void testPassParameters() {
        testExpectedElements('a', 65)
        testExpectedElements('div', 85)
    }

    @PhantomJsTest(url = 'http://mockmockmock.mock')
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

    @PhantomJsTest(url = 'http://groovy.codehaus.org', waitSeconds = 2)
    void testWaitSeconds() {
        assert $('#moto').text().contains('A dynamic language'), "Id=Moto contains 'A dynamic language'"
    }

    @PhantomJsTest(url = 'http://groovy.codehaus.org', info = true)
    void testWithInfo() {
        assert true
    }

    /*
    @PhantomJsTest(url = 'http://localhost:8080/grooscript-vertx/main/vertxEvents', waitSeconds = 2)
    void testWaitSeconds() {
        assert $('#points').html() == '.',"points Html after is ${$('#points').html()}"
    }*/
}

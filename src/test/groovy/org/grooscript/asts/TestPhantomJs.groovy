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

    @PhantomJsTest(url = 'http://www.grails.org/')
    void countLinksFailAssert() {
        assert $('a').size() < 5,"Number of links in page is ${$('a').size()}"
    }

    void testErrorPhantomJsPath() {
        System.properties.remove('PHANTOMJS_HOME')
        try {
            countLinks()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert e.message == 'Need define property PHANTOMJS_HOME, PhantomJs folder. Expression: false'
        }
    }

    void testErrorJsLibraries() {
        System.properties.remove('JS_LIBRARIES_PATH')
        try {
            countLinks()
            fail 'Error not thrown'
        } catch (AssertionError e) {
            assert e.message ==
                'Need define property JS_LIBRARIES_PATH, folder with grooscript.js and kimbo.min.js. Expression: false'
        }
    }

    void testPhantomJs() {
        countLinks()
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
            file.delete()
        }
    }
}

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

    @PhantomJsTest(url='http://www.grails.org/')
    void countLinks() {
        assert $('a').size() > 50,"Number of links in page is ${$('a').size()}"
        def title = $("title")
        assert title[0].text=='Grails - The search is over.',"Title is ${title[0].text}"
        def links = $('a')
        links.each {
            println it
        }
    }

    void testPhantomJs() {
        assert System.getProperty('PHANTOMJS_HOME') == PHANTOMJS_HOME
        assert System.getProperty('JS_LIBRARIES_PATH') == JS_LIBRARIES_PATH
        countLinks()
    }

    @PhantomJsTest(url='http://www.grails.org/')
    void testDirectPhantomJs() {
        assert $('a').size() > 50,"Number of links in page is ${$('a').size()}"
    }
}

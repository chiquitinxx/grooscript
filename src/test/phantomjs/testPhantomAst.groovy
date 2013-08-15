import org.grooscript.asts.PhantomJsTest

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
System.setProperty('PHANTOMJS_HOME','/Applications/phantomjs')
System.setProperty('JS_LIBRARIES_PATH','../../../src/main/resources/META-INF/resources')

@PhantomJsTest(url='http://www.grails.org')
void testCountLinks() {
    assert $('a').size() > 50,"Number of links in page is ${$('a').size()}"
    def title = $("title")
    assert title[0].text=='Grails - The search is over.',"Title is ${title[0].text}"
    def links = $('a')
    links.each {
        println it
    }
}

testCountLinks()
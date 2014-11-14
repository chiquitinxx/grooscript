@GrabConfig(systemClassLoader=true)
@Grab('org.grooscript:grooscript:0.6.2')

import org.grooscript.asts.PhantomJsTest

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
System.setProperty('PHANTOMJS_HOME','../../../node_modules/phantomjs')

@PhantomJsTest(url = 'http://www.grails.org', info = true)
void testCountLinks() {
    assert $('a').size() > 50,"Number of links in page is ${$('a').size()}"
    def title = $("title")
    assert title.text() == 'Grails - The search is over.',"Title is ${title.text()}"
}

testCountLinks()
@GrabConfig(systemClassLoader=true)
@Grab('org.grooscript:grooscript:1.1.2')

import org.grooscript.asts.PhantomJsTest

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
System.setProperty('PHANTOMJS_HOME','../../../node_modules/phantomjs')

@PhantomJsTest(url = 'http://www.grails.org', info = true)
void testCountLinks() {
    assert $('a').size() > 40, "Number of links in page is ${$('a').size()}"
    def title = $("title")
    assert title.text() == 'The Grails Framework',"Title is ${title.text()}"
}

testCountLinks()
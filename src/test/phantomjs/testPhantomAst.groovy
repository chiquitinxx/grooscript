import org.grooscript.asts.PhantomJsTest

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */

class A {
    @PhantomJsTest(url='http://groovy.codehaus.org')
    void testCountLinks() {
        assert $('a').length > 50,"Number of links in page is ${$('a').length}"
        assert $("title").text()=='Groovy - Home',"Yeah!"
    }
}

def a = new A()
a.testCountLinks()

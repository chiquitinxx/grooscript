import org.grooscript.asts.PhantomJsTest

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */

class A {
    @PhantomJsTest(url='http://www.elpais.es')
    void testCountLinks() {
        assert $('a').size() == 5,"Number of links in page is ${$('a').size()}"
    }
}

def a = new A()
a.testCountLinks()

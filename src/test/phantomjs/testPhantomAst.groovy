import org.grooscript.asts.PhantomJsTest

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@PhantomJsTest(url='http://groovy.codehaus.org',jsPath = '/Users/jorgefrancoleza/desarrollo/grooscript/src/main/resources/META-INF/resources')
//@PhantomJsTest(url='http://www.elpais.es',jsPath = '/Users/jorgefrancoleza/desarrollo/grooscript/src/main/resources/META-INF/resources')
void testCountLinks() {
    assert $('a').size() > 50,"Number of links in page is ${$('a').size()}"
    def title = $("title")
    assert title[0].text=='Groovy - Home',"Title is ${title[0].text}"
    def links = $('a')
    links.each {
        println it
    }
    //assert $('a').length > 100,"Number of links in page is ${$('a').length}"
    //assert $("title").text()=='Groovy - Home',"Title is ${$("title").text()}"
}

testCountLinks()
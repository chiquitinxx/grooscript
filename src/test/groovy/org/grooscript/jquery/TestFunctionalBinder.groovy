package org.grooscript.jquery

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript
import org.grooscript.asts.PhantomJsTest

/**
 * Created by jorge on 13/04/14.
 */
class TestFunctionalBinder extends FunctionalTest {

    String testResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.js'))
        result += script(jsFileText('kimbo.min.js'))
        //result += script(jsFileText('grooscript-binder.js'))
        result += script('$(document).ready(function() { $("body").append("<p>Grooscript</p>")});')
        result + '</body></html>'
    }

    void testInitial() {
        assertScript '''
    @org.grooscript.asts.PhantomJsTest(url = 'http://localhost:8000/test', waitSeconds = 1)
    void doTest() {
        assert $('p').html() == 'Grooscript', "Value is ${$('p').html()}"
    }
    doTest()
'''
    }

    /*@PhantomJsTest(url = 'http://localhost:8000/test')
    void testInitial() {
        gSconsoleInfo = true
        println 'Hello!'
    }*/

    private script(text) {
        "<script>${text}</script>"
    }

    private jsFileText(fileName) {
        GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
    }
}
package org.grooscript.jquery

import org.grooscript.FunctionalTest
import org.grooscript.GrooScript
import org.grooscript.JsGenerator
import org.grooscript.asts.PhantomJsTest

/**
 * Created by jorge on 13/04/14.
 */
class TestFunctionalBinder extends FunctionalTest {

    String testResponse() {
        def result = '<html><head><title>Title</title></head><body>'
        result += script(jsFileText('grooscript.js'))
        result += script(jsFileText('jquery.min.js'))
        result += script(jsFileText('grooscript-binder.js'))
        result += script(jsFileText('jQueryImpl.js'))
        result += script(GrooScript.convert('class Book { String author; String title}'))
        result += script('var book = Book(); var binder = Binder({jQuery: JQueryImpl()});')
        result += script('$(document).ready(function() { binder.bindAllProperties(book); book.setAuthor("Jorge"); book.setTitle("Grooscript");});')
        result += '<input type="text" id="author">'
        result += '<input type="text" name="title">'
        result += '</body></html>'
        result
    }

    void setUp() {
        super.setUp()
        JsGenerator.generateJQuery()
        JsGenerator.generateBinder()
    }

    void testInitial() {
        assertScript '''
    @org.grooscript.asts.PhantomJsTest(url = 'http://localhost:8000/test', waitSeconds = 1)
    void doTest() {
        assert $('#author').val() == 'Jorge', "Value is: ${$('#author').val()}"
        assert $("input[name='title']").val() == 'Grooscript', "Value is: ${$("[name='title']").val()}"
    }
    doTest()
'''
    }

    private script(text) {
        "<script>${text}</script>\n"
    }

    private jsFileText(fileName) {
        GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
    }
}
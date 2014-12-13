package org.grooscript.test

import org.grooscript.JsGenerator

import static org.grooscript.util.Util.*
import org.grooscript.GrooScript
import org.grooscript.convert.GsConverter
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 20/04/13
 */
class ConversionMixin {

    GsConverter converter = new GsConverter()
    NodeJs nodeJs = new NodeJs()

    /**
     * Read a groovy file and returns javascript conversion object
     * @param nameOfFile name of groovy file
     * @param jsResultOnConsole true if wanna println js result script
     * @param options for the GsConverter
     * @param textSearch in the js conversion script
     * @param textReplace replace searched text with this one
     */
    JsTestResult convertAndEvaluateWithJsEngine(nameOfFile, jsResultOnConsole = false, options = [:], textSearch = null, textReplace = null) {

        String jsScript = convertFile(nameOfFile, options)

        if (textSearch && jsScript.indexOf(textSearch) >= 0) {
            jsScript = jsScript.substring(0, jsScript.indexOf(textSearch)) +
                    textReplace + jsScript.substring(jsScript.indexOf(textSearch) + textSearch.size())
        }

        if (jsResultOnConsole) {
            GsConsole.message("jsScript Result->${LINE_SEPARATOR}$jsScript")
        }

        JavascriptEngine.jsEval(jsScript)
    }

    String convertFile(nameOfFile, options = null) {
        def file = JavascriptEngine.getGroovyTestScript(nameOfFile)
        converter.toJs(file.text, options)
    }

    boolean checkBuilderCodeAssertsFails(String code, jsResultOnConsole = false, options = [:]) {

        if (options) {
            options.each { key, value ->
                converter."$key" = value
            }
        }

        String jsScript = converter.toJs(code)

        def builderCode = GrooScript.convert(new File(JsGenerator.HTML_BUILDER_SOURCE).text)
        jsScript = builderCode + jsScript

        if (jsResultOnConsole) {
            GsConsole.message("jsScript Result->${LINE_SEPARATOR}$jsScript")
        }

        JavascriptEngine.jsEval(jsScript).assertFails
    }

    JsTestResult convertAndEvaluateWithNode(jsScript) {
        nodeJs.evaluate(jsScript)
    }

    boolean convertAndEvaluate(String fileName, jsResultOnConsole = false, options = [:], textSearch = null, textReplace = null) {
        def evaluationJsEngine = convertAndEvaluateWithJsEngine(fileName, jsResultOnConsole, options, textSearch, textReplace)
        if (evaluationJsEngine.assertFails) {
            println evaluationJsEngine.console
        }
        !evaluationJsEngine.assertFails && !convertAndEvaluateWithNode(evaluationJsEngine.jsScript).assertFails
    }
}

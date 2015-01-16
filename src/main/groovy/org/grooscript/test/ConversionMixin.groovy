package org.grooscript.test

import org.grooscript.JsGenerator
import org.grooscript.GrooScript
import org.grooscript.convert.GsConverter
import org.grooscript.util.GsConsole

import static org.grooscript.util.Util.*

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

    boolean convertAndEvaluate(
            String fileName, jsResultOnConsole = false, options = [:], textSearch = null, textReplace = null) {
        if (JAVA_VERSION >= '1.8' && fileName in filesThatFailsInJava8) {
            String jsCode = convertFile(fileName, options)
            return !convertAndEvaluateWithNode(jsCode).assertFails
        } else {
            def evaluationJsEngine =
                    convertAndEvaluateWithJsEngine(fileName, jsResultOnConsole, options, textSearch, textReplace)
            if (evaluationJsEngine.assertFails) {
                println evaluationJsEngine.console
            }
            return !evaluationJsEngine.assertFails && !convertAndEvaluateWithNode(evaluationJsEngine.jsScript).assertFails
        }
    }

    private getFilesThatFailsInJava8()
    {
        [
            'advanced/PropertiesAndMethods',
            'advanced/MasterScoping',
            'advanced/MethodMissingTwo',
            'classes/StaticProperties',
            'contribution/MySelf11',
            'staticRealm',
            'doc/Object',
            'advanced/MethodPointer',
        ]
    }
}

package org.grooscript.test

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

    /**
     * Read a groovy file and returns javascript conversion object
     * @param nameOfFile name of groovy file
     * @param jsResultOnConsole true if wanna println js result script
     * @param options for the GsConverter
     * @param textSearch in the js conversion script
     * @param textReplace replace searched text with this one
     * @return map with results [assertFails:(true or false),...]
     */
    def readAndConvert(nameOfFile, jsResultOnConsole = false, options = [:], textSearch = null, textReplace = null) {

        String jsScript = convertFile(nameOfFile, options)

        if (textSearch && jsScript.indexOf(textSearch) >= 0) {
            jsScript = jsScript.substring(0, jsScript.indexOf(textSearch)) +
                    textReplace + jsScript.substring(jsScript.indexOf(textSearch) + textSearch.size())
        }

        if (jsResultOnConsole) {
            GsConsole.message("jsScript Result->${LINE_JUMP}$jsScript")
        }

        TestJs.jsEval(jsScript)
    }

    String convertFile(nameOfFile, options = [:]) {
        def file = TestJs.getGroovyTestScript(nameOfFile)
        if (options) {
            options.each { key, value ->
                converter."$key" = value
            }
        }

        converter.toJs(file.text)
    }

    boolean checkBuilderCodeAssertsFails(String code, jsResultOnConsole = false, options = [:]) {

        if (options) {
            options.each { key, value ->
                converter."$key" = value
            }
        }

        String jsScript = converter.toJs(code)

        def builderCode = GrooScript.convert(new File('src/main/groovy/org/grooscript/builder/Builder.groovy').text)
        jsScript = builderCode + jsScript

        if (jsResultOnConsole) {
            GsConsole.message("jsScript Result->${LINE_JUMP}$jsScript")
        }

        TestJs.jsEval(jsScript).assertFails
    }
}

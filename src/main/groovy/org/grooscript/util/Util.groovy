package org.grooscript.util

import org.grooscript.convert.NativeFunction
import org.grooscript.test.JavascriptEngine
import org.grooscript.convert.GsConverter
import org.grooscript.test.JsTestResult

/**
 * JFL 29/08/12
 */
class Util {

    static final USER_HOME = System.getProperty('user.home')
    static final SEP = System.getProperty('file.separator')
    static final LINE_SEPARATOR = System.getProperty('line.separator')
    static final String OS_NAME = System.getProperty('os.name')
    static final JS_EXTENSION = '.js'
    static final GROOVY_EXTENSION = '.groovy'
    static final JAVA_EXTENSION = '.java'
    static final JAVASCRIPT_EXTENSION = '.js'

    //Where Js stuff is
    static String getJsPath() {
        "src${SEP}main${SEP}resources${SEP}META-INF${SEP}resources${SEP}"
    }

    //Location of groovy script examples
    static String getGroovyTestPath() {
        "src${SEP}test${SEP}resources${SEP}"
    }

    //Location of groovy test src examples
    static String getGroovyTestSrcPath() {
        "src${SEP}test${SEP}src${SEP}"
    }

    /**
     * Gets a Js file from js directory
     * @param name
     * @return
     */
    static File getJsFile(String name) {
        def result
        if (name) {
            def finalName = name
            if (!finalName.endsWith(JS_EXTENSION)) {
                finalName += JS_EXTENSION
            }

            File file = new File(getJsPath() + finalName)
            if (file && file.exists() && file.isFile()) {
                result = file
            }
        }
        result
    }

    /**
     * Get a groovy file in test path
     * @param name
     * @return
     */
    static File getGroovyTestScriptFile(String name) {
        def result
        if (name) {
            def finalName = name
            if (!finalName.endsWith(GROOVY_EXTENSION)) {
                finalName += GROOVY_EXTENSION
            }

            File file = new File(getGroovyTestPath() + finalName)
            if (file && file.exists() && file.isFile()) {
                result = file
            } else {
                result = new File(getGroovyTestSrcPath() + finalName)
            }
        }
        result
    }

    /**
     * Full process a script
     * @param script code to process
     * @param jsFile grooscript.js file
     */
    static JsTestResult fullProcessScript(String script, File jsFile) {

        def result = new JsTestResult()

        def jsScript = null
        try {

            def converter = new GsConverter()
            jsScript = converter.toJs(script)

            if (jsFile) {
                result = JavascriptEngine.jsEvalWithFile(jsScript, jsFile)
            } else {
                result = JavascriptEngine.jsEval(jsScript)
            }

        } catch (e) {
            result.exception = e.message
        }

        result.jsScript = jsScript

        result
    }

    /**
     * Full process a script
     * @param script
     */
    static JsTestResult fullProcessScript(String script) {
        fullProcessScript(script, null)
    }

    static List<NativeFunction> getNativeFunctions(String text, String className = null) {

        List<NativeFunction> listResult = []

        def seg = text
        def pat = /(?ms)@(GsNative|org\.grooscript\.asts\.GsNative).+\w+\s*\(.*\)\s*\{\s*(\/\*).*(\*\/)/

        seg.eachMatch(pat) { match ->
            def list = match[0].split('@GsNative')

            list.each { lines ->

                if (lines && lines.trim().size() > 4) {

                    def line = lines.substring(lines.indexOf('/*') + 2, lines.indexOf('*/'))
                    def function = (lines =~ /\w+\s*\(/)[0]

                    function = function.substring(0,function.length() - 1)
                    listResult << new NativeFunction(
                            className: className, code: line.trim(), methodName: function.trim())
                }
            }
        }

        listResult
    }

    static boolean groovyVersionAtLeast(String version) {
        groovyVersion >= version
    }

    static String getGroovyVersion() {
        GroovySystem.version
    }

    static String getGrooscriptVersion() {
        Class.forName('org.grooscript.GrooScript').package.implementationVersion ?: 'snapshot'
    }

    static boolean isWindows () {
        // Use capital name for Win8+
        OS_NAME.startsWith('windows') || OS_NAME.startsWith('Windows')
    }
}

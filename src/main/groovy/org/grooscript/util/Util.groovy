package org.grooscript.util

import org.grooscript.convert.NativeFunction
import org.grooscript.test.JavascriptEngine
import org.grooscript.convert.GsConverter
import org.grooscript.test.JsTestResult

/**
 * JFL 29/08/12
 */
class Util {

    static final String USER_HOME = System.getProperty('user.home')
    static final String SEP = System.getProperty('file.separator')
    static final String LINE_SEPARATOR = System.getProperty('line.separator')
    static final String OS_NAME = System.getProperty('os.name')
    static final String JS_EXTENSION = '.js'
    static final String GROOVY_EXTENSION = '.groovy'
    static final String JAVA_EXTENSION = '.java'

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
        File result = null
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

    static List<NativeFunction> getNativeFunctions(String sourceCode, String className = null) {

        List<NativeFunction> listResult = []

        def pat = /(?ms)@(GsNative|org\.grooscript\.asts\.GsNative).+\w+\s*\(.*\)\s*\{\s*(\/\*).*(\*\/)/

        sourceCode?.eachMatch(pat) { match ->
            def list = match[0].split('@GsNative')

            list.each { functionWithNativeCode ->

                if (functionWithNativeCode && functionWithNativeCode.trim().size() > 4) {

                    def jsCode = functionWithNativeCode.substring(
                            functionWithNativeCode.indexOf('/*') + 2,
                            functionWithNativeCode.indexOf('*/')
                    ).trim()
                    def function = (functionWithNativeCode =~ /\w+\s*\(/)[0]

                    function = function.substring(0, function.length() - 1)
                    listResult << new NativeFunction(
                            className: className ?: classNameFinder(sourceCode, functionWithNativeCode),
                            code: jsCode, methodName: function.trim()
                    )
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

    private static classNameFinder(String sourceCode, String nativeFunctionCode) {
        def jsCodePos = sourceCode.indexOf(nativeFunctionCode)
        def listContainers = []
        ['class', 'trait'].each { type ->
            def matcher = sourceCode =~ /\b${type}\s+(\w+)/
            matcher.each {
                listContainers << [name: it[1], pos: sourceCode.indexOf(it[0])]
            }
        }
        def lastContainer = listContainers.findAll { it.pos < jsCodePos }.max { it.pos }
        lastContainer ? lastContainer.name : null
    }
}

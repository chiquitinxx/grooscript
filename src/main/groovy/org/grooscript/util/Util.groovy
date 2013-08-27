package org.grooscript.util

import org.grooscript.test.TestJs
import org.grooscript.GsConverter

/**
 * JFL 29/08/12
 */
class Util {

    static final USER_DIR = System.getProperty('user.dir')
    static final SEP = System.getProperty('file.separator')
    static final LINE_JUMP = '\n'
    static final JS_EXTENSION = '.js'
    static final GROOVY_EXTENSION = '.groovy'

    //Where Js stuff is
    static getJsPath() {
        def path = "$USER_DIR${SEP}src${SEP}main${SEP}resources${SEP}META-INF${SEP}resources${SEP}"
        def file = new File(path)
        if (!file || !file.exists() || !file.isDirectory()) {
            path = "$USER_DIR${SEP}web${SEP}scripts${SEP}"
        }
        file = new File(path)
        if (!file || !file.exists() || !file.isDirectory()) {
            path = "$USER_DIR${SEP}webapp${SEP}web${SEP}scripts${SEP}"
        }
        path
    }

    //Location of groovy script examples
    static getGroovyTestPath() {
        "$USER_DIR${SEP}src${SEP}test${SEP}resources${SEP}"
    }

    /**
     * Gets a Js file from js directory
     * @param name
     * @return
     */
    static getJsFile(String name) {
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
    static getGroovyTestScriptFile(String name) {
        def result
        if (name) {
            def finalName = name
            if (!finalName.endsWith(GROOVY_EXTENSION)) {
                finalName += GROOVY_EXTENSION
            }

            File file = new File(getGroovyTestPath() + finalName)
            if (file && file.exists() && file.isFile()) {
                result = file
            }
        }
        result
    }

    /**
     * Full process a script
     * @param script code to process
     * @param jsFile grooscript.js file
     * @return map with exception, jsScript, assertFails, ...
     */
    static fullProcessScript(String script, File jsFile) {

        def result = [:]

        def jsScript = null
        try {

            def converter = new GsConverter()
            jsScript = converter.toJs(script)

            if (jsFile) {
                result = TestJs.jsEvalWithFile(jsScript, jsFile)
            } else {
                result = TestJs.jsEval(jsScript)
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
     * @return map with exception,jsScript,assertFails,...
     */
    static fullProcessScript(String script) {
        fullProcessScript(script, null)
    }

    /**
     * Get map with native functions
     * @param text to be converted
     * @return map [name:code]
     */
    static getNativeFunctions(String text) {

        def mapResult = [:]

        def seg = text

        def pat = /(?ms)@(GsNative|org\.grooscript\.GsNative).+\w+\s*\(.*\)\s*\{\s*(\/\*).*(\*\/\s*\})/

        seg.eachMatch(pat) { match ->
            //println 'Item->'+match[0]
            def list = match[0].split('@GsNative')

            list.each { lines ->

                if (lines && lines.trim().size() > 4) {

                    def line = lines.substring(lines.indexOf('/*') + 2, lines.indexOf('*/'))
                    def function = (lines =~ /\w+\s*\(/)[0]

                    function = function.substring(0,function.length() - 1)
                    mapResult.put(function.trim(), line.trim())
                }
            }
        }

        mapResult
    }
}

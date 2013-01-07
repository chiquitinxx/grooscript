package org.grooscript.util

import org.grooscript.test.TestJs
import org.grooscript.GsConverter

/**
 * JFL 29/08/12
 */
class Util {

    //def static final FUNCTIONS_FILE = 'unused_functions.groovy'

    //Where Js stuff is
    def static getJsPath() {
        def s = System.getProperty('file.separator')
        def path = System.getProperty('user.dir')+"${s}src${s}main${s}resources${s}META-INF${s}resources${s}"
        def file = new File(path)
        if (!file || !file.exists() || !file.isDirectory()) {
            path = System.getProperty('user.dir')+"${s}web${s}scripts${s}"
        }
        file = new File(path)
        if (!file || !file.exists() || !file.isDirectory()) {
            path = System.getProperty('user.dir')+"${s}webapp${s}web${s}scripts${s}"
        }
        return path
    }

    //Location of groovy script examples
    def static getGroovyTestPath() {
        def s = System.getProperty('file.separator')
        return System.getProperty('user.dir')+"${s}src${s}test${s}resources${s}"
    }


    /*
    def static getNameFunctionsText() {
        def result

        File file = new File(getJsPath() + FUNCTIONS_FILE)
        if (file && file.exists() && file.isFile()) {
            result = file.text
        }
        result
    }
    */

    /**
     * Gets a Js file from js directory
     * @param name
     * @return
     */
    def static getJsFile(String name) {
        def result
        if (name) {
            def finalName = name
            if (!finalName.endsWith('.js')) {
                finalName += '.js'
            }

            //println 'User->'+System.getProperty('user.dir')
            //println 'JsPath->'+getJsPath()

            File file = new File(getJsPath() +finalName)
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
    def static getGroovyTestScriptFile(String name) {
        def result
        if (name) {
            def finalName = name
            if (!finalName.endsWith('.groovy')) {
                finalName += '.groovy'
            }

            File file = new File(getGroovyTestPath() +finalName)
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
     * @return map with exception,jsScript,assertFails,...
     */
    def static fullProcessScript(String script,File jsFile) {

        def result = [:]

        def jsScript = null
        try {

            def converter = new GsConverter()
            jsScript = converter.toJs(script)

            if (!jsFile) {
                result = TestJs.jsEval(jsScript)
            } else {
                result = TestJs.jsEvalWithFile(jsScript,jsFile)
            }

        } catch (e) {
            result.exception = e.message
        }

        result.jsScript = jsScript

        return result
    }

    /**
     * Full process a script
     * @param script
     * @return map with exception,jsScript,assertFails,...
     */
    def static fullProcessScript(String script) {
        return fullProcessScript(script,null)
    }

    /**
     * Get map with native functions
     * @param text to be converted
     * @return map [name:code]
     */
    def static getNativeFunctions(String text) {

        def mapResult = [:]

        //First lets remove comment lines (//)
        //text = text.replaceAll(/(?m)\/\/.*$/,'')

        def seg = text//text.replaceAll('\n','')

        //def pat = /@GsNative(\s)+.+\w+\s*\(.*\)\s*\{(\s)*\/\*(\s)*$LINES\*\/\s*\}/
        def pat = /(?ms)@(GsNative|org\.grooscript\.GsNative).+\w+\s*\(.*\)\s*\{\s*(\/\*).*(\*\/\s*\})/

        seg.eachMatch(pat) { match ->
            //println 'Item->'+match[0]
            def list = match[0].split('@GsNative')

            list.each { lines ->

                if (lines && lines.trim().size()>4) {

                    //println 'lines->'+lines

                    def line = lines.substring(lines.indexOf('/*')+2,lines.indexOf('*/'))
                    def function = (lines =~ /\w+\s*\(/)[0] /*.each { ma2 ->
                        println 'Miniitem->'+ma2
                    }*/
                    function = function.substring(0,function.length()-1)
                    mapResult.put(function.trim(),line.trim())
                }
            }
        }

        //println 'MapResult->'+mapResult

        return mapResult
    }
}

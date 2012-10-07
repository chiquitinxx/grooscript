package org.yila.gscript.util

import org.yila.gscript.test.TestJs
import org.yila.gscript.GsConverter

/**
 * JFL 29/08/12
 */
class Util {

    def static final FUNCTIONS_FILE = 'functions.groovy'

    //Where Js stuff is
    def static getJsPath() {
        def s = System.getProperty('file.separator')
        def path = System.getProperty('user.dir')+"${s}src${s}main${s}resources${s}js${s}"
        def file = new File(path)
        if (!file || !file.exists() || !file.isDirectory()) {
            path = System.getProperty('user.dir')+"${s}web${s}scripts${s}"
        }
        return path
    }

    //Location of groovy script examples
    def static getGroovyTestPath() {
        def s = System.getProperty('file.separator')
        return System.getProperty('user.dir')+"${s}src${s}test${s}resources${s}"
    }

    def static getNameFunctionsText() {
        def result

        File file = new File(getJsPath() + FUNCTIONS_FILE)
        if (file && file.exists() && file.isFile()) {
            result = file.text
        }
        result
    }

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

    def static fullProcessScript(String script) {

        def result = [:]

        def jsScript = null
        try {

            def converter = new GsConverter()
            jsScript = converter.toJs(script)

            result = TestJs.jsEval(jsScript)

        } catch (e) {
            result.exception = e.message
        }

        result.jsScript = jsScript

        return result
    }
}

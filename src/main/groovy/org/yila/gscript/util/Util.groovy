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
        return System.getProperty('user.dir')+"${s}src${s}main${s}resources${s}js${s}"
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

        try {

            def converter = new GsConverter()
            def jsScript = converter.toJs(script)

            result = TestJs.jsEval(jsScript)

            result.jsScript = jsScript
        } catch (e) {
            result.exception = e.message
        }

        return result
    }
}

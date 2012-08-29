package org.yila.gscript.util

/**
 * JFL 29/08/12
 */
class Util {

    def static final FUNCTIONS_FILE = 'functions.groovy'

    def static getJsPath() {
        def s = System.getProperty('file.separator')
        return System.getProperty('user.dir')+"${s}src${s}main${s}resources${s}js${s}"
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

            def s = System.getProperty('file.separator')
            //println '->'+System.getProperty('user.dir')+"${s}src${s}main${s}resources${s}js${s}${finalName}"
            File file = new File(getJsPath() +finalName)
            if (file && file.exists() && file.isFile()) {
                result = file
            }
        }
        result
    }
}

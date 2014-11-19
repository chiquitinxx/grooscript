package org.grooscript.convert

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class Out {

    def indent = 0
    static final TAB = '  '
    String resultScript = ''

    /**
     * Add a line to javascript output
     * @param script
     * @param line
     * @return
     */
    private addLine() {
        //println "sc(${script}) line(${line})"
        if (resultScript) {
            resultScript += '\n'
        } else {
            resultScript = ''
        }
        indent.times { resultScript += TAB }
    }

    /**
     * Add a text to javascript output
     * @param text
     * @return
     */
    private addScript(text, addNewLineChar = false) {
        resultScript += text
        if (addNewLineChar) {
            addLine()
        }
    }

    /**
     * Add text to javascript output at some position
     * @param text
     * @param position
     * @return
     */
    void addScriptAt(text,position) {
        resultScript = resultScript.substring(0,position) + text + resultScript.substring(position)
    }

    /**
     * Get actual position in javascript output
     * @return
     */
    def getSavePoint() {
        return resultScript.size()
    }

    /**
     * Remove a TAB from current javascript output
     * @return
     */
    private removeTabScript() {
        resultScript = resultScript[0..resultScript.size()-1-TAB.size()]
    }

    def block(String text = '', Closure cl) {
        addScript(text + '{')
        indent ++
        addLine()
        cl()
        addLine()
        indent --
        removeTabScript()
        addScript('};')
        addLine()
    }
}

package org.grooscript.convert

import static org.grooscript.util.Util.LINE_SEPARATOR as LS
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
    void addLine() {
        if (resultScript) {
            resultScript += LS
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
    private void addScript(text, addNewLineChar = false) {
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
    int getSavePoint() {
        return resultScript.size()
    }

    /**
     * Remove a TAB from current javascript output
     * @return
     */
    void removeTabScript() {
        resultScript = resultScript[0..resultScript.size()-1-TAB.size()]
    }

    void block(String text = '', Closure cl) {
        addScript(text + '{')
        indent ++
        addLine()
        cl()
        addLine()
        indent --
        removeTabScript()
        addScript('};', true)
    }
}

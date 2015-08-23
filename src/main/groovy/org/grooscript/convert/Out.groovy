/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.convert

import static org.grooscript.util.Util.LINE_SEPARATOR as LS

class Out {

    private static final TAB = '  '

    int indent = 0
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
     * Add a tab to out
     */
    void addTab() {
        resultScript += TAB
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

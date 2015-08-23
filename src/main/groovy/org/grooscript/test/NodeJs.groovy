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
package org.grooscript.test

import groovy.io.FileType
import org.grooscript.util.GsConsole
import org.grooscript.util.Util

import static org.grooscript.util.Util.LINE_SEPARATOR

class NodeJs {

    private static final String NODE_BINARY = Util.isWindows() ? 'node.exe' : 'node'
    private static final String FILE_NAME = 'nodeJsTest.js'
    private static final FILE_HEAD = "var gs = require('./src/main/resources/META-INF/resources/grooscript.js');" +
            "${LINE_SEPARATOR}gs.consoleOutput = false;${LINE_SEPARATOR}"
    private static final String SEPARATOR = '#'
    private static final FILE_FOOT = "${LINE_SEPARATOR}console.log(gs.fails + '${SEPARATOR}' + gs.consoleData);"
    // Wait time for external process  execution
    private static final long MAX_TIMEOUT = 3000L

    JsTestResult evaluate(String jsCode) {
        JsTestResult result = null

        new File(FILE_NAME).text = FILE_HEAD + jsCode + FILE_FOOT
        try {
            String command = Util.isWindows() ? "cmd /c ${findBinaryPath()}\\$NODE_BINARY" : NODE_BINARY
            def proc = "$command $FILE_NAME".execute()
            proc.waitForOrKill(MAX_TIMEOUT)
            String exit = proc.in.text
            if (!exit) {
                GsConsole.error 'Error evaluating in node.js.'
                GsConsole.error '  return code: ' + proc.exitValue() + ' stderr: ' + proc.err.text
                assert false, 'Error evaluating in Node.js'
            } else {
                result = evaluateOutput(exit)
            }
        } catch (e) {
            e.printStackTrace()
            GsConsole.exception 'Exception evaluating in node.js: ' + e.message
            assert false, 'Exception evaluating in Node.js.'
        } finally {
            new File(FILE_NAME).delete()
        }
        result
    }

    /**
     * Finds path to node.js binary's in embedded installation done by 'gradle-node-plugin'
     * @return path to node.js' binary
     * */
    private File findBinaryPath() {
        /**
         * NOTE: node.js' path is dependant on 'gradle-node-plugin' configuration
         * */
        boolean found = false
        File ret
        new File('build/nodejs').eachFileRecurse(FileType.FILES) {
            if (!found) {
                if (it.name == NODE_BINARY) {
                    ret = it
                    found = true
                }
            }
        }
        return ret.getParentFile()
    }

    private JsTestResult evaluateOutput(String text) {
        def list = text.split(SEPARATOR)
        JsTestResult result = new JsTestResult()
        result.assertFails = Boolean.parseBoolean(list[0])
        result.console = list[1]
        result
    }
}

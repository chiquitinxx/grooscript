package org.grooscript.test

import org.grooscript.util.GsConsole

import static org.grooscript.util.Util.LINE_JUMP

/**
 * Created by jorge on 15/04/14.
 */
class NodeJs {

    private static final FILE_NAME = 'nodeJsTest.js'
    private static final FILE_HEAD = "var gs = require('./src/main/resources/META-INF/resources/grooscript.js');" +
            "${LINE_JUMP}gs.consoleOutput = false;${LINE_JUMP}"
    private static final SEPARATOR = '#'
    private static final FILE_FOOT = "${LINE_JUMP}console.log(gs.fails + '${SEPARATOR}' + gs.consoleData);"
    private static final MAX_TIMEOUT = 3000L

    JsTestResult evaluate(String jsCode) {
        def result = null

        new File(FILE_NAME).text = FILE_HEAD + jsCode + FILE_FOOT
        try {
            def proc = "node ${FILE_NAME}".execute()
            proc.waitForOrKill(MAX_TIMEOUT)
            def exit = proc.in.text
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

    private evaluateOutput(String text) {
        def list = text.split(SEPARATOR)
        JsTestResult result = new JsTestResult()
        result.assertFails = Boolean.parseBoolean(list[0])
        result.console = list[1]
        result
    }
}

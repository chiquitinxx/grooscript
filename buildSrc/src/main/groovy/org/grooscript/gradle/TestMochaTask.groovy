package org.grooscript.gradle

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class TestMochaTask extends DefaultTask {

    String file

    private runTest() {
        def result = "mocha -R json ${file}".execute()
        def failures = 0
        def success = 0
        def messageError = ''
        def returnCode
        try {
            result.waitFor()
            returnCode = result.exitValue()

            (failures, success) = processJson(result, returnCode, messageError, failures, success)
        } catch (e) {
            failures = 1
            try {
                messageError += " Error executing mocha, return code: ${returnCode}\n"
                messageError += " stderr: ${result.err.text}\n"
                messageError += " stdout: ${result?.in?.text}\n"
            } catch (ex) {
            }
        }

        decideResult(failures, messageError, success)
    }

    private List processJson(Process result, int returnCode, messageError, int failures, int success) {
        def slurper = new JsonSlurper()
        def json = slurper.parseText(result.in.text)
        if (returnCode == 0) {
            success = json.stats.passes
        } else {
            failures = json.stats.failures
            json.failures.each {
                messageError += " Fail: ${it.fullTitle}.${it.title} \n"
            }
        }
        [failures, success]
    }

    private void decideResult(failures, messageError, success) {
        if (failures > 0) {
            throw new GradleException("Mocha tests failed: ${failures}\n${messageError}")
        } else {
            if (success == 0) {
                throw new GradleException('0 tests executed.')
            } else {
                println "${success} mocha tests success."
            }
        }
    }

    @TaskAction
    def testMocha() {
        if (!file) {
            throw new GradleException("Need define file parameter, test file to test with mocha.")
        } else {
            runTest()
        }
    }
}
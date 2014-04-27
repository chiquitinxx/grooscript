package org.grooscript.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

class UglifyJsTask extends DefaultTask {

    String source
    String destination

    private runUglify() {
        def result = "uglifyjs ${source} -c -o ${destination}".execute()
        def returnCode
        try {
            result.waitFor()
            returnCode = result.exitValue()

            if (returnCode != 0) {
                throw new GradleException("Error running uglify in source: ${source} to destination: ${destination}")
            } else  {
                println "Uglify done, ${source} to ${destination}"
            }
        } catch (e) {
            println " Error executing mocha, return code: ${returnCode}\n"
            println " stderr: ${result.err.text}\n"
            println " stdout: ${result?.in?.text}\n"
            throw new GradleException("Exception running uglify in source: ${source} to destination: ${destination}")
        }
    }

    @TaskAction
    def doUglify() {
        if (!source || !destination) {
            throw new GradleException("Need define source and destination parameter to run uglify.")
        } else {
            runUglify()
        }
    }
}
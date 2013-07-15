package org.grooscript.daemon

import org.grooscript.GrooScript
import org.grooscript.test.TestJs
import org.grooscript.util.GsConsole
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */
class TestDaemon extends Specification {

    static final TIME_DAEMON = 200
    static final FILE1_NAME = 'File1'
    static final FILE1_PATH = "daemon/${FILE1_NAME}"
    static final FILE1_OUT = "${FILE1_NAME}.js"
    static final FOLDER_OUT = 'testOut'

    def setup() {
        //Create temp output dir
        new File(FOLDER_OUT).mkdir()
    }

    def cleanup() {
        //Delete temp dir
        new File(FOLDER_OUT).deleteDir()
    }

    boolean existGeneratedFile(name) {
        def file = new File("${FOLDER_OUT}${System.getProperty('file.separator')}${name}")
        file && file.exists() && file.isFile()
    }

    def 'test start and stop'() {
        GroovySpy(GsConsole, global: true)

        given:
        GrooScript.startConversionDaemon([TestJs.getGroovyTestScript(FILE1_PATH).absolutePath],FOLDER_OUT)

        when:
        Thread.sleep(TIME_DAEMON)
        GrooScript.stopConversionDaemon()

        then:
        1 * GsConsole.message('Daemon Terminated.')
        0 * GsConsole.error(_)
        existGeneratedFile(FILE1_OUT)
    }
}

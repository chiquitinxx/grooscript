package org.grooscript.daemon

import groovy.transform.Immutable
import org.grooscript.GrooScript
import org.grooscript.util.GsConsole
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */
class TestDaemon extends Specification {

    static final TIME_DAEMON = 200
    static final FILE1_NAME = 'File1'
    static final FILE1_OUT = "${FILE1_NAME}.js"
    static final FOLDER_OUT = 'testOut'
    static final SEP = System.getProperty('file.separator')

    def setup() {
        //Create temp output dir
        new File(FOLDER_OUT).mkdir()
        new File(FOLDER_OUT+SEP+FILE1_NAME+'.groovy') << 'class File1 {}'
    }

    def cleanup() {
        //Delete temp dir
        new File(FOLDER_OUT).deleteDir()
    }

    File generatedFile(name) {
        def file = new File("${FOLDER_OUT}${SEP}${name}")
        file && file.exists() && file.isFile() ? file : null
    }

    def 'test start and stop'() {

        GroovySpy(GsConsole, global: true)

        given:
        GrooScript.startConversionDaemon([FOLDER_OUT] , FOLDER_OUT)

        when:
        waitAndStop()

        then:
        1 * GsConsole.message('Daemon Terminated.')
        0 * GsConsole.error(_)
        generatedFile(FILE1_OUT)
    }

    def 'test option customization applying an ast'() {
        given:
        GrooScript.startConversionDaemon([FOLDER_OUT] , FOLDER_OUT,
                ['customization': {
                    ast(Immutable)
                }])

        when:
        waitAndStop()

        then: 'DomainClass ast applied to class'
        generatedFile(FILE1_OUT).exists()
    }

    def 'test do after'() {
        given:
        def number = 5
        def doAfter = { list ->
            if (list) {
                println "List of converted files ${list}"
            }
            number ++
        }

        when:
        GrooScript.startConversionDaemon([FOLDER_OUT+SEP+FILE1_NAME+'.groovy'] , FOLDER_OUT,
                null, doAfter)
        Thread.sleep(TIME_DAEMON * 5)

        then:
        number > 5
    }

    private waitAndStop() {
        Thread.sleep(TIME_DAEMON)
        GrooScript.stopConversionDaemon()
    }
}

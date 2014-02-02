package org.grooscript.daemon

import org.grooscript.GrooScript
import org.grooscript.util.GsConsole
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */
class TestDaemon extends Specification {

    static final TIME_DAEMON = 200
    static final FILE1_NAME = 'File1'
    static final FILE2_NAME = 'File2'
    static final FILE1_OUT = "${FILE1_NAME}.js"
    static final FILE2_OUT = "${FILE2_NAME}.js"
    static final SOURCE_FOLDER = 'source'
    static final DESTINATION_FOLDER = 'destination'
    static final SEP = System.getProperty('file.separator')

    def setup() {
        new File(SOURCE_FOLDER).mkdir()
        new File(SOURCE_FOLDER + SEP + FILE1_NAME + '.groovy') << 'class File1 {}'
        new File(DESTINATION_FOLDER).mkdir()
    }

    def cleanup() {
        new File(SOURCE_FOLDER).deleteDir()
        new File(DESTINATION_FOLDER).deleteDir()
    }

    File generatedFile(name) {
        def file = new File("${DESTINATION_FOLDER}${SEP}${name}")
        file && file.exists() && file.isFile() ? file : null
    }

    @Unroll
    def 'test start and stop and convert with different sources'() {

        GroovySpy(GsConsole, global: true)

        given:
        GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER)

        when:
        waitAndStop()

        then:
        1 * GsConsole.message('Daemon Terminated.')
        0 * GsConsole.error(_)
        generatedFile(FILE1_OUT)

        where:
        source << [[SOURCE_FOLDER], SOURCE_FOLDER]
    }

    def 'test daemon with a conversion option'() {
        given:
        GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER,
                ["${GrooScript.INITIAL_TEXT_OPTION}": '//Init'])

        when:
        waitAndStop()

        then:
        generatedFile(FILE1_OUT).text.startsWith '//Init\nfunction File1() {'
        generatedFile(FILE1_OUT).text.contains "gSobject.clazz = { name: 'File1', simpleName: 'File1'};"
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
        GrooScript.startConversionDaemon([SOURCE_FOLDER+SEP+FILE1_NAME+'.groovy'] , DESTINATION_FOLDER,
                null, doAfter)
        waitAndStop(5)

        then:
        number > 5
    }

    def 'test without recursive option'() {
        given:
        createFolderInsideSourceWithSecondFile()

        when:
        GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER)
        waitAndStop()

        then:
        generatedFile(FILE1_OUT)
        !generatedFile(FILE2_OUT)
    }

    def 'test with recursive option'() {
        given:
        createFolderInsideSourceWithSecondFile()

        when:
        GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER, null, null, true)
        waitAndStop()

        then:
        generatedFile(FILE1_OUT)
        generatedFile(FILE2_OUT)
    }

    private waitAndStop(waitTimes = 1) {
        Thread.sleep(TIME_DAEMON * waitTimes)
        GrooScript.stopConversionDaemon()
    }

    private createFolderInsideSourceWithSecondFile() {
        new File(SOURCE_FOLDER + SEP + 'inside').mkdir()
        new File(SOURCE_FOLDER + SEP + 'inside'+ SEP + FILE2_NAME + '.groovy') << 'class File2 {}'
    }
}

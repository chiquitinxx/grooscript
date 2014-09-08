package org.grooscript.daemon

import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import org.grooscript.util.GsConsole
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */
class TestDaemon extends Specification {

    static final TIME_DAEMON = 600
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

    def 'actor is active after start daemon'() {
        given:
        def daemon = GrooScript.startConversionDaemon(SOURCE_FOLDER , DESTINATION_FOLDER)
        waitTime()

        expect:
        daemon.convertActor.isActive()

        cleanup:
        daemon.stop()
    }

    def 'on error stop the daemon'() {
        given:
        GroovySpy(GsConsole, global: true)
        def daemon = new ConversionDaemon(source: 'fails', destinationFolder: 'fail')

        when:
        daemon.start()
        waitTime()

        then:
        1 * GsConsole.exception('Exception in daemon: null')
        1 * GsConsole.exception('Daemon Error in file/folder fails')
    }

    @Unroll
    def 'test start and stop and convert with different sources'() {
        given:
        GroovySpy(GsConsole, global: true)
        def daemon = GrooScript.startConversionDaemon(source , DESTINATION_FOLDER)

        when:
        waitAndStop(daemon, 2)

        then:
        1 * GsConsole.message('Daemon Finished.')
        0 * GsConsole.error(_)
        0 * GsConsole.exception(_)
        generatedFile(FILE1_OUT)

        where:
        source << [[SOURCE_FOLDER], SOURCE_FOLDER]
    }

    def 'test daemon with a conversion option'() {
        given:
        def daemon = GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER,
                ["${ConversionOptions.INITIAL_TEXT.text}": '//Init'])

        when:
        waitAndStop(daemon, 2)

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
        def daemon = GrooScript.startConversionDaemon([SOURCE_FOLDER+SEP+FILE1_NAME+'.groovy'] , DESTINATION_FOLDER,
                null, doAfter)
        waitAndStop(daemon, 2)

        then:
        number > 5
    }

    def 'test without recursive option'() {
        given:
        createFolderInsideSourceWithSecondFile()

        when:
        def daemon = GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER)
        waitAndStop(daemon, 2)

        then:
        generatedFile(FILE1_OUT)
        !generatedFile(FILE2_OUT)
    }

    def 'test with recursive option'() {
        given:
        createFolderInsideSourceWithSecondFile()

        when:
        def daemon = GrooScript.startConversionDaemon([SOURCE_FOLDER] , DESTINATION_FOLDER, null, null, true)
        waitAndStop(daemon)

        then:
        generatedFile(FILE1_OUT)
        generatedFile(FILE2_OUT)
    }

    private waitTime(waitTimes = 1) {
        Thread.sleep(TIME_DAEMON * waitTimes)
    }

    private waitAndStop(ConversionDaemon daemon, waitTimes = 1) {
        waitTime(waitTimes)
        daemon.stop()
        waitTime()
    }

    private createFolderInsideSourceWithSecondFile() {
        new File(SOURCE_FOLDER + SEP + 'inside').mkdir()
        new File(SOURCE_FOLDER + SEP + 'inside'+ SEP + FILE2_NAME + '.groovy') << 'class File2 {}'
    }
}

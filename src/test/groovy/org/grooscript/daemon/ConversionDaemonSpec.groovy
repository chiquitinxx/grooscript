package org.grooscript.daemon

import org.grooscript.GrooScript
import org.grooscript.convert.ConversionOptions
import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 15/07/13
 */
class ConversionDaemonSpec extends Specification {

    def 'execution if no files changed'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, CONVERSION_OPTIONS_EMPTY, [])

        then:
        0 * _
    }

    def 'execution if one file changes and destination is a file'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, CONVERSION_OPTIONS_EMPTY, [FILE1])

        then:
        1 * GrooScript.clearAllOptions()
        1 * GrooScript.convert(SOURCE, DESTINATION_FILE)
        1 * GrooScript.convertFiles(_, _)
        0 * _
    }

    def 'execution with conversion options and destination is a file'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, CONVERSION_OPTIONS, [FILE1])

        then:
        1 * GrooScript.clearAllOptions()
        1 * GrooScript.convert(SOURCE, DESTINATION_FILE)
        1 * GrooScript.convertFiles(_, _)
        1 * GrooScript.getDefaultOptions()
        1 * GrooScript.setConversionProperty('classPath', 'classpath')
        0 * _
    }

    def 'execution if one file changes and destination is a folder'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FOLDER, CONVERSION_OPTIONS_EMPTY, [FILE1])

        then:
        1 * GrooScript.clearAllOptions()
        1 * GrooScript.convert([FILE1], DESTINATION_FOLDER)
        1 * GrooScript.convertFiles(_, _)
        0 * _
    }

    @Unroll
    def 'not call conversion if change a file that is not a groovy or java file'() {
        given:
        GroovySpy(GrooScript, global: true)

        when:
        ConversionDaemon.conversionClosure(SOURCE, DESTINATION_FILE, CONVERSION_OPTIONS, [file])

        then:
        0 * _

        where:
        file << ['file', 'file.js', 'file.html']
    }


    def 'converts files on start'() {
        given:
        createFiles()

        when:
        ConversionDaemon.start(SOURCE, DESTINATION_FOLDER, CONVERSION_OPTIONS_EMPTY)

        then:
        filesConverted()
        ConversionDaemon.numberConversions == old(ConversionDaemon.numberConversions) + 1

        cleanup:
        deleteFilesAndDestination()
    }

    private static final FILE1 = 'Class1.groovy'
    private static final FILE2 = 'Class2.groovy'
    private static final SOURCE = [FILE1, FILE2]
    private static final DESTINATION_FILE = 'file.js'
    private static final DESTINATION_FOLDER = 'folder'
    private static final CONVERSION_OPTIONS_EMPTY = [:]
    private static final CONVERSION_OPTIONS = [
            "${ConversionOptions.CLASSPATH.text}" : 'classpath'
    ]

    private createFiles() {
        new File(FILE1) << 'class Class1 {}'
        new File(FILE2) << 'class Class2 {}'
    }

    private boolean filesConverted() {
        new File("${DESTINATION_FOLDER}/Class1.js").exists() &&
        new File("${DESTINATION_FOLDER}/Class2.js").exists()
    }

    private deleteFilesAndDestination() {
        new File(DESTINATION_FOLDER).deleteDir()
        new File(FILE1).delete()
        new File(FILE2).delete()
    }
}

package org.grooscript

import org.grooscript.test.ConversionMixin
import org.grooscript.util.GrooScriptException
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
class TestFiles extends Specification {

    def 'initial inheritance on distinct files'() {
        when:
        def result = convertFile('files/Car')

        then:
        result
    }

    def 'check inheritance use in other files with convertDependencies'() {
        when:
        def result = convertAndEvaluate('files/Vehicles',true, [classPath: 'src/test/resources', consoleInfo: true])

        then:
        notThrown(GrooScriptException)
        result
    }
}

package org.grooscript

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 22/11/13
 */
@Mixin([ConversionMixin])
class TestInheritance extends Specification {

    def 'initial inheritance on distinct files'() {
        when:
        def result = convertFile('inheritance/Car', [classPath: 'src/test/resources'])

        then:
        result
    }

    def 'check inheritance use in other files with convertDependencies'() {
        expect:
        !readAndConvert('inheritance/Vehicles', true,
                [convertDependencies: true]).assertFails
    }
}

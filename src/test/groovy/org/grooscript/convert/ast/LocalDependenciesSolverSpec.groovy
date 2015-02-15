package org.grooscript.convert.ast

import org.grooscript.GrooScript
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 14/2/15.
 */
class LocalDependenciesSolverSpec extends Specification {

    void 'get local dependencies from basic script'() {
        given:
        def script = 'println "Hello!"'

        expect:
        localDependenciesSolver.fromText(script) == [] as Set
    }

    void 'java / groovy types not added as dependencies'() {
        given:
        def script = 'ArrayList list = new ArrayList()'

        expect:
        localDependenciesSolver.fromText(script) == [] as Set
    }

    void 'get local dependencies using class'() {
        given:
        def script = 'import files.Car; Car car'

        expect:
        localDependenciesSolver.fromText(script) == ['files.Car'] as Set
    }

    void 'get local dependencies from new instance'() {
        given:
        def script = 'import files.Car; def car = new Car()'

        expect:
        localDependenciesSolver.fromText(script) == ['files.Car'] as Set
    }

    void 'get local dependencies from extend class'() {
        given:
        def script = 'import files.Car; class SuperCar extends Car {}'
        convert script

        expect:
        localDependenciesSolver.fromText(script) == ['files.Car'] as Set
    }

    private localDependenciesSolver = new LocalDependenciesSolver(classPath: 'src/test/src')

    private convert(script) {
        GrooScript.setConversionProperty('classPath', 'src/test/src')
        GrooScript.convert(script)
    }
}

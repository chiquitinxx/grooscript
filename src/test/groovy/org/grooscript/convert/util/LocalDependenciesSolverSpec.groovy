package org.grooscript.convert.util

import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 14/2/15.
 */
class LocalDependenciesSolverSpec extends Specification {

    void 'get local dependencies from basic script'() {
        given:
        def script = 'println "Hello!"'

        expect:
        localDependenciesSolver.fromText(script) == [] as Set<String>
    }

    void 'java / groovy types not added as dependencies'() {
        given:
        def script = 'ArrayList list = new ArrayList()'

        expect:
        localDependenciesSolver.fromText(script) == [] as Set<String>
    }

    void 'not getting dependencies from an import'() {
        given:
        def script = 'import files.Car; println "Hello!"'

        expect:
        localDependenciesSolver.fromText(script) == [] as Set<String>
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

        expect:
        localDependenciesSolver.fromText(script) == ['files.Car'] as Set
    }

    void 'get local dependencies from trait'() {
        given:
        def script = 'import files.MyTrait; class MyCar implements MyTrait {}'

        expect:
        localDependenciesSolver.fromText(script) == ['files.MyTrait'] as Set
    }

    private localDependenciesSolver = new LocalDependenciesSolver(classPath: 'src/test/src')
}

package org.grooscript.convert.ast

import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 14/2/15.
 */
class LocalDependenciesSolverSpec extends Specification {

    void 'get local dependencies from basic script'() {
        given:
        def script = 'println "Hello!"'

        expect:
        localDependenciesSolver.fromText(script) == []
    }

    private localDependenciesSolver = new LocalDependenciesSolver(classPath: 'src/test/src')
}

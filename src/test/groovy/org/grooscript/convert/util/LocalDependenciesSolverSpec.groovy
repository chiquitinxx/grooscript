/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.convert.util

import spock.lang.Specification

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

    private localDependenciesSolver = new LocalDependenciesSolver(classpath: 'src/test/src')
}

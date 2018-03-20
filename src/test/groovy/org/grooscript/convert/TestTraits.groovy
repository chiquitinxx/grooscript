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
package org.grooscript.convert

import org.grooscript.test.ConversionMixin

class TestTraits extends GroovyTestCase implements ConversionMixin {

    def 'initial traits support'() {
        assert convertAndEvaluate('traits/Starting')
    }

    def 'abstract methods'() {
        assert convertAndEvaluate('traits/AbstractMethods')
    }

    def 'private methods'() {
        assert convertAndEvaluate('traits/PrivateMethods')
    }

    def 'inheritance and interfaces'() {
        assert convertAndEvaluate('traits/Inheritance')
    }

    def 'properties'() {
        assert convertAndEvaluate('traits/Properties')
    }

    def 'private fields'() {
        assert convertAndEvaluate('traits/PrivateFields')
    }

    def 'composition and order'() {
        assert convertAndEvaluate('traits/Composition')
    }

    def 'extending'() {
        assert convertAndEvaluate('traits/Extending')
    }

    def 'dynamic'() {
        assert convertAndEvaluate('traits/Dynamic')
    }

    def 'with traits'() {
        assert convertAndEvaluate('traits/WithTraits')
    }

    def 'as runtime'() {
        assert convertAndEvaluate('traits/Runtime')
    }

    def 'use GsNative in traits'() {
        assert convertAndEvaluate('traits/Native')
    }

    def 'initialization of trait fields'() {
        assert convertAndEvaluate('traits/Initialization')
    }

    def 'method calling'() {
        assert convertAndEvaluate('traits/MethodCalling')
    }

    def 'static properties'() {
        assert convertAndEvaluate('traits/Static')
    }

    def 'confusing method call'() {
        assert convertAndEvaluate('traits/ConfusingMethod')
    }

    def 'get object properties'() {
        assert convertAndEvaluate('traits/ObjectProperties')
    }

    def 'using this in trait functions'() {
        assert convertAndEvaluate('traits/UsingThis')
    }
}

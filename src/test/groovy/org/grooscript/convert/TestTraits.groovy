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
import org.grooscript.util.Util
import spock.lang.IgnoreIf
import spock.lang.Specification

@Mixin([ConversionMixin])
@IgnoreIf({ !Util.groovyVersionAtLeast('2.3') })
class TestTraits extends Specification {

    def 'initial traits support'() {
        expect:
        convertAndEvaluate('traits/Starting')
    }

    def 'abstract methods'() {
        expect:
        convertAndEvaluate('traits/AbstractMethods')
    }

    def 'private methods'() {
        expect:
        convertAndEvaluate('traits/PrivateMethods')
    }

    def 'inheritance and interfaces'() {
        expect:
        convertAndEvaluate('traits/Inheritance')
    }

    def 'properties'() {
        expect:
        convertAndEvaluate('traits/Properties')
    }

    def 'private fields'() {
        expect:
        convertAndEvaluate('traits/PrivateFields')
    }

    def 'composition and order'() {
        expect:
        convertAndEvaluate('traits/Composition')
    }

    def 'extending'() {
        expect:
        convertAndEvaluate('traits/Extending')
    }

    def 'dynamic'() {
        expect:
        convertAndEvaluate('traits/Dynamic')
    }

    def 'with traits'() {
        expect:
        convertAndEvaluate('traits/WithTraits')
    }

    def 'as runtime'() {
        expect:
        convertAndEvaluate('traits/Runtime')
    }

    def 'use GsNative in traits'() {
        expect:
        convertAndEvaluate('traits/Native')
    }

    def 'initialization of trait fields'() {
        expect:
        convertAndEvaluate('traits/Initialization')
    }

    def 'method calling'() {
        expect:
        convertAndEvaluate('traits/MethodCalling')
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.4') })
    def 'static properties'() {
        expect:
        convertAndEvaluate('traits/Static')
    }

    def 'confusing method call'() {
        expect:
        convertAndEvaluate('traits/ConfusingMethod')
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.4') })
    def 'get object properties'() {
        expect:
        convertAndEvaluate('traits/ObjectProperties')
    }

    def 'using this in trait functions'() {
        expect:
        convertAndEvaluate('traits/UsingThis')
    }
}

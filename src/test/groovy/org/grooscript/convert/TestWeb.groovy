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
import spock.lang.Specification

@Mixin([ConversionMixin])
class TestWeb extends Specification {

    def 'test prefix and postfix undefined'() {
        expect:
        !convertAndEvaluateWithJsEngine('web/PrePostFix', false, [:], 'a = 0;', 'var a=0,b=0;func();gs.assert(a==1);' +
                'gs.assert(b==-1);gs.assert(c==1);gs.assert(d==-1);').assertFails
    }

    def 'test calling a function out of the script'() {
        expect:
        !convertAndEvaluateWithJsEngine('web/HideFunction', false, [:], 'var add', 'function bValue() {' +
                ' return 4;}; var add').assertFails
    }

    def 'test traits'() {
        expect:
        convertAndEvaluate('web/Traits')
    }
}

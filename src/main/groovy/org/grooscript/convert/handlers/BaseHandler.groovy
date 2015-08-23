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
package org.grooscript.convert.handlers

import org.grooscript.convert.Context
import org.grooscript.convert.ConversionFactory
import org.grooscript.convert.Functions
import org.grooscript.convert.Out
import org.grooscript.convert.Traits

import static org.grooscript.JsNames.*

class BaseHandler {

    static final List<Map> SPECIAL_STATIC_METHOD_CALLS = [
            [type: 'org.grooscript.GrooScript', method: 'toJavascript', function: GS_TO_JAVASCRIPT],
            [type: 'org.grooscript.GrooScript', method: 'toGroovy', function: GS_TO_GROOVY],
            [type: 'org.grooscript.GrooScript', method: 'toJsObj', function: GS_TO_JS_OBJECT],
            [type: 'java.lang.Integer', method: 'parseInt', function: 'parseInt'],
            [type: 'java.lang.Float', method: 'parseFloat', function: 'parseFloat'],
    ]

    Context context
    Out out
    ConversionFactory conversionFactory
    Functions functions
    Traits traits
}

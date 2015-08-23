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

import org.codehaus.groovy.ast.MethodNode

import static org.grooscript.JsNames.*

class MethodNodeHandler extends BaseHandler {

    void handle(MethodNode method, boolean isConstructor = false) {
        def name =  method.name
        //Constructor method
        if (isConstructor) {
            //Add number of params to constructor name
            //BEWARE Atm only accepts constructor with different number or arguments
            name = context.classNameStack.peek() + (method.parameters ? method.parameters.size() : '0')
        }

        functions.processBasicFunction("${GS_OBJECT}['$name']", method, isConstructor)
    }
}

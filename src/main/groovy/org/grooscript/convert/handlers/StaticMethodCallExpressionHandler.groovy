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

import org.codehaus.groovy.ast.expr.*

class StaticMethodCallExpressionHandler extends BaseHandler {

    void handle(StaticMethodCallExpression expression) {
        def ownerType = expression.ownerType.name
        def specialStatic = SPECIAL_STATIC_METHOD_CALLS.find {
            it.type == ownerType && it.method == expression.method
        }
        if (ownerType == 'org.grooscript.GrooScript' && expression.method == 'nativeJs') {
            conversionFactory.outFirstArgument(expression)
        } else {
            if (specialStatic) {
                out.addScript(specialStatic.function)
            } else {
                out.addScript("${conversionFactory.reduceClassName(ownerType)}.${expression.method}")
            }
            conversionFactory.visitNode(expression.arguments)
        }
    }
}

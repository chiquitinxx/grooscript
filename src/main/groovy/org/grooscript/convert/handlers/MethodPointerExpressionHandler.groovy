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

import org.codehaus.groovy.ast.expr.MethodPointerExpression

import static org.grooscript.JsNames.*

class MethodPointerExpressionHandler extends BaseHandler {

    void handle(MethodPointerExpression expression) {
        if (conversionFactory.isThis(expression.expression) &&
                context.currentVariableScopingHasMethod(expression.methodName.text)) {
            out.addScript(GS_OBJECT)
        } else {
            conversionFactory.visitNode(expression.expression)
        }
        out.addScript('[')
        conversionFactory.visitNode(expression.methodName)
        out.addScript(']')
    }
}

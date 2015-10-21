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

import org.codehaus.groovy.ast.stmt.ContinueStatement

class ContinueStatementHandler extends BaseHandler {

    void handle(ContinueStatement continueStatement, boolean noNeedNewLine = true) {
        out.addScript('continue')
        if (!noNeedNewLine) {
            out.addLine()
        }
    }
}

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

import org.codehaus.groovy.control.CompilationUnit
import org.grooscript.convert.ast.CodeVisitor
import org.grooscript.convert.ast.GrooScriptCompiler

class LocalDependenciesSolver extends GrooScriptCompiler {

    private Map<String, Set<String>> cache = [:]

    /**
     * Get list of local dependencies
     * @param sourceCode
     * @return
     */
    Set<String> fromText(String sourceCode) {

        Set<String> allLocalDependencies = cache[sourceCode]
        if (!allLocalDependencies) {
            CompilationUnit cu = compiledCode(sourceCode)

            allLocalDependencies = [] as Set
            CodeVisitor codeVisitor = new CodeVisitor(allLocalDependencies, cu.classLoader)
            cu.ast.modules.each { module ->
                module.statementBlock.visit(codeVisitor)
                module.classes.each { classNode ->
                    codeVisitor.checkTraits(classNode)
                    codeVisitor.check(classNode.superClass)
                    classNode.visitContents(codeVisitor)
                }
            }
            cache[sourceCode] = allLocalDependencies
        }

        allLocalDependencies.clone()
    }
}

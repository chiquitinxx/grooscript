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
package org.grooscript.convert.ast

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.SourceUnit
import org.grooscript.convert.Traits

class CodeVisitor extends ClassCodeVisitorSupport {

    Set dependencies
    GroovyClassLoader groovyClassLoader
    Traits traits = new Traits()

    CodeVisitor(listDependencies, GroovyClassLoader classLoader) {
        dependencies = listDependencies
        groovyClassLoader = classLoader
    }

    public check(ClassNode classNode) {
        if (classNode.name != 'java.lang.Object') {
            if (isLocalFileType(classNode)) {
                dependencies << classNode.name
            }
        }
    }

    public checkTraits(ClassNode classNode) {
        classNode?.interfaces.findAll {
            traits.isTrait(it)
        }.each {
            check(it)
        }
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression call) {
        super.visitConstructorCallExpression(call)
        check call.type
    }


    @Override
    public void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression)
        check expression.type
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }

    private isLocalFileType(ClassNode type) {
        groovyClassLoader.resourceLoader.loadGroovySource(type.name) != null
    }
}

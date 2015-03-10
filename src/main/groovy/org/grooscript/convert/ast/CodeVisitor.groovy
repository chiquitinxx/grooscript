package org.grooscript.convert.ast

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.SourceUnit
import org.grooscript.convert.Traits

/**
 * Created by jorgefrancoleza on 14/2/15.
 */
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

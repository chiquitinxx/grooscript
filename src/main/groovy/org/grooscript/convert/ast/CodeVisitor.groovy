package org.grooscript.convert.ast

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.control.SourceUnit

/**
 * Created by jorgefrancoleza on 14/2/15.
 */
class CodeVisitor extends ClassCodeVisitorSupport {

    Set dependencies
    GroovyClassLoader groovyClassLoader

    CodeVisitor(listDependencies, GroovyClassLoader classLoader) {
        dependencies = listDependencies
        groovyClassLoader = classLoader
    }

    public check(ClassNode type) {
        if (type.name != 'java.lang.Object') {
            if (isLocalFileType(type)) {
                dependencies << type.name
            }
        }
    }

    private isLocalFileType(ClassNode type) {
        groovyClassLoader.resourceLoader.loadGroovySource(type.name) != null
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
}

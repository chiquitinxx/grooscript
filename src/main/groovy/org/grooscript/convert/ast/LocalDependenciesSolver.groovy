package org.grooscript.convert.ast

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilationUnit

/**
 * User: jorgefrancoleza
 * Date: 14/2/15
 */
class LocalDependenciesSolver extends GrooscriptCompiler {

    /**
     * Get list of local dependencies
     * @param sourceCode
     * @return
     */
    Set<String> fromText(String sourceCode) {

        CompilationUnit cu = compiledCode(sourceCode)

        Set allLocalDependencies = [] as Set
        CodeVisitor codeVisitor = new CodeVisitor(allLocalDependencies, cu.classLoader)
        cu.ast.modules.each { module ->
            module.statementBlock.visit(codeVisitor)
            module.classes.each { classNode ->
                codeVisitor.check(classNode.superClass)
                classNode.visitContents(codeVisitor)
            }
        }

        //cu.ast.visitContents(new CodeVisitor())
        //println '--->'+classLoader.resourceLoader.loadGroovySource('files.Vehicle')
        //println '--->'+classLoader.resourceLoader.loadGroovySource('java.util.ArrayList')

        allLocalDependencies
    }
}

package org.grooscript.convert.util

import org.codehaus.groovy.control.CompilationUnit
import org.grooscript.convert.ast.CodeVisitor
import org.grooscript.convert.ast.GrooScriptCompiler

/**
 * User: jorgefrancoleza
 * Date: 14/2/15
 */
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

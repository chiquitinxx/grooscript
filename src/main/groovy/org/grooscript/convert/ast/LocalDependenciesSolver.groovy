package org.grooscript.convert.ast

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
    List<String> fromText(String sourceCode) {

        CompilationUnit cu = compiledCode(sourceCode)

        cu.ast.modules.each { module ->
            module.visit(new CodeVisitor())
        }

        //cu.ast.visitContents(new CodeVisitor())
        //println '--->'+classLoader.resourceLoader.loadGroovySource('files.Vehicle')
        //println '--->'+classLoader.resourceLoader.loadGroovySource('java.util.ArrayList')

        []
    }
}

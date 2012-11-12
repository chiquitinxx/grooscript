package org.yila.gscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * JFL 10/11/12
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class Native implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Nothing to do
    }
}

package org.yila.gscript.asts

import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.SourceUnit

/**
 * JFL 10/11/12
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class NotConvert implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Nothing to do
    }
}

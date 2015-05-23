package org.grooscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * JFL 23/05/15
 */
@SuppressWarnings(['EmptyMethod', 'UnusedMethodParameter'])
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class RequireJsModuleImpl implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        AnnotationNode annotationNode = (AnnotationNode) nodes[0]

        if (!annotationNode.getMember('path')) {
            sourceUnit.addError(new SyntaxException('Have to define path parameter',
                    annotationNode.lineNumber, annotationNode.columnNumber))
        }
    }
}

package org.grooscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.lang.reflect.Modifier

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class GQueryImpl implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof ClassNode) {
            return
        }

        ClassNode classNode = (ClassNode) nodes[1]
        ClassNode jQueryImpl = new ClassNode(org.grooscript.jquery.GQueryImpl)
        classNode.addProperty('gQuery', Modifier.PUBLIC , jQueryImpl,
                new ConstructorCallExpression(jQueryImpl, ArgumentListExpression.EMPTY_ARGUMENTS), null, null)
    }
}
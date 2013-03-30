package org.grooscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.GsConverter

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class PhantomJsTestImpl implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Start
        if (!nodes[0] instanceof AnnotationNode ||
            !nodes[1] instanceof MethodNode) {
            return
        }

        AnnotationNode node = (AnnotationNode) nodes[0]
        String url = node.getMember('url').text
        println 'url->'+url

        MethodNode method = (MethodNode) nodes[1]

        def Statement testCode = method.getCode()

        println 'testCode->'+testCode

        def GsConverter converter = new GsConverter()

        def jsTest
        try {
            jsTest = converter.processAstListToJs([testCode])
        } catch (e) {
            println 'Error converting ->'+e.message
        }

        println 'js code->'+jsTest

        method.setCode new AstBuilder().buildFromCode {
            println '1'
            assert  true,'true'
        }
    }
}
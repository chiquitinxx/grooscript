package org.grooscript.convert.ast

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 9/2/15.
 */
class AstTreeGeneratorSpec extends Specification {

    void 'get ast from basic script'() {
        given:
        def script = 'println "Hola!"'

        when:
        def (astNodes, nativeFunctions) = astTreeGenerator.fromText(script)

        then:
        astNodes.size() == 1
        astNodes.first() instanceof BlockStatement
        nativeFunctions == []
    }

    void 'get ast from a class'() {
        given:
        def script = 'class A {}'

        when:
        def (block, classNode) = astTreeGenerator.fromText(script)[0]

        then:
        classNode instanceof ClassNode && classNode.name == 'A'
        block.empty
    }

    void 'get ast from a script using a class in the classpath'() {
        given:
        def script = 'import files.Vehicle; def vehicle = new Vehicle()'

        when:
        def result = astTreeGenerator.fromText(script)

        then:
        result.first().first() instanceof BlockStatement
        result[1] == []
    }

    private astTreeGenerator = new AstTreeGenerator(classPath: 'src/test/src')
}

package org.grooscript.convert.ast

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import spock.lang.Specification
import spock.lang.Unroll

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

    @Unroll
    void 'get class node names from script'() {
        expect:
        nodeNames == astTreeGenerator.classNodeNamesFromText(script)

        where:
        nodeNames    | script
        []           | 'println "1"'
        ['A']        | 'class A {}'
        ['A']        | 'package aaa; class A {}'
        ['A']        | 'interface Int {}; class A implements Int {}'
        ['A', 'B']   | 'class A{}; class B extends A{}'
        ['Fly', 'A'] | 'trait Fly {}; class A implements Fly{}'
    }

    private astTreeGenerator = new AstTreeGenerator(classpath: 'src/test/src')
}

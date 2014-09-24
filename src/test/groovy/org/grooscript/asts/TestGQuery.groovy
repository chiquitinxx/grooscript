package org.grooscript.asts

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 25/09/14
 */
class TestGQuery extends GroovyTestCase {

    void testAstAddProperty() {
        assertScript '''
            import org.grooscript.asts.GQuery
            import org.grooscript.jquery.GQueryImpl

            @GQuery
            class A {}

            def a = new A()
            assert a.gQuery
            assert a.gQuery instanceof GQueryImpl
'''
    }

    void testAstConvertedToJavascript() {
        def result = GrooScript.convert('''
import org.grooscript.asts.GQuery

@GQuery
class A {}
''')
        assert result.contains('gSobject.gQuery = GQueryImpl();')
    }
}

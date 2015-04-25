package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.grooscript.convert.ConversionFactory
import org.grooscript.convert.Out
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by jorgefrancoleza on 2/3/15.
 */
class MethodCallExpressionHandlerSpec extends Specification {

    @Unroll
    void 'rehydrate and dehydrate are ignored and just process the closure'() {
        given:
        def closureExpression = new ClosureExpression(null, null)
        def expression = new MethodCallExpression(closureExpression, methodName, null)

        when:
        handler.handle(expression)

        then:
        1 * conversionFactory.visitNode(closureExpression)
        0 * _

        where:
        methodName << ['rehydrate', 'dehydrate']
    }

    private out = Mock(Out)
    private conversionFactory = Mock(ConversionFactory)
    private handler = new MethodCallExpressionHandler(out: out, conversionFactory: conversionFactory)
}

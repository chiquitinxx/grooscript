package org.grooscript.packages

import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 2/11/14
 */
@Mixin([ConversionMixin])
class GoogleSpec extends Specification {
    def 'try google'() {
        expect:
        convertAndEvaluate('packages/google', true)
    }
}

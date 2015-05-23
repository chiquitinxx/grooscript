package org.grooscript.convert

import org.grooscript.convert.util.RequireJsDependency
import org.grooscript.test.ConversionMixin
import spock.lang.Specification

import static org.grooscript.util.Util.LINE_SEPARATOR

/**
 * User: jorgefrancoleza
 * Date: 23/05/15
 */
@Mixin([ConversionMixin])
class GsConverterSpec extends Specification {

    void 'test basic conversion'() {
        expect:
        converter.toJs(BASIC_CLASS).startsWith '''function A() {'''
    }

    void 'convert as a require.js module add spaces'() {
        expect:
        converter.toJs(BASIC_CLASS, conversionOptionsWithRequireJs()).startsWith("  ${LINE_SEPARATOR}  function A() {")
    }

    void 'initialize require.js modules (ast) converting as require.js module'() {
        expect:
        converter.toJs(CLASS_WITH_REQUIRE_MODULE, conversionOptionsWithRequireJs()).contains('gSobject.module = module;')
        converter.requireJsDependencies == [new RequireJsDependency(path: 'any/path', name: 'module')]
    }

    void 'ignore require.js modules (ast) converting normal'() {
        expect:
        converter.toJs(CLASS_WITH_REQUIRE_MODULE).contains('gSobject.module = null;')
        converter.requireJsDependencies == []
    }

    private static final BASIC_CLASS = 'class A {}'
    private static final CLASS_WITH_REQUIRE_MODULE = '''class A {
    @org.grooscript.asts.RequireJsModule(path = "any/path")
    def module
}'''

    void 'add requireJs dependencies'() {
        expect:
        converter.requireJsDependencies == []

        when:
        converter.addRequireJsDependency(PATH, NAME)

        then:
        converter.requireJsDependencies == [new RequireJsDependency(path: PATH, name: NAME)]
    }

    private GsConverter converter = new GsConverter()

    private static final PATH = 'path'
    private static final NAME = 'name'

    private Map conversionOptionsWithRequireJs() {
        Map conversionOptions = [:]
        conversionOptions[ConversionOptions.REQUIRE_JS_MODULE.text] = true
        conversionOptions
    }
}

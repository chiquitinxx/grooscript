package org.grooscript.convert

import org.grooscript.convert.util.RequireJsDependency
import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 23/05/15
 */
@Mixin([ConversionMixin])
class GsConverterSpec extends Specification {

    void 'test basic conversion'() {
        expect:
        converter.toJs(BASIC_CLASS).startsWith '''function A() {
  var gSobject = gs.inherit(gs.baseClass,'A');
  gSobject.clazz = { name: 'A', simpleName: 'A'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};'''
    }

    void 'convert as a require.js module add spaces'() {
        expect:
        converter.toJs(BASIC_CLASS, conversionOptionsWithRequireJs()).startsWith('  ' + '''
  function A() {
    var gSobject = gs.inherit(gs.baseClass,'A');
    gSobject.clazz = { name: 'A', simpleName: 'A'};
    gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
    if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};''')
    }

    void 'initialize require.js modules (ast) converting as require.js module'() {
        expect:
        converter.toJs(CLASS_WITH_REQUIRE_MODULE, conversionOptionsWithRequireJs()).startsWith('  ' + '''
  function A() {
    var gSobject = gs.inherit(gs.baseClass,'A');
    gSobject.clazz = { name: 'A', simpleName: 'A'};
    gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
    gSobject.module = module;
    if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};''')
        converter.requireJsDependencies == [new RequireJsDependency(path: 'any/path', name: 'module')]
    }

    void 'ignore require.js modules (ast) converting normal'() {
        expect:
        converter.toJs(CLASS_WITH_REQUIRE_MODULE).startsWith('''function A() {
  var gSobject = gs.inherit(gs.baseClass,'A');
  gSobject.clazz = { name: 'A', simpleName: 'A'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.module = null;
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};''')
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

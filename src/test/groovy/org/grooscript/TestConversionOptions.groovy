package org.grooscript

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 14/02/13
 */
class TestConversionOptions extends Specification {

    def static final CONVERT_DEPENDENCIES_OPTION = 'convertDependencies'
    def static final CUSTOMIZATION_OPTION = 'customization'
    def static final FILE_BASIC_NAME = 'BasicClass'
    def static final FILE_BASIC_GROOVY_SOURCE = "src/test/resources/classes/${FILE_BASIC_NAME}.groovy"
    def static final FILE_BASIC_JS_FOLDER = "need"
    def static final FILE_BASIC_JS = "${FILE_BASIC_JS_FOLDER}/${FILE_BASIC_NAME}.js"

    //Reset options of GrooScript
    def setup() {
        GrooScript.setOwnClassPath(null)
        GrooScript.setConversionProperty(CONVERT_DEPENDENCIES_OPTION, true)
        GrooScript.setConversionProperty(CUSTOMIZATION_OPTION, null)
    }

    def cleanup() {
        def file = new File(FILE_BASIC_JS)
        if (file && file.exists()) {
            file.delete()
        }
        GrooScript.clearAllOptions()
        println GrooScript.options
    }

    def cleanupSpec() {

    }

    def 'check dependency resolution'() {

        when:
        GrooScript.setOwnClassPath('need')
        def String result = GrooScript.convert("class A {};def need = new Need()")

        then:
        result
        result.startsWith('function A()')
        result.endsWith('var need = Need();\n')
    }

    def 'check dependency resolution alone'() {

        when:
        GrooScript.setOwnClassPath('need')
        String result = GrooScript.convert("class B { def Need c}")

        then:
        result
        result.startsWith('function B()')
        result.contains('gSobject.c = null;\n')
    }

    def 'testing dependency conversions'() {

        when: 'we dont want dependencies classes in the javascript result, in this case Need class'
        GrooScript.setOwnClassPath('need')
        GrooScript.setConversionProperty(CONVERT_DEPENDENCIES_OPTION,false)
        String result = GrooScript.convert("class B { def Need c}")

        then: 'Need class not converted'
        result
        !result.contains('function Need()')
    }

    def 'can set classpath as List'() {
        when: 'we set classpath as list'
        GrooScript.setOwnClassPath(['need'])
        String result = GrooScript.convert("class B { def Need c}")

        then: 'not fails and Need converted'
        result
        result.contains('function Need()')
    }

    def 'with conversion option convertDependencies=false on a file without dependencies'() {
        when: 'no dependencies to convert in a file in a package'
        GrooScript.setConversionProperty(CONVERT_DEPENDENCIES_OPTION,false)
        GrooScript.convert(FILE_BASIC_GROOVY_SOURCE,FILE_BASIC_JS_FOLDER)
        def file = new File(FILE_BASIC_JS)

        then: 'Conversion returns data converted'
        file.text.startsWith("function ${FILE_BASIC_NAME}()")
    }

    def 'customization option'() {
        given:
        def customization = {
            ast(groovy.transform.TypeChecked)
        }

        when:
        GrooScript.setConversionProperty(CUSTOMIZATION_OPTION, customization)
        GrooScript.convert('class A {  def say() { println aaaa }}')

        then:
        thrown(Exception)
    }
}

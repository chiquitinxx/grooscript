package org.grooscript

import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 14/02/13
 */
class TestConversionOptions extends Specification {

    private static final CONVERT_DEPENDENCIES_OPTION = 'convertDependencies'
    private static final CUSTOMIZATION_OPTION = 'customization'
    private static final FILE_BASIC_NAME = 'BasicClass'
    private static final FILE_BASIC_GROOVY_SOURCE = "src/test/resources/classes/${FILE_BASIC_NAME}.groovy"
    private static final FOLDER_NEED_DEPENDENCY = "need"
    private static final CLASS_NEED_DEPENDENCY = "Need"
    private static final SEP = System.getProperty('file.separator')
    private static final FILE_BASIC_JS = "${FOLDER_NEED_DEPENDENCY}${SEP}${FILE_BASIC_NAME}.js"
    private static final SOURCE_DIR = 'source'
    private static final DESTINATION_FILE = 'destination.js'

    def cleanup() {
        new File(FOLDER_NEED_DEPENDENCY).deleteDir()
        new File(SOURCE_DIR).deleteDir()
        new File(DESTINATION_FILE).delete()
        GrooScript.clearAllOptions()
    }

    def 'check dependency resolution'() {

        given:
        setupNeedDirectory()
        GrooScript.setOwnClassPath(FOLDER_NEED_DEPENDENCY)

        when: 'convert a class with need dependency'
        String result = GrooScript.convert("class A {};def need = new Need()")

        then:
        result.startsWith('function A()')
        result.endsWith("var need = ${CLASS_NEED_DEPENDENCY}();\n")

        and: 'class need also converted'
        result.contains("function ${CLASS_NEED_DEPENDENCY}()")
    }

    def 'testing convert dependencies option'() {

        given:
        setupNeedDirectory()
        GrooScript.setOwnClassPath(FOLDER_NEED_DEPENDENCY)

        when: 'we dont want dependencies classes in the javascript result, in this case Need class'
        GrooScript.setOwnClassPath(FOLDER_NEED_DEPENDENCY)
        GrooScript.setConversionProperty(CONVERT_DEPENDENCIES_OPTION,false)
        String result = GrooScript.convert("class B { ${CLASS_NEED_DEPENDENCY} c}")

        then: 'Need class not converted'
        result.startsWith('function B()')
        !result.contains('function Need()')
    }

    def 'can set classpath as List'() {

        given:
        setupNeedDirectory()

        when: 'we set classpath as list'
        GrooScript.setOwnClassPath([FOLDER_NEED_DEPENDENCY])
        String result = GrooScript.convert("class B { def Need c}")

        then: 'conversion done'
        result
        result.contains('function Need()')
    }

    def 'with conversion option convertDependencies=false on a file without dependencies'() {

        given:
        setupNeedDirectory()

        when: 'no dependencies to convert in a file in a package'
        GrooScript.setConversionProperty(CONVERT_DEPENDENCIES_OPTION,false)
        GrooScript.convert(FILE_BASIC_GROOVY_SOURCE,FOLDER_NEED_DEPENDENCY)
        def file = new File(FILE_BASIC_JS)

        then: 'Conversion returns data converted'
        file.text.startsWith("function ${FILE_BASIC_NAME}()")
    }

    def 'customization option with an ast transformation'() {

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

    def 'test join js files in one file'() {

        given:
        setupFilesWithNumbers(SOURCE_DIR, 5)

        when:
        GrooScript.joinFiles(SOURCE_DIR, DESTINATION_FILE)
        File file = new File(DESTINATION_FILE)

        then:
        file.text == '0\n1\n2\n3\n4\n'
    }

    private setupNeedDirectory() {
        new File(FOLDER_NEED_DEPENDENCY).mkdir()
        new File(FOLDER_NEED_DEPENDENCY+SEP+CLASS_NEED_DEPENDENCY+'.groovy') << "class ${CLASS_NEED_DEPENDENCY} {}"
    }

    private setupFilesWithNumbers(name, number) {
        new File(name).mkdir()
        number.times {
            new File(name+SEP+it+'.js') << it as String
        }
    }
}

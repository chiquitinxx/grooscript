package org.grooscript

import spock.lang.Specification
import spock.lang.Unroll

/**
 * User: jorgefrancoleza
 * Date: 14/02/13
 */
class TestConversionOptions extends Specification {

    private static final FILE_BASIC_NAME = 'BasicClass'
    private static final FILE_BASIC_GROOVY_SOURCE = "src/test/resources/classes/${FILE_BASIC_NAME}.groovy"
    private static final FOLDER_NEED_DEPENDENCY = "need"
    private static final CLASS_NEED_DEPENDENCY = "Need"
    private static final SEP = System.getProperty('file.separator')
    private static final FILE_BASIC_JS = "${FOLDER_NEED_DEPENDENCY}${SEP}${FILE_BASIC_NAME}.js"
    private static final SOURCE_DIR = 'source'
    private static final DESTINATION_DIR = 'destination'
    private static final DESTINATION_FILE = 'destination.js'

    def setup() {
        GrooScript.clearAllOptions()
        new File(SOURCE_DIR).mkdir()
        new File(DESTINATION_DIR).mkdir()
    }

    def cleanup() {
        new File(FOLDER_NEED_DEPENDENCY).deleteDir()
        new File(SOURCE_DIR).deleteDir()
        new File(DESTINATION_FILE).delete()
        new File(DESTINATION_DIR).deleteDir()
    }

    def 'initial values for conversion'() {
        expect:
        expectedInitialValues()

        when:
        GrooScript.clearAllOptions()

        then:
        expectedInitialValues()
    }

    def 'convert a groovy file'() {
        given:
        def name = 'name'
        createFolderWithFiles(SOURCE_DIR, 1, name)

        when:
        GrooScript.convert("${SOURCE_DIR + SEP + name}0.groovy", DESTINATION_DIR)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, 1)
    }

    def 'convert a list of groovy files'() {
        given:
        def name = 'name'
        createFolderWithFiles(SOURCE_DIR, 2, name)

        when:
        GrooScript.convert(["${SOURCE_DIR + SEP + name}0.groovy",
                "${SOURCE_DIR + SEP + name}1.groovy"], DESTINATION_DIR)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, 2)
    }

    def 'cat set options more than 1 time'() {
        when:
        GrooScript.setConversionProperty(GrooScript.CLASSPATH_OPTION, FOLDER_NEED_DEPENDENCY)
        GrooScript.setConversionProperty(GrooScript.CLASSPATH_OPTION, FOLDER_NEED_DEPENDENCY)

        then:
        GrooScript.options.size() == 1
    }

    def 'check dependency resolution'() {

        given:
        setupNeedDirectory()
        GrooScript.setConversionProperty(GrooScript.CLASSPATH_OPTION, FOLDER_NEED_DEPENDENCY)

        when: 'convert a class with need dependency'
        String result = GrooScript.convert("class A {};def need = new ${CLASS_NEED_DEPENDENCY}()")

        then:
        result.startsWith('function A()')
        result.endsWith("var need = ${CLASS_NEED_DEPENDENCY}();\n")

        and: 'class need also converted'
        result.contains("function ${CLASS_NEED_DEPENDENCY}()")
    }

    def 'testing convert dependencies option true'() {

        given:
        setupNeedDirectory()

        when:
        GrooScript.setConversionProperty(GrooScript.CLASSPATH_OPTION, FOLDER_NEED_DEPENDENCY)
        GrooScript.setConversionProperty(GrooScript.CONVERT_DEPENDENCIES_OPTION, true)
        String result = GrooScript.convert("class B { ${CLASS_NEED_DEPENDENCY} c}")

        then: 'Need class converted'
        result.startsWith('function B()')
        result.contains('function Need()')
    }

    def 'testing convert dependencies option false'() {

        given:
        setupNeedDirectory()

        when: 'we dont want dependencies classes in the javascript result, in this case Need class'
        GrooScript.setConversionProperty(GrooScript.CLASSPATH_OPTION, FOLDER_NEED_DEPENDENCY)
        GrooScript.setConversionProperty(GrooScript.CONVERT_DEPENDENCIES_OPTION, false)
        String result = GrooScript.convert("class B { ${CLASS_NEED_DEPENDENCY} c}")

        then: 'Need class not converted'
        result.startsWith('function B()')
        !result.contains('function Need()')
    }

    def 'can set classpath as List'() {

        given:
        setupNeedDirectory()

        when: 'we set classpath as list'
        GrooScript.setConversionProperty(GrooScript.CLASSPATH_OPTION, [FOLDER_NEED_DEPENDENCY])
        String result = GrooScript.convert("class B { def Need c}")

        then: 'conversion done'
        result
        result.contains('function Need()')
    }

    def 'with conversion option convertDependencies=false on a file without dependencies'() {

        given:
        setupNeedDirectory()

        when: 'no dependencies to convert in a file in a package'
        GrooScript.setConversionProperty(GrooScript.CONVERT_DEPENDENCIES_OPTION, false)
        GrooScript.convert(FILE_BASIC_GROOVY_SOURCE, FOLDER_NEED_DEPENDENCY)
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
        GrooScript.setConversionProperty(GrooScript.CUSTOMIZATION_OPTION, customization)
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
        file.text == '0\r\n1\r\n2\r\n3\r\n4\r\n'
    }

    def 'define main context scope variables'() {
        given:
        def code = 'def addToB = { a ->  console.log("Hello!"); a + b }'
        GrooScript.setConversionProperty(GrooScript.MAIN_CONTEXT_SCOPE_OPTION, ['b', 'console'])

        when:
        def result = GrooScript.convert(code)

        then:
        result == '''var addToB = function(a) {
  gs.mc(console,"log",gs.list(["Hello!"]));
  return gs.plus(a, b);
};
'''
    }

    @Unroll
    def 'add text before and after in generated result'() {
        given:
        def code = 'def a = 0'

        when:
        GrooScript.setConversionProperty(option, 'Text')
        def result = GrooScript.convert(code)

        then:
        result == expectedResult

        where:
        option                         | expectedResult
        GrooScript.INITIAL_TEXT_OPTION | 'Text\nvar a = 0;\n'
        GrooScript.FINAL_TEXT_OPTION   | 'var a = 0;\n\nText'
    }

    @Unroll
    def 'test recursive conversion'() {
        given:
        createFolderWithSubfolderAndFilesInEachDir(SOURCE_DIR, sourceFiles)

        when:
        GrooScript.setConversionProperty(GrooScript.RECURSIVE_OPTION, true)
        GrooScript.convert(SOURCE_DIR, DESTINATION_DIR)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, destinationFiles)

        where:
        sourceFiles |destinationFiles
        0           |0
        1           |2
        3           |6
    }

    private void expectedInitialValues() {
        assert GrooScript.recursive == false
        assert GrooScript.debug == false
        assert GrooScript.options == [:]
    }

    private setupNeedDirectory() {
        new File(FOLDER_NEED_DEPENDENCY).mkdir()
        new File(FOLDER_NEED_DEPENDENCY+SEP+CLASS_NEED_DEPENDENCY+'.groovy') << "class ${CLASS_NEED_DEPENDENCY} {}"
    }

    private setupFilesWithNumbers(name, number) {
        new File(name).mkdir()
        number.times {
            new File(name + SEP + it + '.js') << it as String
        }
    }

    private createFolderWithFiles(sourceDir, numberOfFilesInside, name = 'name') {
        new File(sourceDir).mkdir()
        numberOfFilesInside.times {
            new File(sourceDir + SEP + name + it + '.groovy') << it as String
        }
    }

    private createFolderWithSubfolderAndFilesInEachDir(sourceDir, numberOfFilesInEachDir) {
        createFolderWithFiles(sourceDir, numberOfFilesInEachDir, 'base')
        createFolderWithFiles(sourceDir + SEP + 'inside', numberOfFilesInEachDir, 'inside')
    }

    private void destinationDirContainsFiles(destinationDir, numberTotalOfFiles) {
        assert numberTotalOfFiles == new File(destinationDir).listFiles().size()
    }
}

package org.grooscript.convert

import org.grooscript.GrooScript
import org.grooscript.JsGenerator
import org.grooscript.util.Util
import spock.lang.IgnoreIf
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

    def setupSpec() {
        JsGenerator.generateGrooscriptToolsJs()
    }

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
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FOLDER_NEED_DEPENDENCY)
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FOLDER_NEED_DEPENDENCY)

        then:
        GrooScript.options[ConversionOptions.CLASSPATH.text] == FOLDER_NEED_DEPENDENCY
    }

    def 'check dependency resolution'() {

        given:
        setupNeedDirectory()
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, FOLDER_NEED_DEPENDENCY)

        when: 'convert a class with need dependency'
        String result = GrooScript.convert("class A {};def need = new ${CLASS_NEED_DEPENDENCY}()")

        then:
        result.startsWith('function A()')
        result.endsWith("var need = ${CLASS_NEED_DEPENDENCY}();\n")

        and: 'class need not converted'
        !result.contains("function ${CLASS_NEED_DEPENDENCY}()")
    }

    def 'can set classpath as List'() {

        given:
        setupNeedDirectory()

        when: 'we set classpath as list'
        GrooScript.setConversionProperty(ConversionOptions.CLASSPATH.text, [FOLDER_NEED_DEPENDENCY])
        String result = GrooScript.convert("class B { Need c = new Need() }")

        then: 'conversion done'
        result
        !result.contains('function Need()')
        result.contains('c = Need();')
    }

    def 'convert a file'() {

        given:
        setupNeedDirectory()

        when: 'no dependencies to convert in a file in a package'
        GrooScript.convert(FILE_BASIC_GROOVY_SOURCE, FOLDER_NEED_DEPENDENCY)
        def file = new File(FILE_BASIC_JS)

        then: 'Conversion returns data converted'
        file.text.startsWith("function ${FILE_BASIC_NAME}()")
    }

    @IgnoreIf({ !Util.groovyVersionAtLeast('2.1') })
    @Unroll
    def 'customization option with a type check customization'() {

        given:
        def customization = {
            ast(astChecking)
        }

        when:
        GrooScript.setConversionProperty(ConversionOptions.CUSTOMIZATION.text, customization)
        GrooScript.convert('class A {  def say() { println aaaa }}')

        then:
        thrown(Exception)

        where:
        astChecking << [groovy.transform.TypeChecked, groovy.transform.CompileStatic]
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
        GrooScript.setConversionProperty(ConversionOptions.MAIN_CONTEXT_SCOPE.text, ['b', 'console'])

        when:
        def result = GrooScript.convert(code)

        then:
        result == '''var addToB = function(a) {
  gs.mc(console,"log",["Hello!"]);
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
        option                              | expectedResult
        ConversionOptions.INITIAL_TEXT.text | 'Text\nvar a = 0;\n'
        ConversionOptions.FINAL_TEXT.text   | 'var a = 0;\n\nText'
    }

    @Unroll
    def 'test recursive conversion'() {
        given:
        createFolderWithSubfolderAndFilesInEachDir(SOURCE_DIR, sourceFiles)

        when:
        GrooScript.setConversionProperty(ConversionOptions.RECURSIVE.text, true)
        GrooScript.convert(SOURCE_DIR, DESTINATION_DIR)

        then:
        destinationDirContainsFiles(DESTINATION_DIR, destinationFiles)

        where:
        sourceFiles | destinationFiles
        0           | 0
        1           | 2
        3           | 6
    }

    @Unroll
    def 'test include js archive at the beginning of the conversion'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.INCLUDE_JS_LIB.text, fileName)
        def result = GrooScript.convert('println "Hello!"')

        then:
        result.startsWith(new File("src/main/resources/META-INF/resources/${fileName}.js").text)

        where:
        fileName << ['grooscript', 'grooscript.min']
    }

    def 'test include two js archives at the beginning of the conversion'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.INCLUDE_JS_LIB.text, 'grooscript.min, grooscript-tools')
        def result = GrooScript.convert('println "Hello!"')

        then:
        result.startsWith(new File("src/main/resources/META-INF/resources/grooscript.min.js").text)
        result.contains('function HtmlBuilder')
        result.contains('function GQueryImpl')
    }

    def 'test use google closure tools library'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.USE_JS_LIB.text, 'google')
        def result = GrooScript.convert '''
import static org.grooscript.GrooScript.useJsLib

def goog = useJsLib('goog.crypt.base64')

def input = "Lorem ipsum dolor sit amet"
def output = goog.crypt.base64.encodeString input
println "Original string: $input"
println "Encoded base64 string: $output"
'''
        then:
        result == '''goog.require('goog.crypt.base64');
var input = "Lorem ipsum dolor sit amet";
var output = goog.crypt.base64.encodeString(input);
gs.println("Original string: " + (input) + "");
gs.println("Encoded base64 string: " + (output) + "");
'''
    }

    def 'test require dojo'() {
        when:
        GrooScript.setConversionProperty(ConversionOptions.USE_JS_LIB.text, 'dojo')
        def result = GrooScript.convert '''
import static org.grooscript.packages.DojoPackage.require

require("dojo/cldr/monetary") {
    ["EUR", "USD", "GBP", "JPY"].each {
        def cldrMonetaryData = dojo.cldr.monetary.getData(it)
        println "Places: $cldrMonetaryData.places"
        println "Round: $cldrMonetaryData.round"
    }
}
'''
        then:
        result == '''require(["dojo/cldr/monetary"], function(monetary) {
  gs.mc(gs.list(["EUR" , "USD" , "GBP" , "JPY"]),"each",[function(it) {
    var cldrMonetaryData = monetary.getData(it);
    gs.println("Places: " + (gs.gp(cldrMonetaryData,"places")) + "");
    return gs.println("Round: " + (gs.gp(cldrMonetaryData,"round")) + "");
  }]);
});
'''
    }

    private void expectedInitialValues() {
        assert GrooScript.debug == false
        assert GrooScript.options == null
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

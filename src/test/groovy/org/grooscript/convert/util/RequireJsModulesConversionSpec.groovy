package org.grooscript.convert.util

import org.grooscript.convert.GsConverter
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.util.FileSolver
import org.grooscript.util.GsConsole
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 12/4/15.
 */
class RequireJsModulesConversionSpec extends Specification {

    void 'convert require modules if sourceFile not exists'() {
        given:
        GroovySpy(GsConsole, global: true)

        when:
        def result = requireJs.convert(invalidFile, destinationFolder)

        then:
        1 * GsConsole.error("File ${invalidFile} doesn't exists.")
        0 * codeConverter._
        result == []
    }

    void 'convert require modules without dependencies'() {
        when:
        def result = requireJs.convert(validFile, destinationFolder)

        then:
        1 * dependenciesSolver.processFile(validFile) >> []
        1 * codeConverter.toJs(validFileCode, null) >> convertedCode
        1 * astTreeGenerator.classNodeNamesFromText(validFileCode) >> validFileClasses
        0 * _
        result == [validFile]
    }

    void 'convert require modules with dependencies'() {
        when:
        def result = requireJs.convert(validFile, destinationFolder)

        then:
        1 * dependenciesSolver.processFile(validFile) >> [dependenciesFile]
        2 * codeConverter.toJs(_, null) >> convertedCode
        2 * astTreeGenerator.classNodeNamesFromText(_) >> validFileClasses
        0 * _
        result == [dependenciesFile, validFile]
    }

    private validFile = 'File.groovy'
    private dependenciesFile = 'FileDep.groovy'
    private invalidFile = 'invalid'
    private validFileCode = 'file code'
    private convertedCode = 'convertedCode'
    private destinationFolder = 'dest'
    private validFileClasses = ['A']
    private DependenciesSolver dependenciesSolver = Mock()
    private GsConverter codeConverter = Mock()
    private AstTreeGenerator astTreeGenerator = Mock()
    private FileSolver fileSolver = Stub(FileSolver) {
        it.exists(invalidFile) >> false
        it.exists(validFile) >> true
        it.readFile(validFile) >> validFileCode
    }
    private RequireJsModulesConversion requireJs = new RequireJsModulesConversion(
            dependenciesSolver: dependenciesSolver,
            fileSolver: fileSolver,
            codeConverter: codeConverter,
            astTreeGenerator: astTreeGenerator
    )
}

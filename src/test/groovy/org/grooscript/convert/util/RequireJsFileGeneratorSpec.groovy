package org.grooscript.convert.util

import org.grooscript.util.FileSolver
import spock.lang.Specification

import static org.grooscript.util.Util.LINE_SEPARATOR as LS
import static org.grooscript.util.Util.SEP
/**
 * Created by jorgefrancoleza on 12/4/15.
 */
class RequireJsFileGeneratorSpec extends Specification {

    void 'generate simple module'() {
        when:
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, basicModuleResult)
    }

    void 'generate module in other path'() {
        when:
        requireTemplate.requireFolder = 'dest'
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile("dest${SEP}$destFileName", basicModuleResult)
    }

    void 'generate module in other path and file in a folder'() {
        when:
        requireTemplate.requireFolder = 'dest'
        requireTemplate.destinationFile = 'data' + SEP + 'other.js'
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile("dest${SEP}data${SEP}other.js", basicModuleResult)
    }

    void 'module with dependencies'() {
        when:
        requireTemplate.dependencies = [
                new RequireJsDependency(path: 'a/b/A', name: 'A'),
                new RequireJsDependency(path: 'C', name: 'D')
        ]
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, "define(['a/b/A','C'], function (A,D) {${LS}jsCode${LS}});")
    }

    void 'returns one class'() {
        when:
        requireTemplate.classes = ['A']
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, "define(function () {${LS}jsCode${LS}return A;${LS}});")
    }

    void 'returns more than one class'() {
        when:
        requireTemplate.classes = ['A', 'B']
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, "define(function () {${LS}jsCode${LS}return {A:A,B:B};${LS}});")
    }

    private basicModuleResult = "define(function () {${LS}jsCode${LS}});"
    private destFileName = 'file.js'
    private RequireJsTemplate requireTemplate = new RequireJsTemplate(
            dependencies: [],
            requireFolder: '.',
            destinationFile: destFileName,
            jsCode: 'jsCode',
            classes: []
    )
    private FileSolver fileSolver = Mock(FileSolver)
    private RequireJsFileGenerator require = new RequireJsFileGenerator(fileSolver: fileSolver)
}
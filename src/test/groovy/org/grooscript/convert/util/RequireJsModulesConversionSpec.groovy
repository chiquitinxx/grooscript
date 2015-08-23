/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.convert.util

import org.grooscript.convert.GsConverter
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.util.FileSolver
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole
import spock.lang.Specification
import spock.lang.Unroll

import static org.grooscript.convert.util.RequireJsModulesConversion.DEFAULT_PATH
import static org.grooscript.util.Util.SEP

class RequireJsModulesConversionSpec extends Specification {

    @Unroll
    void 'destination js file from dependency'() {
        given:
        fileSolver.filePathFromClassName(dependency) >> dependency

        expect:
        requireJs.destinationFromDependency(dependency) == dependency + '.js'

        where:
        dependency << ['A', "A.a"]
    }

    @Unroll
    void 'destination js file from filePath'() {
        given:
        fileSolver.canonicalPath('A.groovy') >> "${SEP}anyFolder${SEP}A.groovy"
        fileSolver.canonicalPath("folder${SEP}A.groovy") >> "${SEP}anyFolder${SEP}folder${SEP}A.groovy"
        fileSolver.canonicalPath("folder${SEP}a${SEP}A.groovy") >> "${SEP}anyFolder${SEP}folder${SEP}a${SEP}A.groovy"
        fileSolver.canonicalPath('.') >> "${SEP}anyFolder"
        fileSolver.canonicalPath('folder') >> "${SEP}anyFolder${SEP}folder"

        expect:
        requireJs.destinationFromFilePath(filePath, classPath) == expectedResult

        where:
        filePath                      | classPath | expectedResult
        'A.groovy'                    | '.'       | "A.js"
        "folder${SEP}A.groovy"        | '.'       | "folder${SEP}A.js"
        "folder${SEP}A.groovy"        | 'folder'  | "A.js"
        "folder${SEP}a${SEP}A.groovy" | 'folder'  | "a${SEP}A.js"
    }

    void 'convert require modules if sourceFile not exists'() {
        given:
        GroovySpy(GsConsole, global: true)

        when:
        requireJs.convert(invalidFile, destinationFolder)

        then:
        def e = thrown(GrooScriptException)
        e.message == 'File invalid doesn\'t exists.'
    }

    void 'convert require modules without dependencies'() {
        given:
        fileSolver.canonicalPath(validFile) >> "${SEP}${validFile}"
        fileSolver.canonicalPath(DEFAULT_PATH) >> SEP

        when:
        def result = requireJs.convert(validFile, destinationFolder)

        then:
        result == [new ConvertedFile('File.groovy', 'File.js')]
        1 * dependenciesSolver.processFile(validFile) >> []
        1 * localDependenciesSolver.fromText(validFileCode) >> ['thing.uh']
        1 * codeConverter.toJs(validFileCode, [requireJsModule: true]) >> convertedCode
        1 * codeConverter.getRequireJsDependencies() >> [requireJsDependency]
        1 * astTreeGenerator.classNodeNamesFromText(validFileCode) >> validFileClasses
        1 * requireJsModuleGenerator.generate(new RequireJsTemplate(
                destinationFile: 'File.js',
                requireFolder: destinationFolder,
                dependencies: [new RequireJsDependency("thing${SEP}uh", 'uh'), requireJsDependency],
                jsCode: convertedCode,
                classes: validFileClasses
        ))
        0 * _
    }

    private validFile = 'File.groovy'
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
        it.filePathFromClassName("to.path") >> "to${SEP}path"
        it.filePathFromClassName("thing.uh") >> "thing${SEP}uh"
    }
    private RequireJsFileGenerator requireJsModuleGenerator = Mock()
    private localDependenciesSolver = Mock(LocalDependenciesSolver)
    private RequireJsModulesConversion requireJs = new RequireJsModulesConversion(
            dependenciesSolver: dependenciesSolver,
            fileSolver: fileSolver,
            codeConverter: codeConverter,
            astTreeGenerator: astTreeGenerator,
            requireJsFileGenerator: requireJsModuleGenerator,
            localDependenciesSolver: localDependenciesSolver
    )
    private getRequireJsDependency() {
        new RequireJsDependency("to${SEP}path", 'data')
    }
}

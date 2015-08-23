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

import org.grooscript.util.FileSolver
import spock.lang.Specification

import static org.grooscript.util.Util.LINE_SEPARATOR as LS
import static org.grooscript.util.Util.SEP

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
                new RequireJsDependency(path: 'a\\b\\B', name: 'B'),
                new RequireJsDependency(path: 'C', name: 'D')
        ]
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, "define(['a/b/A','a/b/B','C'], function (A,B,D) {${LS}jsCode${LS}});")
    }

    void 'returns one class'() {
        when:
        requireTemplate.classes = ['A']
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, "define(function () {${LS}jsCode${LS}  return A;${LS}});")
    }

    void 'returns more than one class'() {
        when:
        requireTemplate.classes = ['A', 'B']
        require.generate(requireTemplate)

        then:
        1 * fileSolver.saveFile(destFileName, "define(function () {${LS}jsCode${LS}  return {A:A,B:B};${LS}});")
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
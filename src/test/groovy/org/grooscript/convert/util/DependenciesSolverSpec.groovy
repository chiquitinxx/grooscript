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

class DependenciesSolverSpec extends Specification {

    void 'resolve all dependencies from a file without dependencies'() {
        given:
        localDependenciesSolver.fromText(fileContent) >> emptySet

        expect:
        dependenciesSolver.processFile(filePath) == []
    }

    void 'resolve all dependencies from a file with dependencies'() {
        given:
        localDependenciesSolver.fromText(fileContent) >> ([otherClassName] as Set)

        expect:
        dependenciesSolver.processFile(filePath) == [otherClassName]
    }

    void 'circular dependencies'() {
        given:
        localDependenciesSolver.fromText(fileContent) >> ([otherClassName] as Set)
        localDependenciesSolver.fromText(otherFileContent) >> ([fullClassName] as Set)

        expect:
        dependenciesSolver.processFile(filePath) == [otherClassName, fullClassName]
    }

    void 'chain dependencies'() {
        given:
        localDependenciesSolver.fromText(fileContent) >> ([otherClassName] as Set)
        localDependenciesSolver.fromText(otherFileContent) >> ([thirdClassName] as Set)

        expect:
        dependenciesSolver.processFile(filePath) == [otherClassName, thirdClassName]
    }

    void 'repeat dependencies'() {
        given:
        localDependenciesSolver.fromText(fileContent) >> ([otherClassName] as Set)
        localDependenciesSolver.fromText(otherFileContent) >> ([otherClassName, fullClassName] as Set)

        expect:
        dependenciesSolver.processFile(filePath) == [otherClassName, fullClassName]
    }

    void 'process groovy code to get dependencies'() {
        given:
        def groovyCode = 'println "Hello World!"'
        localDependenciesSolver.fromText(groovyCode) >> ([] as Set)
        localDependenciesSolver.fromText(fileContent) >> ([otherClassName] as Set)

        expect:
        dependenciesSolver.processCode(groovyCode) == []
        dependenciesSolver.processCode(fileContent) == [new File(otherFilePath)]
    }

    private emptySet = [] as Set
    private String filePath = 'file.groovy'
    private String otherFilePath = 'otherFile.groovy'
    private String classpath = 'classpath'
    private String fileContent = 'content'
    private String otherFileContent = 'other content'
    private String fullClassName = 'org.grooscript.GrooScript'
    private String otherClassName = 'org.grooscript.JsNames'
    private String thirdClassName = 'org.grooscript.Gs'
    private LocalDependenciesSolver localDependenciesSolver = Stub(LocalDependenciesSolver)
    private FileSolver fileSolver = Stub(FileSolver) {
        it.readFile(filePath) >> fileContent
        it.readFile(otherFilePath) >> otherFileContent
        it.filePathFromClassName(fullClassName, classpath) >> filePath
        it.filePathFromClassName(otherClassName, classpath) >> otherFilePath
    }
    private DependenciesSolver dependenciesSolver = new DependenciesSolver(
            localDependenciesSolver: localDependenciesSolver,
            classpath: classpath,
            fileSolver: fileSolver
    )
}

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

class DependenciesSolver {

    LocalDependenciesSolver localDependenciesSolver
    String classpath
    FileSolver fileSolver

    List<String> processFile(String filePath) {
        resolveDependencies(fileSolver.readFile(filePath), [] as Set, [filePath] as Set).toList().unique()
    }

    List<File> processCode(String groovyCode) {
        resolveDependencies(groovyCode, [] as Set, [] as Set).toList().unique().collect {
            def filePath = fileSolver.filePathFromClassName(it, classpath)
            new File(filePath)
        }
    }

    private Set<String> resolveDependencies(String content,
                                            Set<String> dependencies, Set<String> processedFiles) {
        def currentDependencies = dependencies + localDependenciesSolver.fromText(content)
        def result = [] as Set
        currentDependencies.each { className ->
            String filePath = fileSolver.filePathFromClassName(className, classpath)
            if (!(filePath in processedFiles)) {
                processedFiles << filePath
                result = result + resolveDependencies(fileSolver.readFile(filePath), currentDependencies, processedFiles)
            }
        }
        (result + currentDependencies) as Set
    }
}

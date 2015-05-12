package org.grooscript.convert.util

import org.grooscript.util.FileSolver

/**
 * Created by jorgefrancoleza on 10/3/15.
 */
class DependenciesSolver {

    LocalDependenciesSolver localDependenciesSolver
    String classPath
    FileSolver fileSolver

    List<String> processFile(String filePath) {
        resolveDependencies(fileSolver.readFile(filePath), [] as Set, [filePath] as Set).toList().unique()
    }

    private Set<String> resolveDependencies(String content,
                                            Set<String> dependencies, Set<String> processedFiles) {
        def currentDependencies = dependencies + localDependenciesSolver.fromText(content)
        def result = [] as Set
        currentDependencies.each { className ->
            String filePath = fileSolver.filePathFromClassName(className, classPath)
            if (!(filePath in processedFiles)) {
                processedFiles << filePath
                result = result + resolveDependencies(fileSolver.readFile(filePath), currentDependencies, processedFiles)
            }
        }
        (result + currentDependencies) as Set
    }
}

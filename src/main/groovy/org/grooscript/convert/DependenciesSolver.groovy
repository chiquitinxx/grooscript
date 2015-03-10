package org.grooscript.convert

import org.grooscript.convert.ast.LocalDependenciesSolver
import org.grooscript.util.FileSolver

/**
 * Created by jorgefrancoleza on 10/3/15.
 */
class DependenciesSolver {

    LocalDependenciesSolver localDependenciesSolver
    String classPath
    FileSolver fileSolver

    List<String> processFile(String filePath) {
        resolveDependencies(fileSolver.readFile(filePath), [] as Set, [filePath] as Set).toList()
    }

    private Set<String> resolveDependencies(String content,
                                            Set<String> allDependencies, Set<String> processedFiles) {
        allDependencies.addAll localDependenciesSolver.fromText(content)
        allDependencies.each { className ->
            String filePath = fileSolver.filePathFromClassName(className, classPath)
            if (!(filePath in processedFiles)) {
                processedFiles << filePath
                resolveDependencies(fileSolver.readFile(filePath), allDependencies, processedFiles)
            }
        }
        allDependencies
    }
}

package org.grooscript.convert.util

import org.grooscript.util.FileSolver

/**
 * Created by jorgefrancoleza on 10/3/15.
 */
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

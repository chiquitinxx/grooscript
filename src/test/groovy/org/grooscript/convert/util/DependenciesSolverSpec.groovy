package org.grooscript.convert.util

import org.grooscript.util.FileSolver
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 10/3/15.
 */
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

    private emptySet = [] as Set
    private String filePath = 'file.groovy'
    private String otherFilePath = 'otherFile.groovy'
    private String classPath = 'classPath'
    private String fileContent = 'content'
    private String otherFileContent = 'other content'
    private String fullClassName = 'org.grooscript.GrooScript'
    private String otherClassName = 'org.grooscript.JsNames'
    private String thirdClassName = 'org.grooscript.Gs'
    private LocalDependenciesSolver localDependenciesSolver = Stub(LocalDependenciesSolver)
    private FileSolver fileSolver = Stub(FileSolver) {
        it.readFile(filePath) >> fileContent
        it.readFile(otherFilePath) >> otherFileContent
        it.filePathFromClassName(fullClassName, classPath) >> filePath
        it.filePathFromClassName(otherClassName, classPath) >> otherFilePath
    }
    private DependenciesSolver dependenciesSolver = new DependenciesSolver(
            localDependenciesSolver: localDependenciesSolver,
            classPath: classPath,
            fileSolver: fileSolver
    )
}

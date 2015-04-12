package org.grooscript.convert.util

import org.grooscript.convert.GsConverter
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.util.FileSolver

import static org.grooscript.util.GsConsole.error

/**
 * Created by jorgefrancoleza on 12/4/15.
 */
class RequireJsModulesConversion {

    DependenciesSolver dependenciesSolver
    FileSolver fileSolver
    GsConverter codeConverter
    AstTreeGenerator astTreeGenerator

    List<String> convert(String sourceFilePath, String destinationPath, Map conversionOptions = null) {
        List<String> sourceFiles = []
        if (fileSolver.exists(sourceFilePath)) {
            def dependencies = dependenciesSolver.processFile(sourceFilePath)
            sourceFiles = dependencies + [sourceFilePath]
            sourceFiles.each {
                def sourceCode = fileSolver.readFile(it)
                def jsCode = codeConverter.toJs(sourceCode, conversionOptions)
                def generatedClasses = astTreeGenerator.classNodeNamesFromText(sourceCode)
                generateTemplate(destinationPath, dependencies, jsCode, generatedClasses)
            }
        } else {
            error("File ${sourceFilePath} doesn't exists.")
        }
        sourceFiles
    }

    private void generateTemplate(
            String destinationPath, List<String> dependencies, String jsCode, List<String> classes) {

    }
}

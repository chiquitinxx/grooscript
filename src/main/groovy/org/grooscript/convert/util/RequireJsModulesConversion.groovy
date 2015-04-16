package org.grooscript.convert.util

import org.grooscript.convert.ConversionOptions
import org.grooscript.convert.GsConverter
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.util.FileSolver

import static org.grooscript.util.GsConsole.error
import static org.grooscript.util.Util.JAVASCRIPT_EXTENSION
import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.GROOVY_EXTENSION

/**
 * Created by jorgefrancoleza on 12/4/15.
 */
class RequireJsModulesConversion {

    public static final DEFAULT_PATH = '.'

    DependenciesSolver dependenciesSolver
    FileSolver fileSolver
    GsConverter codeConverter
    AstTreeGenerator astTreeGenerator
    RequireJsFileGenerator requireJsFileGenerator
    LocalDependenciesSolver localDependenciesSolver

    List<String> convert(String sourceFilePath, String destinationFolder, Map conversionOptions = null) {
        List<String> sourceFiles = []
        if (fileSolver.exists(sourceFilePath)) {
            def dependencies = dependenciesSolver.processFile(sourceFilePath)
            def classPath = classPathFolder(conversionOptions)
            sourceFiles << generateTemplate(sourceFilePath, destinationFolder,
                    destinationFromFilePath(sourceFilePath, classPath), conversionOptions)
            dependencies.each {
                def filePath = filePathFromDependency(it, classPath)
                sourceFiles << generateTemplate(filePath, destinationFolder,
                        destinationFromDependency(it), conversionOptions)
            }
        } else {
            error("File ${sourceFilePath} doesn't exists.")
        }
        sourceFiles
    }

    private String generateTemplate(String sourceFilePath, String destinationFolder,
                                    String destinationFile, Map conversionOptions) {
        def sourceCode = fileSolver.readFile(sourceFilePath)
        def requireTemplate = new RequireJsTemplate(
                destinationFile: destinationFile,
                requireFolder: destinationFolder,
                dependencies: localDependenciesSolver.fromText(sourceCode) as List,
                jsCode: codeConverter.toJs(sourceCode, conversionOptions),
                classes: astTreeGenerator.classNodeNamesFromText(sourceCode)
        )
        requireJsFileGenerator.generate(requireTemplate)
        sourceFilePath
    }

    String filePathFromDependency(String dependency, String classPath) {
        def result = dependency.replaceAll(/\./, SEP) + GROOVY_EXTENSION
        classPath + SEP + result
    }

    String destinationFromDependency(String dependency) {
        dependency.replaceAll(/\./, SEP) + JAVASCRIPT_EXTENSION
    }

    String destinationFromFilePath(String filePath, String classPath) {
        def result = fileSolver.canonicalPath(filePath) - fileSolver.canonicalPath(classPath)
        while (result.startsWith(SEP)) {
            result = result.substring(SEP.size())
        }
        result.substring(0, result.size() - GROOVY_EXTENSION.size()) + JAVASCRIPT_EXTENSION
    }

    /*
    String sourceFileAsDependency(String sourceFile, String classPathFolder) {
        def result = fileSolver.canonicalPath(sourceFile) - fileSolver.canonicalPath(classPathFolder)
        while (result.startsWith(SEP)) {
            result = result.substring(SEP.size())
        }
        if (result.endsWith(Util.GROOVY_EXTENSION)) {
            result = result.substring(0, result.size() - Util.GROOVY_EXTENSION.size())
        }
        if (result.endsWith(Util.JAVA_EXTENSION)) {
            result = result.substring(0, result.size() - Util.JAVA_EXTENSION.size())
        }
        result.replaceAll(SEP, '.')
    }*/

    String classPathFolder(Map conversionOptions) {
        if (!conversionOptions || !conversionOptions[ConversionOptions.CLASSPATH.text]) {
            return DEFAULT_PATH
        } else {
            return firstFolderFrom(conversionOptions[ConversionOptions.CLASSPATH.text])
        }
    }

    private String firstFolderFrom(classPath) {
        if (classPath instanceof String && fileSolver.isFolder(classPath)) {
            return classPath
        }
        if (classPath instanceof List) {
            return classPath.find { fileSolver.isFolder(it) } ?: DEFAULT_PATH
        }
        DEFAULT_PATH
    }
}

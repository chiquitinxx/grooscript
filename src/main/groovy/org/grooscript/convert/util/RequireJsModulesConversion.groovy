package org.grooscript.convert.util

import org.grooscript.convert.ConversionOptions
import org.grooscript.convert.GsConverter
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.util.FileSolver

import java.util.regex.Matcher

import static org.grooscript.util.GsConsole.error
import static org.grooscript.util.Util.JS_EXTENSION
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

    List<ConvertedFile> convert(String sourceFilePath, String destinationFolder, Map conversionOptions = null) {
        List<ConvertedFile> convertedFiles = []
        if (fileSolver.exists(sourceFilePath)) {
            def dependencies = dependenciesSolver.processFile(sourceFilePath)
            def classPath = classPathFolder(conversionOptions)
            convertedFiles << generateTemplate(sourceFilePath, destinationFolder,
                    destinationFromFilePath(sourceFilePath, classPath), conversionOptions)
            dependencies.each {
                def filePath = fileSolver.filePathFromClassName(it, classPath)
                if (!convertedFiles.any { it.sourceFilePath == filePath}) {
                    convertedFiles << generateTemplate(filePath, destinationFolder,
                            destinationFromDependency(it), conversionOptions)
                }
            }
        } else {
            error("File ${sourceFilePath} doesn't exists.")
        }
        convertedFiles
    }

    private ConvertedFile generateTemplate(String sourceFilePath, String destinationFolder,
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
        new ConvertedFile(sourceFilePath: sourceFilePath, destinationFilePath: destinationFile)
    }

    String destinationFromDependency(String dependency) {
        fileSolver.filePathFromClassName(dependency) + JS_EXTENSION
    }

    String destinationFromFilePath(String filePath, String classPath) {
        def result = fileSolver.canonicalPath(filePath) - fileSolver.canonicalPath(classPath)
        while (result.startsWith(SEP)) {
            result = result.substring(SEP.size())
        }
        result.substring(0, result.size() - GROOVY_EXTENSION.size()) + JS_EXTENSION
    }

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

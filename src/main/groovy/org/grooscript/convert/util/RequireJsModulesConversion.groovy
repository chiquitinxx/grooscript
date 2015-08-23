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

import org.grooscript.convert.ConversionOptions
import org.grooscript.convert.GsConverter
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.util.FileSolver
import org.grooscript.util.GrooScriptException

import java.util.regex.Matcher

import static org.grooscript.util.GsConsole.error
import static org.grooscript.util.Util.JS_EXTENSION
import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.GROOVY_EXTENSION

class RequireJsModulesConversion {

    public static final DEFAULT_PATH = '.'

    DependenciesSolver dependenciesSolver
    FileSolver fileSolver
    GsConverter codeConverter
    AstTreeGenerator astTreeGenerator
    RequireJsFileGenerator requireJsFileGenerator
    LocalDependenciesSolver localDependenciesSolver

    List<ConvertedFile> convert(String sourceFilePath, String destinationFolder, Map conversionOptions = [:]) {
        List<ConvertedFile> convertedFiles = []
        if (fileSolver.exists(sourceFilePath)) {
            def dependencies = dependenciesSolver.processFile(sourceFilePath)
            def classPath = fileSolver.classPathFolder(conversionOptions[ConversionOptions.CLASSPATH.text])
            def requireJsConversionOptions = addRequireJsConversionOption(conversionOptions)
            //Generate dependencies
            dependencies.each {
                def filePath = fileSolver.filePathFromClassName(it, classPath)
                if (filePath != sourceFilePath && !convertedFiles.any { it.sourceFilePath == filePath }) {
                    convertedFiles << generateTemplate(filePath, destinationFolder,
                            destinationFromDependency(it), requireJsConversionOptions)
                }
            }
            //Generate initial file last one
            convertedFiles << generateTemplate(sourceFilePath, destinationFolder,
                    destinationFromFilePath(sourceFilePath, classPath), requireJsConversionOptions)
        } else {
            throw new GrooScriptException("File ${sourceFilePath} doesn't exists.")
        }
        convertedFiles
    }

    private ConvertedFile generateTemplate(String sourceFilePath, String destinationFolder,
                                    String destinationFile, Map conversionOptions) {
        String sourceCode = fileSolver.readFile(sourceFilePath)
        String jsCode = codeConverter.toJs(sourceCode, conversionOptions)
        List<RequireJsDependency> dependencies = getLocalDependencies(sourceCode) + codeConverter.requireJsDependencies
        def requireTemplate = new RequireJsTemplate(
                destinationFile: destinationFile, requireFolder: destinationFolder,
                dependencies: dependencies, jsCode: jsCode,
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

    private Map addRequireJsConversionOption(conversionOptions) {
        def result = conversionOptions ?: [:]
        result[ConversionOptions.REQUIRE_JS_MODULE.text] = true
        result
    }

    private List<RequireJsDependency> getLocalDependencies(sourceCode) {
        def list = localDependenciesSolver.fromText(sourceCode) as List
        list.collect {
            new RequireJsDependency(
                    name: it.split("\\.").last(),
                    path: fileSolver.filePathFromClassName(it)
            )
        }
    }
}

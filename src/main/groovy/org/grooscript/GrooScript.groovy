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
package org.grooscript

import org.grooscript.convert.ConversionOptions
import org.grooscript.convert.ast.AstTreeGenerator
import org.grooscript.convert.util.ConvertedFile
import org.grooscript.convert.util.DependenciesSolver
import org.grooscript.convert.util.LocalDependenciesSolver
import org.grooscript.convert.util.RequireJsFileGenerator
import org.grooscript.convert.util.RequireJsModulesConversion
import org.grooscript.test.JavascriptEngine
import org.grooscript.test.JsTestResult
import org.grooscript.convert.GsConverter
import org.grooscript.util.FileSolver
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static org.grooscript.util.Util.*

class GrooScript {

    /**
     * Get a new GsConverter
     * @return GsConverter
     */
    static GsConverter getNewConverter() {
        new GsConverter()
    }

    /**
     * Convert a piece of groovy code to javascript
     * @param String text groovy code
     * @param Map conversionOptions(optional)
     * @return String javascript result code
     * @throws Exception If conversion fails or text is null
     */
    static String convert(String text, Map conversionOptions = defaultConversionOptions) throws GrooScriptException {
        if (text) {
            def jsResult = convertGroovyCode(text, conversionOptions)
            return completeJsResult(jsResult, conversionOptions)
        }
        throw new GrooScriptException('Nothing to Convert.')
    }

    /**
     * Converts from a source to destination, groovy files to javascript
     * Result files will be .js with same name that groovy file if destination is a folder, or just one .js file
     * @param source (String or List of String's) directories with groovy files, or groovy files
     * @param destination folder or path to .js file
     * @param Map  conversionOptions(optional)
     * @throws Exception something fails
     */
    static void convert(source, String destination, Map conversionOptions = defaultConversionOptions) throws GrooScriptException {
        if (source && destination) {
            List<File> files = []
            if (source instanceof String || source instanceof GString) {
                files = addFileToBeConverted(new File(source), files, conversionOptions)
            } else if (source instanceof List) {
                source.each {
                    files = addFileToBeConverted(new File(it), files, conversionOptions)
                }
            } else {
                throw new GrooScriptException('Source must be a String or a list.')
            }
            convertFiles(files, new File(destination), conversionOptions)
        } else {
            throw new GrooScriptException('Have to define source and destination.')
        }
    }

    /**
     * Converts a list of files to a destination js file or path
     * @param sources
     * @param destination
     * @param Map conversionOptions(optional)
     */
    static void convert(List<File> sources, File destination, Map conversionOptions = defaultConversionOptions) {
        if (sources && destination) {
            List<File> files = []
            sources.each {
                files = addFileToBeConverted(it, files, conversionOptions)
            }
            convertFiles(files, destination, conversionOptions)
        } else {
            throw new GrooScriptException('Have to define sources and destination.')
        }
    }

    /**
     * Join js files in one directory to only file
     * @param sourceDirectory with js files
     * @param fileDestinationName name of destination file
     */
    static void joinFiles(sourceDirectory, fileDestinationName) {
        File source = new File(sourceDirectory)
        if (source && source.isDirectory()) {
            def newFile = new File(fileDestinationName)
            if (newFile.exists()) {
                newFile.delete()
            }
            source.eachFile { File file ->
                if (file.isFile() && file.name.toLowerCase().endsWith(JS_EXTENSION)) {
                    newFile.append(file.text + LINE_SEPARATOR)
                }
            }
        } else {
            GsConsole.error 'Source must be a directory'
        }
    }

    /**
     * Get default conversion options
     * @return
     */
    static Map<String, Object> getDefaultConversionOptions() {
        ConversionOptions.values().inject([:]) { map, value ->
            map[value.text] = value.defaultValue
            map
        }
    }

    /**
     * Evaluate a piece of groovy code
     * @param code that will be evaluated using grooscript.min
     * @param comma separated js libs to add for evaluation
     * @param Map conversionPptions(optional)
     * @return JsTestResult
     */
    static JsTestResult evaluateGroovyCode(String code, String jsLibs = null, Map conversionOptions = defaultConversionOptions) {
        String jsCode = convert(code)

        def jsScript = getJsLibText('grooscript.min')
        if (jsLibs) {
            jsLibs.split(',').each { nameLib ->
                jsScript += getJsLibText(nameLib.trim())
            }
        }
        jsScript = jsScript + JavascriptEngine.addEvaluationVars(jsCode)

        def testResult = JavascriptEngine.evaluateJsCode(jsScript)
        testResult.jsCode = jsCode
        testResult
    }

    /**
     * Get content of a javascript library inside grooscript.jar
     * grooscript, grooscript.min or grooscript-html-builder
     * @param nameJsLib
     * @return
     */
    static String getJsLibText(String nameJsLib) {
        GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + nameJsLib + '.js').text
    }

    /**
     * In converted code, convert data to 'groovy'
     * @param data
     * @return
     */
    static toGroovy(data) {
        data
    }

    /**
     * In converted code convert data to 'javascript'
     * @param data
     * @return
     */
    static toJavascript(data) {
        data
    }

    /**
     * In converted code, convert data to 'javascript object'
     * @param data
     * @return
     */
    static toJsObj(data) {
        data
    }

    /**
     * In converted code, code is not converted
     * @param code must be a quotes string 'console.log("1")'
     * @return
     */
    static nativeJs(String code) {
        code
    }

    /**
     * Convert a file to require.js modules, all dependencies are converted
     * @param initialFile
     * @param destinationFolder
     * @param Map conversionOptions
     * @return
     */
    static List<ConvertedFile> convertRequireJs(String initialFile, String destinationFolder, Map conversionOptions = defaultConversionOptions) {
        try {
            DependenciesSolver dependenciesSolver = newDependenciesSolver(conversionOptions)
            RequireJsModulesConversion reqJs = new RequireJsModulesConversion(
                    fileSolver: dependenciesSolver.fileSolver,
                    codeConverter: newConverter,
                    astTreeGenerator: new AstTreeGenerator(compilerOptions(conversionOptions)),
                    requireJsFileGenerator: new RequireJsFileGenerator(fileSolver: dependenciesSolver.fileSolver),
                    localDependenciesSolver: dependenciesSolver.localDependenciesSolver,
                    dependenciesSolver: dependenciesSolver
            )
            return reqJs.convert(initialFile, destinationFolder, conversionOptions)
        } catch (Throwable e) {
            throw new GrooScriptException(
                    "Error converting ${initialFile} to require.js modules. Exception: ${e.message}")
        }
    }

    /**
     * Get dependencies from groovy code, using classpath. Mainly used by gradle plugin
     * @param conversionOptions
     * @return DependenciesSolver
     */
    static DependenciesSolver newDependenciesSolver(Map conversionOptions) {
        FileSolver fileSolver = new FileSolver()
        LocalDependenciesSolver localDependenciesSolver = new LocalDependenciesSolver(compilerOptions(conversionOptions))
        new DependenciesSolver(
                fileSolver: fileSolver,
                classpath: fileSolver.classPathFolder(conversionOptions[ConversionOptions.CLASSPATH.text]),
                localDependenciesSolver: localDependenciesSolver
        )
    }

    private static Map compilerOptions(Map conversionOptions) {
        [
                classpath: conversionOptions[ConversionOptions.CLASSPATH.text],
                customization: conversionOptions[ConversionOptions.CUSTOMIZATION.text]
        ]
    }

    private static List<File> addFileToBeConverted(File fSource, List<File> files, Map conversionOptions) {

        if (fSource.exists()) {
            if (fSource.isDirectory()) {
                fSource.eachFile { file ->
                    files = addFileIfValid(files, file)
                }
                if (conversionOptions && conversionOptions[ConversionOptions.RECURSIVE.text]) {
                    fSource.eachDir { File dir ->
                        files = addFileToBeConverted(dir, files, conversionOptions)
                    }
                }
            } else {
                files = addFileIfValid(files, fSource)
            }
        }
        files
    }

    private static List<File> addFileIfValid(List<File> files, File file) {
        if (file && file.isFile() && (file.name.endsWith(GROOVY_EXTENSION) || file.name.endsWith(JAVA_EXTENSION))) {
            files << file
        }
        files
    }

    private static void convertFiles(List<File> files, File destination, Map conversionOptions) {

        try {
            if (files) {
                boolean toOneFile = destination && destination.name.endsWith(JS_EXTENSION)
                String allConvertedJs = ''

                files.each { File file ->
                    def jsResult = convertGroovyCode(file.text, conversionOptions)
                    if (toOneFile) {
                        allConvertedJs += jsResult
                    } else {
                        if (!destination.exists()) {
                            destination.mkdirs()
                        }
                        def name = file.name.split(/\./)[0]
                        def newFile = new File("${destination.path}$SEP$name$JS_EXTENSION")
                        saveFile(newFile, completeJsResult(jsResult, conversionOptions))
                    }
                }
                if (toOneFile) {
                    saveFile(destination, completeJsResult(allConvertedJs, conversionOptions))
                }
            } else {
                GsConsole.error('No files to be converted. *.groovy or *.java files not found.')
            }
        } catch (Throwable e) {
            throw new GrooScriptException("Convert Exception: ${e.message}")
        }
    }

    private static String convertGroovyCode(String source, Map conversionOptions) {
        String result = ''
        if (conversionOptions && conversionOptions[ConversionOptions.INCLUDE_DEPENDENCIES.text] == true) {
            DependenciesSolver dependenciesSolver = newDependenciesSolver(conversionOptions)
            List<File> dependencies = dependenciesSolver.processCode(source)
            dependencies.each {
                result = addIfNotExists(newConverter.toJs(it.text, conversionOptions), result)
            }
        }
        result = addIfNotExists(newConverter.toJs(source, conversionOptions), result)
        result
    }

    private static String addIfNotExists(String code, String all) {
        String result = all
        if (!all.contains(code)) {
            result += code
        }
        result
    }

    private static String completeJsResult(String result, Map conversionOptions) {
        if (conversionOptions) {
            if (conversionOptions[ConversionOptions.INITIAL_TEXT.text]) {
                result = conversionOptions[ConversionOptions.INITIAL_TEXT.text] + LINE_SEPARATOR + result
            }
            if (conversionOptions[ConversionOptions.FINAL_TEXT.text]) {
                result = result + LINE_SEPARATOR + conversionOptions[ConversionOptions.FINAL_TEXT.text]
            }
            if (conversionOptions[ConversionOptions.ADD_GS_LIB.text]) {
                def files = conversionOptions[ConversionOptions.ADD_GS_LIB.text].split(',').reverse()
                files.each { fileName ->
                    def file = GrooScript.classLoader.getResourceAsStream(
                            "META-INF/resources/${fileName.trim()}.js")
                    if (file) {
                        result = file.text + LINE_SEPARATOR + result
                    }
                }
            }
        }
        result
    }

    private static void saveFile(File file, String content) {
        if (content) {
            if (file.exists()) {
                file.delete()
            } else {
                if (file.parent) {
                    new File(file.parent).mkdirs()
                }
            }
            if (file.createNewFile()) {
                file.text = content
            } else {
                throw new GrooScriptException("Cannot create file ${file.absolutePath}")
            }
        } else {
            file.delete()
        }
    }
}

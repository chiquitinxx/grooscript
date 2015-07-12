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
/**
 * JFL 09/11/12
 */
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
     * @param Map options conversion options
     * @return String javascript result code
     * @throws Exception If conversion fails or text is null
     */
    static String convert(String text, Map options = defaultOptions) throws GrooScriptException {
        if (text) {
            def jsResult = newConverter.toJs(text, options)
            return completeJsResult(jsResult, options)
        }
        throw new GrooScriptException('Nothing to Convert.')
    }

    /**
     * Converts from a source to destination, groovy files to javascript
     * Result files will be .js with same name that groovy file if destination is a folder, or just one .js file
     * @param source (String or List of String's) directories with groovy files, or groovy files
     * @param destination folder or path to .js file
     * @param options conversion options
     * @throws Exception something fails
     */
    static void convert(source, String destination, Map options = defaultOptions) throws GrooScriptException {
        if (source && destination) {
            List<File> files = []
            if (source instanceof String || source instanceof GString) {
                files = addFileToBeConverted(new File(source), files, options)
            } else if (source instanceof List) {
                source.each {
                    files = addFileToBeConverted(new File(it), files, options)
                }
            } else {
                throw new GrooScriptException('Source must be a String or a list.')
            }
            convertFiles(files, new File(destination), options)
        } else {
            throw new GrooScriptException('Have to define source and destination.')
        }
    }

    /**
     * Converts a list of files to a destination js file or path
     * @param sources
     * @param destination
     * @param conversion options
     */
    static void convert(List<File> sources, File destination, Map options = defaultOptions) {
        if (sources && destination) {
            List<File> files = []
            sources.each {
                files = addFileToBeConverted(it, files, options)
            }
            convertFiles(files, destination, options)
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
     * Join files to a destination file
     * @param filePaths last is destination file
     */
    static void joinListOfFiles(... filePaths) {
        if (filePaths.size() < 3) {
            GsConsole.error('Params are files to join and destination file')
            return
        }
        File destinationFile = new File(filePaths.last())
        destinationFile.text = ''
        def filesToJoin = filePaths - filePaths.last()
        filesToJoin.each { sourceFile ->
            def file = new File(sourceFile)
            if (file.isFile()) {
                destinationFile.append(file.text + LINE_SEPARATOR)
            } else {
                GsConsole.error 'Error joining file ' + sourceFile
            }
        }
    }

    /**
     * Get default conversion options
     * @return
     */
    static Map<String, Object> getDefaultOptions() {
        ConversionOptions.values().inject([:]) { map, value ->
            map[value.text] = value.defaultValue
            map
        }
    }

    /**
     * Evaluate a piece of groovy code
     * @param code that will be evaluated using grooscript.min
     * @param comma separated js libs to add for evaluation
     * @param conversion options
     * @return JsTestResult
     */
    static JsTestResult evaluateGroovyCode(String code, String jsLibs = null, Map options = defaultOptions) {
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
     * grooscript, grooscript.min, grooscript-tools or jquery.min
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
     * @param conversion options
     * @return
     */
    static List<ConvertedFile> convertRequireJs(String initialFile, String destinationFolder, Map options = defaultOptions) {
        try {
            FileSolver fileSolver = new FileSolver()
            Map compilerOptions = [
                    classPath: options[ConversionOptions.CLASSPATH.text],
                    customization: options[ConversionOptions.CUSTOMIZATION.text]
            ]
            LocalDependenciesSolver localDependenciesSolver = new LocalDependenciesSolver(compilerOptions)
            RequireJsModulesConversion reqJs = new RequireJsModulesConversion(
                    fileSolver: fileSolver,
                    codeConverter: newConverter,
                    astTreeGenerator: new AstTreeGenerator(compilerOptions),
                    requireJsFileGenerator: new RequireJsFileGenerator(fileSolver: fileSolver),
                    localDependenciesSolver: localDependenciesSolver
            )
            String classPath = reqJs.classPathFolder(options)
            reqJs.dependenciesSolver = new DependenciesSolver(
                    fileSolver: fileSolver,
                    classPath: classPath,
                    localDependenciesSolver: localDependenciesSolver
            )
            return reqJs.convert(initialFile, destinationFolder, options)
        } catch (Throwable e) {
            throw new GrooScriptException(
                    "Error converting ${initialFile} to require.js modules. Exception: ${e.message}")
        }
    }

    private static List<File> addFileToBeConverted(File fSource, List<File> files, Map options) {

        if (fSource.exists()) {
            if (fSource.isDirectory()) {
                fSource.eachFile { file ->
                    addFileIfValid(files, file)
                }
                if (options && options[ConversionOptions.RECURSIVE.text]) {
                    fSource.eachDir { File dir ->
                        files = addFileToBeConverted(dir, files, options)
                    }
                }
            } else {
                addFileIfValid(files, fSource)
            }
        }
        files
    }

    private static addFileIfValid(List<File> files, File file) {
        if (file && file.isFile() && (file.name.endsWith(GROOVY_EXTENSION) || file.name.endsWith(JAVA_EXTENSION))) {
            files << file
        }
    }

    private static convertFiles(List<File> files, File destination, Map options) {

        try {
            if (files) {
                boolean toOneFile = destination && destination.name.endsWith(JS_EXTENSION)
                String allConvertedJs = ''

                files.each { File file ->
                    def jsResult = newConverter.toJs(file.text, options)
                    if (toOneFile) {
                        allConvertedJs += jsResult
                    } else {
                        if (!destination.exists()) {
                            destination.mkdirs()
                        }
                        def name = file.name.split(/\./)[0]
                        def newFile = new File("${destination.path}$SEP$name$JS_EXTENSION")
                        saveFile(newFile, completeJsResult(jsResult, options))
                    }
                }
                if (toOneFile) {
                    saveFile(destination, completeJsResult(allConvertedJs, options))
                }
            } else {
                GsConsole.error('No files to be converted. *.groovy or *.java files not found.')
            }
        } catch (Throwable e) {
            throw new GrooScriptException("Convert Exception: ${e.message}")
        }
    }

    private static String completeJsResult(String result, Map options) {
        if (options) {
            if (options[ConversionOptions.INITIAL_TEXT.text]) {
                result = options[ConversionOptions.INITIAL_TEXT.text] + LINE_SEPARATOR + result
            }
            if (options[ConversionOptions.FINAL_TEXT.text]) {
                result = result + LINE_SEPARATOR + options[ConversionOptions.FINAL_TEXT.text]
            }
            if (options[ConversionOptions.ADD_GS_LIB.text]) {
                def files = options[ConversionOptions.ADD_GS_LIB.text].split(',').reverse()
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
    }
}

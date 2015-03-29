package org.grooscript

import org.grooscript.convert.ConversionOptions
import org.grooscript.test.JavascriptEngine
import org.grooscript.test.JsTestResult
import org.grooscript.convert.GsConverter
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static org.grooscript.util.Util.*
/**
 * JFL 09/11/12
 */
class GrooScript {

    static boolean debug = false
    static Map options

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
     * @return String javascript result code
     * @throws Exception If conversion fails or text is null
     */
    static String convert(String text) {
        if (text) {
            def jsResult = newConverter.toJs(text, options)
            return completeJsResult(jsResult)
        }
        throw new GrooScriptException('Nothing to Convert.')
    }

    /**
     * Converts from a source to destination, groovy files to javascript
     * Result files will be .js with same name that groovy file if destination is a folder, or just one .js file
     * @param source (String or List of String's) directories with groovy files, or groovy files
     * @param destination folder or path to .js file
     * @throws Exception something fails
     */
    static void convert(source, String destination) {
        if (source && destination) {
            List<File> files = []
            if (source instanceof String || source instanceof GString) {
                files = checkConvertFile(new File(source))
            } else if (source instanceof List) {
                source.each {
                    files = files + checkConvertFile(new File(it))
                }
            } else {
                throw new GrooScriptException('Source must be a String or a list.')
            }
            convertFiles(files, new File(destination))
        } else {
            throw new GrooScriptException('Have to define source and destination.')
        }
    }

    /**
     * Converts a list of files to a destination js file or path
     * @param sources
     * @param destination
     */
    static void convert(List<File> sources, File destination) {
        if (sources && destination) {
            List<File> files = []
            sources.each {
                files = files + checkConvertFile(it)
            }
            convertFiles(files, destination)
        } else {
            throw new GrooScriptException('Have to define sources and destination.')
        }
    }

    private static checkConvertFile(File fSource, List<File> files = []) {

        if (fSource.exists()) {
            if (fSource.isDirectory()) {
                fSource.eachFile { file ->
                    addFileIfValid(files, file)
                }
                if (options && options[ConversionOptions.RECURSIVE.text]) {
                    fSource.eachDir { File dir ->
                        files = checkConvertFile(dir, files)
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

    private static convertFiles(List<File> files, File destination) {

        try {
            if (files) {
                boolean toOneFile = destination && destination.name.endsWith(JS_EXTENSION)
                String allConvertedJs = ''

                files.each { File file ->
                    if (debug) {
                        GsConsole.debug("    Converting file ${file.absolutePath}...")
                    }
                    def jsResult = newConverter.toJs(file.text, options)

                    if (toOneFile) {
                        allConvertedJs += jsResult
                    } else {
                        if (!destination.exists()) {
                            destination.mkdirs()
                        }
                        def name = file.name.split(/\./)[0]
                        def newFile = new File("${destination.path}$SEP$name$JS_EXTENSION")
                        saveFile(newFile, completeJsResult(jsResult))
                    }

                    if (debug) {
                        GsConsole.debug("    Converted file ${file.absolutePath}.")
                    }
                }
                if (toOneFile) {
                    saveFile(destination, completeJsResult(allConvertedJs))
                }
            } else {
                GsConsole.error('No files to be converted. *.groovy or *.java files not found.')
            }
        } catch (e) {
            throw new GrooScriptException("Convert Exception: ${e.message}")
        }
    }

    private static String completeJsResult(String result) {
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

    /**
     * Set a value to a conversion property
     * @param name
     * @param value
     */
    static setConversionProperty(String name, value) {
        if (!options) {
            options = defaultOptions
        }
        options[name] = value
    }

    /**
     * Resets all compiler options
     * @return
     */
    static clearAllOptions() {
        options = null
        debug = false
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

    static void joinListOfFiles(... params) {
        if (params.size() < 3) {
            GsConsole.error('Params are files to join and destination file')
            return
        }
        File destinationFile = new File(params.last())
        destinationFile.text = ''
        def filesToJoin = params - params.last()
        filesToJoin.each { sourceFile ->
            def file = new File(sourceFile)
            if (file.isFile()) {
                destinationFile.append(file.text + LINE_SEPARATOR)
            } else {
                GsConsole.error 'Error joining file ' + sourceFile
            }
        }
    }

    static Map getDefaultOptions() {
        ConversionOptions.values().inject([:]) { map, value ->
            map[value.text] = value.defaultValue
            map
        }
    }

    /**
     * Evaluate a piece of groovy code
     * @param code that will be evaluated using grooscript.min
     * @param comma separated js libs to add for evaluation
     * @return JsTestResult
     */
    static JsTestResult evaluateGroovyCode(String code, String jsLibs = null) {
        String jsCode = GrooScript.convert(code)

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

    static String getJsLibText(String nameJsLib) {
        GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + nameJsLib + '.js').text
    }

    static toGroovy(data) {
        data
    }

    static toJavascript(data) {
        data
    }

    static toJsObj(data) {
        data
    }

    static nativeJs(String code) {
        code
    }
}

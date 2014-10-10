package org.grooscript

import org.grooscript.convert.ConversionOptions

import static org.grooscript.util.Util.*

import org.grooscript.convert.GsConverter
import org.grooscript.util.GrooScriptException
import org.grooscript.daemon.ConversionDaemon
import org.grooscript.util.GsConsole
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
                files = checkConvertFile(source)
            } else if (source instanceof List) {
                source.each {
                    files = files + checkConvertFile(it)
                }
            } else {
                throw new GrooScriptException('Source must be a String or a list.')
            }
            convertFiles(files, destination)
        } else {
            throw new GrooScriptException('Have to define source and destination.')
        }
    }

    private static checkConvertFile(String source, List<File> files = []) {
        File fSource = new File(source)

        if (fSource.exists()) {
            if (fSource.isDirectory()) {
                fSource.eachFile { file ->
                    addFileIfValid(files, file)
                }
                if (options && options[ConversionOptions.RECURSIVE.text]) {
                    fSource.eachDir { File dir ->
                        files = checkConvertFile(dir.path, files)
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

    private static convertFiles(List<File> files, String destinationPath) {

        try {
            if (files) {
                boolean toOneFile = destinationPath && destinationPath.endsWith(JS_EXTENSION)
                File destination = new File(destinationPath)
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
            }
        } catch (e) {
            throw new GrooScriptException("Convert Exception: ${e.message}")
        }
    }

    private static String completeJsResult(String result) {
        if (options) {
            if (options[ConversionOptions.INITIAL_TEXT.text]) {
                result = options[ConversionOptions.INITIAL_TEXT.text] + '\n' + result
            }
            if (options[ConversionOptions.FINAL_TEXT.text]) {
                result = result + '\n' + options[ConversionOptions.FINAL_TEXT.text]
            }
            if (options[ConversionOptions.INCLUDE_JS_LIB.text]) {
                def file = GrooScript.classLoader.getResourceAsStream(
                        "META-INF/resources/${options[ConversionOptions.INCLUDE_JS_LIB.text]}.js")
                if (file) {
                    result = file.text + '\n' + result
                }
            }
        }
        result
    }

    private static void saveFile(File file, String content) {
        if (file.exists()) {
            file.delete()
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
     * Starts a daemon that check all time if files change, and try convert them
     * 1stTime runs, convert all source
     *
     * @param sourceList A list of folders and files to be converted
     * @param destinationFolder Folder where save .js files
     * @param conversionOptions Map of conversion options [classPath:['xxx/groovy','xxx.jar'], ...]
     * @param doAfter A closure to launch each time daemons ends and convert files. Param is a list of files modified
     * @param recursive convert folders recursively, default is false
     */
    static ConversionDaemon startConversionDaemon(sourceList, destinationFolder,
                                 conversionOptions = null, doAfter = null, recursive = false) {

        ConversionDaemon daemon = new ConversionDaemon()
        daemon.source = sourceList
        daemon.destinationFolder = destinationFolder
        daemon.conversionOptions = conversionOptions
        daemon.recursive = recursive
        daemon.doAfter = doAfter

        daemon.start()
        return daemon
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
                    newFile.append(file.text + LINE_JUMP)
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
                destinationFile.append(file.text + LINE_JUMP)
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
}

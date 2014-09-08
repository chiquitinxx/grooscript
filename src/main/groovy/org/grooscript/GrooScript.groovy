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
            return newConverter.toJs(text, options)
        }
        throw new GrooScriptException('Nothing to Convert.')
    }

    /**
     * Converts from a source to destination, groovy files to javascript files
     * Result files will be .js with same name that groovy file
     * @param source (String or List of String's) directories with groovy files, or groovy files
     * @param destination directory of .js files
     * @throws Exception something fails
     */
    static void convert(source, String destination) {
        if (source && destination) {
            if (source instanceof String || source instanceof GString) {
                checkConvertFile(source, destination)
            } else if (source instanceof List) {
                source.each {
                    checkConvertFile(it, destination)
                }
            } else {
                throw new GrooScriptException('Source must be a String or a list.')
            }
        } else {
            throw new GrooScriptException('Have to define source and destination.')
        }
    }

    private static checkConvertFile(String source, String destination) {
        File fSource = new File(source)
        File fDestination = new File(destination)

        if (fSource.exists() && fDestination.exists() && fDestination.isDirectory()) {
            if (fSource.isDirectory()) {
                fSource.eachFile { file ->
                    if (file.isFile()) {
                        fileConvert(file, fDestination)
                    }
                }
                if (options && options[ConversionOptions.RECURSIVE.text]) {
                    fSource.eachDir { File dir ->
                        convert(dir.path, destination)
                    }
                }
            } else {
                fileConvert(fSource, fDestination)
            }
        } else {
            throw new GrooScriptException('Source and destination must exist, and destination must be a directory.')
        }
    }

    private static fileConvert(File source, File destination) {
        if (debug) {
            GsConsole.debug("    Converting file ${source.absolutePath}...")
        }
        try {
            if (source.isFile() && source.name.endsWith(GROOVY_EXTENSION)) {
                //println 'Name file->'+source.name
                def name = source.name.split(/\./)[0]
                def jsResult = newConverter.toJs(source.text, options)

                //println 'Result file->'+destination.path+System.getProperty('file.separator')+name+'.js'
                def newFile = new File("${destination.path}$SEP$name$JS_EXTENSION")
                if (newFile.exists()) {
                    newFile.delete()
                }
                newFile.write(jsResult)
                if (debug) {
                    GsConsole.debug("    Result -> ${jsResult.size()}")
                    GsConsole.debug("***** Converted file: ${newFile.name} *****")
                }
            }
        } catch (e) {
            throw new GrooScriptException("Convert Exception: ${e.message}")
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

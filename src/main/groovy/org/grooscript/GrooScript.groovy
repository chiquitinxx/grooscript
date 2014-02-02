package org.grooscript

import static org.grooscript.util.Util.*

import org.grooscript.convert.GsConverter
import org.grooscript.util.GrooScriptException
import org.grooscript.daemon.ConversionDaemon
import org.grooscript.util.GsConsole
/**
 * JFL 09/11/12
 */
class GrooScript {

    static final String CLASSPATH_OPTION = 'classPath'
    static final String CONVERT_DEPENDENCIES_OPTION = 'convertDependencies'
    static final String CUSTOMIZATION_OPTION = 'customization'
    static final String MAIN_CONTEXT_SCOPE_OPTION = 'mainContextScope'
    static final String INITIAL_TEXT_OPTION = 'initialText'
    static final String FINAL_TEXT_OPTION = 'finalText'
    static final String RECURSIVE_OPTION = 'recursive'

    static boolean debug = false
    static Map options = [:]
    static boolean recursive = false

    /**
     * Get a new GsConverter with options applied
     * @return GsConverter
     */
    static GsConverter getNewConverter() {
        def converter = new GsConverter()
        options.each { key, value ->
            converter."${key}" = value
        }
        converter
    }

    /**
     * Convert a piece of groovy code to javascript
     * @param String text groovy code
     * @return String javascript result code
     * @throws Exception If conversion fails or text is null
     */
    static convert(String text) {
        if (text) {
            return getNewConverter().toJs(text)
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
    static convert(source, String destination) {
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
                if (recursive) {
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
                def jsResult = getNewConverter().toJs(source.text)

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
        if (name == RECURSIVE_OPTION) {
            recursive = value
        } else {
            options[name] = value
        }
    }

    /**
     * Resets all compiler options
     * @return
     */
    static clearAllOptions() {
        options = [:]
        recursive = false
        debug = false
    }

    //Only a deamon can be active
    static ConversionDaemon daemon

    /**
     * Starts a daemon that check all time if files change, and try convert them
     * 1stTime runs, convert all source
     *
     * @param sourceList A list of folders and files to be converted
     * @param destinationFolder Folder where save .js files
     * @param conversionOptions Map of conversion options [classPath:['xxx/groovy','xxx.jar'], addClassNames: true, ...]
     * @param doAfter A closure to launch each time daemons ends and convert files. Param is a list of files modified
     * @param recursive convert folders recursively, default is true
     */
    static startConversionDaemon(sourceList, destinationFolder,
                                 conversionOptions = null, doAfter = null, recursive = false) {
        if (daemon) {
            stopConversionDaemon()
        }
        daemon = new ConversionDaemon()
        daemon.source = sourceList
        daemon.destinationFolder = destinationFolder
        daemon.conversionOptions = conversionOptions
        daemon.recursive = recursive
        if (doAfter) {
            daemon.doAfter = doAfter
        }

        daemon.start()
    }

    /**
     * Stop the conversion daemon if active, waits until execution stopped
     */
    static stopConversionDaemon() {
        if (daemon) {
            daemon.stop()
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
                    newFile.append(file.text + LINE_JUMP)
                }
            }
        } else {
            GsConsole.error 'Source must be a directory'
        }
    }
}

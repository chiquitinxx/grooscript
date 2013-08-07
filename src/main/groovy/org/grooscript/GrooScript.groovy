package org.grooscript

import org.grooscript.daemon.ConversionDaemon

/**
 * JFL 09/11/12
 */
class GrooScript {

    static final String JS_TEMP_FILE = 'gSTempFile.js'
    def static ownClassPath
    def static debug = false
    def private static HEAD = '[GrooScript]'
    def static options = [:]

    /**
     * Get a new GsConverter with options applied
     * @return GsConverter
     */
    def static GsConverter getNewConverter() {
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
    def static convert(String text) {
        if (text) {
            return getNewConverter().toJs(text,ownClassPath)
        } else {
            throw new Exception("Nothing to Convert.")
        }
    }

    /**
     * Converts from a source to destination, groovy files to javascript files
     * Result files will be .js with same name that groovy file
     * @param source path to directory with groovy files, or a groovy file path. Not recursive
     * @param destination directory of .js files
     * @throws Exception something fails
     */
    def static convert(String source, String destination) {
        if (source && destination) {
            File fSource = new File(source)
            if (debug) {
                println "${HEAD} Source file: ${fSource.absolutePath}"
            }
            File fDestination = new File(destination)
            if (debug) {
                println "${HEAD} Destination file: ${fDestination.absolutePath}"
            }

            if (fSource.exists() && fDestination.exists() && fDestination.isDirectory()) {
                if (!fSource.isDirectory()) {
                    fileConvert(fSource,fDestination)
                } else {
                    fSource.eachFile { file ->
                        if (file.isFile()) {
                            fileConvert(file,fDestination)
                        }
                    }
                }
            } else {
                throw new Exception("Source and destination must exist, and destination must be a directory.")
            }

        } else {
            throw new Exception("Have to define source and destination.")
        }
    }

    /**
     * Set the dir where all your groovy starts, the mainSource ( src/main/groovy, src/groovy, ..)
     * @param dir String or List
     * @return
     */
    def static setOwnClassPath(dir) {
        ownClassPath = dir
    }

    def private static fileConvert(File source,File destination) {
        if (debug) {
            println "${HEAD}    Converting..."
        }
        try {
            if (source.isFile() && source.name.endsWith('.groovy')) {
                //println 'Name file->'+source.name
                def name = source.name.split(/\./)[0]
                def jsResult = getNewConverter().toJs(source.text,ownClassPath)

                //println 'Result file->'+destination.path+System.getProperty('file.separator')+name+'.js'
                def newFile = new File(destination.path+System.getProperty('file.separator')+name+'.js')
                if (newFile.exists()) {
                    newFile.delete()
                }
                newFile.write(jsResult)
                if (debug) {
                    println "${HEAD}    Result -> ${jsResult.size()}"
                    println '***** Converted file: '+newFile.name + ' *****'
                }
            }
        } catch (e) {
            println "${HEAD} Convert Exception: "+e.message
            throw e
        }
    }

    /**
     * Set a value to a conversion property
     * @param name
     * @param value
     */
    def static setConversionProperty(name,value) {
        options."$name" = value
    }

    /**
     * Resets all compiler options
     * @return
     */
    def static clearAllOptions() {
        options = [:]
        ownClassPath = null
    }

    //Only a deamon can be active
    def static ConversionDaemon daemon

    /**
     * Starts a daemon that check all time if files change, and try convert them
     * 1stTime runs, convert all sourceList
     *
     * @param sourceList A list of folders and files to be converted
     * @param destinationFolder Folder where save .js files
     * @param conversionOptions Map of conversion options [classpath:['xxx/groovy','xxx.jar'], addClassNames: true, ...]
     * @param doAfter A closure to launch each time daemons ends check and convert files. Recieve a list of files modified
     */
    def static startConversionDaemon(sourceList,destinationFolder,conversionOptions = null,doAfter = null) {
        if (daemon) {
            stopConversionDaemon()
        }
        daemon = new ConversionDaemon()
        daemon.sourceList = sourceList
        daemon.destinationFolder = destinationFolder
        daemon.conversionOptions = conversionOptions
        if (doAfter) {
            daemon.doAfter = doAfter
        }

        daemon.start()
    }

    /**
     * Stop the conversion daemon if active, waits until execution stopped
     */
    def static stopConversionDaemon() {
        if (daemon) {
            daemon.stop()
        }
    }
}

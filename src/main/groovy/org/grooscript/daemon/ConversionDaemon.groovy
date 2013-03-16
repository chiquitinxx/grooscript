package org.grooscript.daemon

import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.dataflow.operator.PoisonPill
import org.grooscript.GsConverter
import org.grooscript.util.GsConsole
import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.dataflow.Dataflow.operator
import static groovyx.gpars.dataflow.Dataflow.task

/**
 * User: jorgefrancoleza
 * Date: 21/02/13
 */
class ConversionDaemon {

    def static final REST_TIME = 500

    def sourceList
    def destinationFolder
    def options = [:]
    def doAfter = null

    def dates = [:]

    def continueExecution = false
    def actualTask

    /**
     * Start the daemon
     * @return
     */
    def start() {
        if (sourceList && destinationFolder) {
            continueExecution = true
            actualTask = task {
                while (continueExecution) {
                    def list = work()
                    if (doAfter) {
                        doAfter(list)
                    }
                    sleep(REST_TIME)
                }
            }
            GsConsole.message('Daemon Started.')
        } else {
            GsConsole.error('Daemon need sourceList and destinationFolder to run.')
        }
    }

    /**
     * Stop the daemon if active
     * @return
     */
    def stop() {
        if (actualTask) {
            continueExecution = false
            actualTask.join()
            GsConsole.message('Daemon Terminated.')
        }
    }

    //Convert a groovy file to javascript and save in destinationFolder
    def convertFile(absolutePath) {

        def source = new File(absolutePath)
        if (source && source.isFile() && source.name.endsWith('.groovy')) {
            //Get name of file
            def name = source.name.split(/\./)[0]
            //Get a converter
            def converter = new GsConverter()

            //Set the options
            def classpath = null
            if (options) {
                options.each { String key,value ->
                    if (key.toUpperCase().contains('CLASSPATH')) {
                        classpath = value
                    } else {
                        converter."${key}" = value
                    }
                }
            }

            //Do conversion
            def jsResult = converter.toJs(source.text,classpath)

            //Save the js file
            def newFile = new File(destinationFolder+System.getProperty('file.separator')+name+'.js')
            if (newFile.exists()) {
                newFile.delete()
            }
            newFile.write(jsResult)
        } else {
            throw new Exception('Error in daemon with file '+absolutePath)
        }
    }

    def work() {

        final def works = new DataflowQueue()
        final def exit = new DataflowVariable()
        def listConverteds = []

        final op = operator(inputs: [works], outputs: [exit], maxForks: 3) { absolutePath ->

            try {
                convertFile(absolutePath)
                listConverteds << absolutePath
            } catch (e) {
                GsConsole.error("Daemon, error processing file (${absolutePath})\n   ${e.message}")
            }

        }

        //Check if lastModified of file changed
        def checkFile = { File file ->
            def change
            if (dates."${file.absolutePath}") {
                change = !(dates."${file.absolutePath}"==file.lastModified())
            } else {
                change = true
            }
            if (change) {
                //println '  File changed: '+file.absolutePath
                //Send the work of convert the file
                works << file.absolutePath
                dates."${file.absolutePath}" = file.lastModified()
            }
        }

        //Check all files and all files in dirs
        withPool {
            sourceList.eachParallel { name ->
                def file = new File(name)
                if (file && (file.isDirectory() || file.isFile())) {
                    if (file.isDirectory()) {

                        file.eachFile { File item ->
                            if (item.isFile()) {
                                checkFile(item)
                            }
                        }

                    } else {
                        checkFile(file)
                    }
                } else {
                    def message = 'Daemon Error in file/folder '+name
                    GsConsole.error message
                    throw new Exception(message)
                }
            }
        }

        //Send poison pill to finish the flow
        works << PoisonPill.instance

        //Wait flow ends
        op.join()

        //List of converted files
        return listConverteds
    }
}

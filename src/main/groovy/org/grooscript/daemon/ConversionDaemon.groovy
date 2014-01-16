package org.grooscript.daemon

import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.dataflow.Dataflow.operator
import static groovyx.gpars.dataflow.Dataflow.task

import org.grooscript.util.GrooScriptException
import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.dataflow.operator.PoisonPill
import org.grooscript.convert.GsConverter
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 21/02/13
 */
class ConversionDaemon {

    static final REST_TIME = 500

    def sourceList
    def destinationFolder
    def conversionOptions = [:]
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
            }.then( {
                GsConsole.message('Daemon Terminated.')
            }) { e ->
                GsConsole.error('Daemon finished cause error: ' + e.message)
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

            //Set the conversion options
            if (conversionOptions) {
                conversionOptions.each { String key,value ->
                    converter."${key}" = value
                }
            }

            //Do conversion
            def jsResult = converter.toJs(source.text)

            //Save the js file
            def newFile = new File(destinationFolder + System.getProperty('file.separator') + name + '.js')
            if (newFile.exists()) {
                newFile.delete()
            }
            newFile.write(jsResult)
        } else {
            throw new GrooScriptException("Error in daemon with file $absolutePath")
        }
    }

    def work() {

        final DataflowQueue works = new DataflowQueue()
        final DataflowVariable exit = new DataflowVariable()
        def listConverteds = []

        final workOperator = operator(inputs: [works], outputs: [exit], maxForks: 3) { absolutePath ->

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
                change = !(dates."${file.absolutePath}" == file.lastModified())
            } else {
                change = true
            }
            if (change) {
                //Send the work of convert the file
                works << file.absolutePath
                dates."${file.absolutePath}" = file.lastModified()
            }
        }

        //Recursive by default
        def checkPath = { File fileToCheck ->
            if (fileToCheck.isDirectory()) {

                fileToCheck.eachFile { File item ->
                    if (item.isFile()) {
                        checkFile(item)
                    }
                }
                fileToCheck.eachDir { dir ->
                    checkPath(dir)
                }
            } else {
                checkFile(fileToCheck)
            }
        }

        //Check all files and all files in dirs
        withPool {
            sourceList.eachParallel { name ->
                def file = new File(name)
                if (file && (file.isDirectory() || file.isFile())) {
                    checkPath(file)
                } else {
                    throw new GrooScriptException("Daemon Error in file/folder $name")
                }
            }
        }

        //Send poison pill to finish the flow
        works << PoisonPill.instance

        //Wait flow ends
        workOperator.join()

        //Return list of converted files
        listConverteds
    }
}

package org.grooscript.daemon

import groovyx.gpars.actor.DefaultActor
import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.DataflowVariable
import groovyx.gpars.dataflow.operator.PoisonPill
import org.grooscript.convert.GsConverter
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static groovyx.gpars.GParsPool.withPool
import static groovyx.gpars.dataflow.Dataflow.operator

/**
 * User: jorgefrancoleza
 * Date: 04/02/14
 */
class ConvertActor extends DefaultActor {

    static final FINISH = 'finish'

    private static final REST_TIME = 500
    private static final SEPARATOR = System.getProperty('file.separator')

    def source
    String destinationFolder
    Map conversionOptions = [:]
    def doAfter = null
    boolean recursive = false

    private DataflowQueue works
    private Map dates = [:]

    void act() {
        loop {
            react { source ->
                if (source == FINISH) {
                    finishIt()
                } else {
                    def list = work()
                    if (doAfter) {
                        doAfter(list)
                    }
                    sleep(REST_TIME)
                    this << source
                }
            }
        }
    }

    public void onInterrupt(InterruptedException e) {
        GsConsole.error('InterruptedException in daemon: ' + e.message)
        finishIt()
    }

    public void onException(Throwable e) {
        GsConsole.error('Exception in daemon: ' + e.message)
        finishIt()
    }

    private finishIt() {
        GsConsole.message('Daemon Finished.')
        terminate()
    }

    def work() {

        works = new DataflowQueue()
        final DataflowVariable exit = new DataflowVariable()
        def listConverteds = []

        final workOperator = operator(inputs: [works], outputs: [exit], maxForks: 3) { absolutePath ->

            try {
                convertFile(absolutePath)
                listConverteds << absolutePath
            } catch (e) {
                GsConsole.error("Daemon, error converting file (${absolutePath})\n   ${e.message}")
            }
        }

        //Check all files and all files in dirs
        withPool {
            if (!(source instanceof List)) {
                source = [source]
            }
            source.eachParallel { name ->
                File file = new File(name)
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

    //Convert a groovy file to javascript and save in destinationFolder
    private convertFile(absolutePath) {
        //TODO call to a simple convert 1 file
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
            def newFile = new File(destinationFolder + SEPARATOR + name + '.js')
            if (newFile.exists()) {
                newFile.delete()
            }
            newFile.write(jsResult)
        } else {
            throw new GrooScriptException("Error in daemon with file $absolutePath")
        }
    }

    //Recursive by default
    private checkPath = { File fileToCheck ->
        if (fileToCheck.isDirectory()) {

            fileToCheck.eachFile { File item ->
                if (item.isFile()) {
                    checkFile(item)
                }
            }
            if (recursive) {
                fileToCheck.eachDir { File dir ->
                    checkPath(dir)
                }
            }
        } else {
            checkFile(fileToCheck)
        }
    }

    //Check if lastModified of file changed
    private checkFile = { File file ->
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
}

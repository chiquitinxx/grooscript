package org.grooscript.daemon

import groovyx.gpars.actor.DefaultActor
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 16/11/14
 */
class FilesActor extends DefaultActor {

    static final FINISH = 'finish'

    Closure action
    long restTime = 200
    boolean recursive = false

    private List<String> filesChanged
    private Map dates = [:]

    void act() {
        loop {
            react { source ->
                if (source == FINISH) {
                    finishIt()
                } else {
                    resetFilesChanged()
                    detectFileChanges(source)
                    if (filesChanged && action) {
                        try {
                            action(filesChanged)
                        } catch (Throwable e) {
                            GsConsole.error("Exception executing action in files (${filesChanged}): ${e.message}")
                        }
                    }
                    sleep(restTime)
                    this << source
                }
            }
        }
    }

    public void onInterrupt(InterruptedException e) {
        GsConsole.error('InterruptedException in FilesActor: ' + e.message)
        finishIt()
    }

    public void onException(Throwable e) {
        GsConsole.exception('Exception in FilesActor: ' + e.message)
        finishIt()
    }

    private finishIt() {
        GsConsole.message('FilesActor Finished.')
        terminate()
    }

    private void detectFileChanges(List files) {
        //Check all files and all files in dirs
        files.each { name ->
            File file = new File(name)
            if (file && (file.isDirectory() || file.isFile())) {
                checkFolder(file)
            } else {
                throw new GrooScriptException("FilesActor Error in file/folder $name")
            }
        }
    }

    private checkFolder = { File fileToCheck ->
        if (fileToCheck.isDirectory()) {

            fileToCheck.eachFile { File item ->
                if (item.isFile()) {
                    checkFile(item)
                }
            }

            if (recursive) {
                fileToCheck.eachDir { File dir ->
                    checkFolder(dir)
                }
            }
        } else {
            checkFile(fileToCheck)
        }
    }

    //Check if lastModified of file changed
    private checkFile = { File file ->
        def change = false
        def lastModificationDate = file.lastModified()
        if (dates."${file.absolutePath}") {
            change = !(dates."${file.absolutePath}" == lastModificationDate)
        }
        dates."${file.absolutePath}" = lastModificationDate
        if (change) {
            addFileChanged(file.path)
        }
    }

    private resetFilesChanged() {
        filesChanged = []
    }

    private addFileChanged(String fileName) {
        filesChanged << fileName
    }
}

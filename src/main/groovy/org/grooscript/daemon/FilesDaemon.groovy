package org.grooscript.daemon

import org.grooscript.util.GsConsole

import static groovyx.gpars.dataflow.Dataflow.task

/**
 * Created by jorgefrancoleza on 16/11/14.
 */
class FilesDaemon {

    private static final WAIT_TIME = 200
    final List<String> files
    final Closure action
    Map options = [
        time: WAIT_TIME,
        actionOnStartup: false,
        recursive: false
    ]
    FilesActor actor

    FilesDaemon(List<String> files, Closure action, Map options = [:]) {
        this.files = files
        this.action = action
        options.each { key, value ->
            this.options[key] = value
        }
    }

    void start() {
        if (options.actionOnStartup == true) {
            try {
                action files
            } catch (e) {
                GsConsole.error("Error executing action at start in files (${files}): ${e.message}")
            }
        }
        task {
            actor = new FilesActor(action: action, restTime: options.time).start()
            actor << files
        }
        GsConsole.message('Listening file changes in : ' + files)
    }

    void stop() {
        if (actor && actor.isActive()) {
            actor << FilesActor.FINISH
        }
    }
}

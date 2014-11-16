package org.grooscript.daemon

import org.grooscript.util.GsConsole

import static groovyx.gpars.dataflow.Dataflow.task

/**
 * Created by jorgefrancoleza on 16/11/14.
 */
class FilesDaemon {

    List<String> files
    Closure action
    Map options = [
        time: 400,
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
            action files
        }
        task {
            actor = new FilesActor(action: action, restTime: options.time).start()
            actor << files
        }
        GsConsole.message('Listening file changes in : '+files)
    }

    void stop() {
        if (actor && actor.isActive()) {
            actor << FilesActor.FINISH
        }
    }
}

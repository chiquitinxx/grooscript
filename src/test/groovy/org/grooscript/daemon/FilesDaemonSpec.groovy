package org.grooscript.daemon

import org.grooscript.util.GsConsole
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * Created by jorgefrancoleza on 16/11/14.
 */
class FilesDaemonSpec extends Specification {

    void 'initialize the daemon'() {
        given:
        def daemon = new FilesDaemon(files, ACTION)

        expect:
        daemon.action.is ACTION
        daemon.files == files
        daemon.options == [
            actionOnStartup: false,
            time: 400,
            recursive: false
        ]
    }

    void 'initialize the daemon with other options'() {
        given:
        def daemon = new FilesDaemon(files, ACTION, NEW_OPTIONS)

        expect:
        daemon.options == NEW_OPTIONS
    }

    void 'if starts with actions on start up, run them'() {
        given:
        def actionExecutions = 0
        def daemon = new FilesDaemon(files, { List<String> changingFiles ->
            assert files == changingFiles
            actionExecutions++
        }, NEW_OPTIONS)

        when:
        daemon.start()
        daemon.stop()

        then:
        actionExecutions == 1
    }

    void 'show error if exception in action at start, and continue execution'() {
        given:
        def conditions = new PollingConditions(initialDelay: 0.3)
        GroovySpy(GsConsole, global:true)
        def daemon = new FilesDaemon(files, {
            throw new Exception('error')
        }, [actionOnStartup: true])

        when:
        daemon.start()

        then:
        1 * GsConsole.error("Error executing action at start in files ([${tempFile.path}]): error")
        conditions.eventually {
            daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    void 'show error if exception in action during execution, and continue execution'() {
        given:
        def conditions = new PollingConditions(timeout: 2, initialDelay: 0.3)
        boolean fileError = false
        GsConsole.error("Error executing action in files ([${tempFile.path}]): error") >> { fileError = true }
        GroovySpy(GsConsole, global:true)
        def daemon = new FilesDaemon(files, { List<String> files ->
            throw new Exception('error')
        }, [actionOnStartup: false])

        when:
        daemon.start()
        modifyFile()

        then:
        conditions.eventually {
            fileError
            daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    void 'starts the actor and detects change of file'() {
        given:
        def conditions = new PollingConditions(timeout: 2, initialDelay: 0.3)
        def actionExecutions = 0
        def daemon = new FilesDaemon(files, { List<String> changingFiles ->
            println 'Executing action.'
            assert changingFiles == files
            actionExecutions++
        }, [time: 100])

        when:
        daemon.start()
        modifyFile()

        then:
        conditions.eventually {
            actionExecutions == 1
            daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    private File tempFile
    private List files

    private static final ACTION = { files -> files }
    private static final NEW_OPTIONS = [
        actionOnStartup: true,
        time: 200,
        recursive: true
    ]
    private static final FILE1_NAME = 'File1'

    private modifyFile() {
        tempFile.text = 'pepe'
    }

    def setup() {
        tempFile = File.createTempFile(FILE1_NAME, 'groovy')
        tempFile.text = 'class File1 {}'
        files = [tempFile.path]
    }

    def cleanup() {
        tempFile.delete()
    }
}

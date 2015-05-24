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
            time: 200,
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
        def daemon = new FilesDaemon(files, { list ->
            assert list == [tempFile.path]
            throw new Exception('error')
        }, [actionOnStartup: true])

        when:
        daemon.start()

        then:
        1 * GsConsole.error("Error executing action at start in files ([${tempFile.path}]): error")
        conditions.eventually {
            assert daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    void 'change detected and continue execution'() {
        given:
        def changed = false
        def daemon = new FilesDaemon(files, { List<String> files ->
            assert files == [tempFile.path]
            changed = true
            throw new Throwable('error')
        }, [actionOnStartup: false])

        when:
        daemon.start()
        sleep(1000)
        def conditions = new PollingConditions(timeout: 2)
        modifyFile()

        then:
        conditions.eventually {
            assert changed
            assert daemon.actor.isActive()
        }

        cleanup:
        daemon.stop()
    }

    private File tempFile
    private List files

    private static final ACTION = { files -> files }
    private static final NEW_OPTIONS = [
        actionOnStartup: true,
        time: 300,
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

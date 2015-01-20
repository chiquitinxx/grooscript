package org.grooscript.daemon

import org.grooscript.util.GsConsole
import spock.lang.Specification

/**
 * Created by jorgefrancoleza on 16/11/14.
 */
class FilesDaemonSpec extends Specification {

    void 'initialize the daemon'() {
        given:
        def daemon = new FilesDaemon(FILES, ACTION)

        expect:
        daemon.action == ACTION
        daemon.files == FILES
        daemon.options == [
            actionOnStartup: false,
            time: 400,
            recursive: false
        ]
    }

    void 'initialize the daemon with other options'() {
        given:
        def daemon = new FilesDaemon(FILES, ACTION, NEW_OPTIONS)

        expect:
        daemon.options == NEW_OPTIONS
    }

    void 'if starts with actions on start up, run them'() {
        given:
        def actionExecutions = 0
        def daemon = new FilesDaemon(FILES, { List<String> files ->
            assert files == files
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
        GroovySpy(GsConsole, global:true)
        def daemon = new FilesDaemon(FILES, { List<String> files ->
            throw new Exception('error')
        }, [actionOnStartup: true])

        when:
        daemon.start()
        waitTime(1000)

        then:
        1 * GsConsole.error('Error executing action at start in files ([File1.groovy]): error')
        daemon.actor.isActive()

        cleanup:
        daemon.stop()
    }

    void 'show error if exception in action during execution, and continue execution'() {
        given:
        GroovySpy(GsConsole, global:true)
        def daemon = new FilesDaemon(FILES, { List<String> files ->
            throw new Exception('error')
        }, [actionOnStartup: false])

        when:
        daemon.start()
        waitTime(1000)
        modifyFile()
        waitTime(1000)

        then:
        1 * GsConsole.error('Error executing action in files ([File1.groovy]): error')
        daemon.actor.isActive()

        cleanup:
        daemon.stop()
    }

    void 'starts the actor and detects change of file'() {
        given:
        def actionExecutions = 0
        def daemon = new FilesDaemon(FILES, { List<String> files ->
            println 'Executing action.'
            assert files == FILES
            actionExecutions++
        }, [time: 100])

        when:
        daemon.start()
        waitTime(1000)
        modifyFile()
        waitTime(1000)

        then:
        actionExecutions == 1
        daemon.actor.isActive()

        cleanup:
        daemon.stop()
    }

    private static final ACTION = { files -> files }
    private static final NEW_OPTIONS = [
        actionOnStartup: true,
        time: 200,
        recursive: true
    ]
    private static final FILE1_PATH = 'File1.groovy'
    private static final FILES = [FILE1_PATH]

    private waitTime(time) {
        Thread.sleep time
    }

    private modifyFile() {
        new File(FILE1_PATH) << 'pepe'
    }

    def setup() {
        new File(FILE1_PATH) << 'class File1 {}'
    }

    def cleanup() {
        new File(FILE1_PATH).delete()
    }
}

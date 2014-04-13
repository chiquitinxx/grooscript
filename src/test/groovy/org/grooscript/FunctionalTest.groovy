package org.grooscript

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.grooscript.asts.PhantomJsTest
import org.grooscript.asts.PhantomJsTestImpl

/**
 * User: jorgefrancoleza
 * Date: 15/08/13
 */
abstract class FunctionalTest extends GroovyTestCase {

    private static final PHANTOMJS_HOME = System.getProperty('PHANTOMJS_HOME') ?: '/Applications/phantomjs'
    private static final JS_LIBRARIES_PATH = 'src/main/resources/META-INF/resources'
    private static final PORT = 8000
    private static final ACTION = '/test'
    protected static final String FULL_ADRESS = "http://localhost:${PORT}${ACTION}"

    private HttpServer server

    abstract String testResponse()

    class MyHandler implements HttpHandler {
        String response
        MyHandler(String response) {
            this.response = response
        }
        public void handle(HttpExchange t) throws IOException {
            t.sendResponseHeaders(200, response.length())
            //new File('try.html').text = response
            OutputStream os = t.getResponseBody()
            os.write(response.getBytes())
            os.close()
        }
    }

    private startServer() {
        server = HttpServer.create(new InetSocketAddress(PORT), 0)
        server.createContext(ACTION, new MyHandler(testResponse()))
        server.setExecutor(null) // creates a default executor
        server.start()
        println 'Server started, responds to: ' + FULL_ADRESS
    }

    private stopServer() {
        println 'Closing server...'
        server.stop(0)
        println 'Server closed.'
    }

    void setUp() {
        System.setProperty('PHANTOMJS_HOME',PHANTOMJS_HOME)
        System.setProperty('JS_LIBRARIES_PATH',JS_LIBRARIES_PATH)
        startServer()
    }

    void tearDown() {
        stopServer()
    }
}

package org.grooscript

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

/**
 * User: jorgefrancoleza
 * Date: 15/08/13
 */
abstract class FunctionalTest extends GroovyTestCase {

    private static final PHANTOMJS_HOME = './node_modules/phantomjs'
    private static final JS_LIBRARIES_PATH = 'src/main/resources/META-INF/resources'
    private static final PORT = 8000
    private static final HTML_ACTION = '/test'
    private static final JSON_ACTION = '/json'
    protected static final String HTML_ADRESS = "http://localhost:${PORT}${HTML_ACTION}"
    protected static final String JSON_ADRESS = "http://localhost:${PORT}${JSON_ACTION}"

    private HttpServer server

    abstract String htmlResponse()

    class TestActionHandler implements HttpHandler {
        String response

        TestActionHandler(String response) {
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
        server.createContext(HTML_ACTION, new TestActionHandler(htmlResponse()))
        server.createContext(JSON_ACTION, new JsonActionHandler())
        server.setExecutor(null) // creates a default executor
        server.start()
        println 'Server started, responds to: ' + HTML_ADRESS
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

    protected script(text) {
        "<script>${text}</script>\n"
    }

    protected jsFileText(fileName) {
        GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
    }

    class JsonActionHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            def response = '{"result": true, "number": 5, "name": "George"}'
            t.sendResponseHeaders(200, response.length())
            t.getResponseHeaders().add('Content-Type', 'application/json')
            OutputStream os = t.getResponseBody()
            os.write(response.getBytes())
            os.close()
        }
    }

    protected getJsonResultClass() {
        '''
        class Result {
            boolean result
            Integer number
            String name
        }
        '''
    }
}

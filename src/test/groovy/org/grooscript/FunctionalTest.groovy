/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.isWindows

abstract class FunctionalTest extends GroovyTestCase {

    static final String HTML_ACTION = '/test'
    static final String JSON_ACTION = '/json'
    /**
     * NOTE: due a Groovy characteristic, the following URLs must be set manually to match the previous parameters.
     * Otherwise they cannot be references in annotations.
     * see: http://jira.codehaus.org/browse/GROOVY-3278
     */
    public static final String HTML_ADDRESS = 'http://localhost:9090/test'
    public static final String JSON_ADDRESS = 'http://localhost:9090/json'

    private static final String PHANTOMJS_HOME = ".${SEP}node_modules${SEP}phantomjs"
    private static final String PHANTOMJS_WINDOWS_HOME = "$PHANTOMJS_HOME${SEP}lib${SEP}phantom"
    private static final String JS_LIBRARIES_PATH = "src/main/resources/META-INF/resources"
    static final int PORT = 9090

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
        println 'Server started, responds to: ' + HTML_ADDRESS
    }

    private stopServer() {
        println 'Closing server...'
        server.stop(isWindows()? 10 : 0)
        println 'Server closed.'
    }

    void setUp() {
        System.setProperty('PHANTOMJS_HOME', isWindows()? PHANTOMJS_WINDOWS_HOME : PHANTOMJS_HOME)
        System.setProperty('JS_LIBRARIES_PATH', JS_LIBRARIES_PATH)
        startServer()
    }

    void tearDown() {
        stopServer()
    }

    protected script(text) {
        "<script>${text}</script>\n"
    }

    protected jsFileText(fileName) {
        new File(JS_LIBRARIES_PATH + '/' + fileName).text
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

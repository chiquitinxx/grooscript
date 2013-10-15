package org.grooscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.GrooScript
import org.grooscript.GsConverter

import static org.grooscript.util.GsConsole.*

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class PhantomJsTestImpl implements ASTTransformation {

    static final MAX_TIME_WAITING = 10000L
    static final HEAD = '[PhantomJs Test]'
    static final CONSOLE = '  [Console]'

    static final phantomJsText = '''
var page = require('webpage').create();

page.onConsoleMessage = function(msg) {
    console.log('CONSOLE: ' + msg);
};

page.onError = function(msg, trace) {
    console.error('ONERROR: ' + msg);
};

function evaluateAfterSeconds(seconds) {
    setTimeout(function() {
        var result = evaluateTest();
        console.log('Number of tests: '+result.number);
        if (result.number > 0) {
            var i;
            for (i=0;i<result.tests.length;i++) {
                console.log('Test ('+i+') Result:'+(result.tests[i].result==true?'OK':'FAIL')+
                    ' Desc:'+result.tests[i].text);
            }
        } else {
            console.log('0 tests done.');
        }
        {{CAPTURE}}
        phantom.exit();
    }, seconds * 1000);
};

function evaluateTest() {

    return page.evaluate(function() {

        var gSresult = { number:0 , tests: [], console: ''};
        function gSassert(value, text) {
            var test = { result: value, text: value.toString()};
            if (arguments.length == 2 && arguments[1]!=null && arguments[1]!=undefined) {
                test.text = text;
            }
            gSresult.tests[gSresult.number++] = test;
        };

        function gSprintln(value) {
            console.log(value);
        };

        function grooKimbo(selector,other) {
            var result = window.Kimbo(selector,other);
            result.size = function() {
                return this.length;
            }
            return result;
        }
        window.$ = grooKimbo;

        {{GROOSCRIPT}}

        try {
            {{FUNCTION_CALL}}
        } catch (e) {
            var message = 'ERROR EXECUTING CODE: ' + e;
            console.log (message);
            var test = { result: false, text: message};
            gSresult.tests[gSresult.number++] = test;
        };

        return gSresult;
    });
};

page.open('{{URL}}', function (status) {
    if (status !== 'success') {
        console.log('  Result:FAIL Desc:Fail loading url..');
        phantom.exit(1);
    } else {
        page.libraryPath = '{{LIBRARY_PATH}}'
        if (page.injectJs('kimbo.min.js') && page.injectJs('grooscript.js')) {
            //console.log('Evaluating code...');
            evaluateAfterSeconds({{SECONDS}});
        } else {
            console.log(' Result:FAIL Desc:Fail in inject.');
            phantom.exit(1);
        }
    }
});'''

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Start
        if (!nodes[0] instanceof AnnotationNode ||
            !nodes[1] instanceof MethodNode) {
            return
        }

        MethodNode method = (MethodNode) nodes[1]
        //Statement testCode = method.getCode()

        def messageError
        AnnotationNode node = (AnnotationNode) nodes[0]
        String url = node.getMember('url').text
        String capture = node.getMember('capture')?.text
        int waitSeconds = node.getMember('waitSeconds') ? node.getMember('waitSeconds').value : 0
        String finalText = ''

        if (!url) {
            messageError = 'url not defined, use @PhantomJsTest(url=\'http://grooscript.org\')'
        }
        if (!messageError) {
            GsConverter converter = new GsConverter()

            def jsTest
            try {
                jsTest = converter.processAstListToJs([method])
            } catch (e) {
                messageError = 'Error converting code ->'+e.message
            }

            if (!messageError) {
                finalText = phantomJsText
                finalText = finalText.replace('{{URL}}', url)
                finalText = finalText.replace('{{GROOSCRIPT}}', jsTest)
                finalText = finalText.replace('{{SECONDS}}', waitSeconds as String)

                if (capture) {
                    finalText = finalText.replace('{{CAPTURE}}', "console.log('Capturing...');page.render('$capture');\n")
                } else {
                    finalText = finalText.replace('{{CAPTURE}}', '')
                }
            }
        }
        //(org.grooscript.asts.PhantomJsTestImpl).
        def textCode = "def listParams = [];\n${method.parameters.collect { "listParams << ${it.name}"}.join(';')};\n" +
                "Class.forName('org.grooscript.asts.PhantomJsTestImpl').doPhantomJsTest('${url}', " +
                "\'\'\'"+ finalText +"\'\'\', '${method.name?:''}', listParams, " +
                "'${messageError?:''}'); return null"
        method.declaringClass
        method.setCode new AstBuilder().buildFromString(CompilePhase.CLASS_GENERATION , textCode)
    }

    static String getJsLibrariesPath() {

        String jsHome = System.getProperty('JS_LIBRARIES_PATH')
        if (!System.getProperty('JS_LIBRARIES_PATH')) {

            try {
                def userHome = System.getProperty('user.home')

                if (userHome) {
                    def version = Class.forName('org.grooscript.GsConverter').package.implementationVersion

                    def path = userHome + File.separator + '.grooscript' + (version ? File.separator + version : '')
                    def folder = new File(path)
                    if (folder.exists()) {
                        message 'Using js local files in ' + path, HEAD
                    } else {
                        folder.mkdirs()
                        ['grooscript.js','kimbo.min.js'].each { fileName ->
                            new File(path + File.separator + fileName).text =
                                GrooScript.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
                        }
                    }
                    jsHome = path
                } else {
                    error "Error looking for js files: missing System.getProperty('user.home')", HEAD
                }
            } catch (e) {
                exception "Error looking for js files: ${e.message}", HEAD
            }
            if (!jsHome) {
                assert false, 'Need define property JS_LIBRARIES_PATH, folder with grooscript.js and kimbo.min.js'
            }
        }
        return jsHome
    }

    static void doPhantomJsTest(String url, String testCode, String methodName,
                                List parameters = null, String messageError = null) {
        def nameFile = 'phantomjs.js'
        try {
            if (messageError) {
                assert false, 'PhantomJs Initial Error: ' + messageError
            }
            def sysOp = System.getProperty("os.name")
            def jsHome = org.grooscript.asts.PhantomJsTestImpl.getJsLibrariesPath()
            def phantomJsHome
            if (!System.getProperty('PHANTOMJS_HOME') && !System.getenv('PHANTOMJS_HOME')) {
                assert false, 'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder'
            } else {
                phantomJsHome = System.getProperty('PHANTOMJS_HOME') ?: System.getenv('PHANTOMJS_HOME')
            }
            message "Starting Test in ${url}", HEAD

            //Save the file
            def finalText = testCode

            if (sysOp && sysOp.toUpperCase().contains('WINDOWS')) {
                jsHome = jsHome.replace("\\",'/')
                jsHome = (jsHome.indexOf(':') == 1 ? jsHome.substring(2) : jsHome)
            }

            finalText = finalText.replace('{{LIBRARY_PATH}}', jsHome)
            finalText = finalText.replace('{{FUNCTION_CALL}}', "${methodName}(${getParametersText(parameters)});")
            new File(nameFile).text = finalText

            //println '*********************** FINAL '
            //println finalText
            //println '*********************** FINAL '

            //Execute PhantomJs
            String command = phantomJsHome
            if (sysOp && sysOp.toUpperCase().contains('WINDOWS')) {
                command += "${File.separator}phantomjs.exe " + nameFile
            } else {
                command += "${File.separator}bin${File.separator}phantomjs " + nameFile
            }
            def proc = command.execute()
            proc.waitForOrKill(MAX_TIME_WAITING)
            def exit = proc.in.text
            if (!exit) {
                error 'Error executing command: '+command, HEAD
                message '  return code: ' + proc.exitValue() + ' stderr: ' + proc.err.text, HEAD
                assert false, 'Error launching PhantomJs'
            } else {
                exit.eachLine { String line->
                    if (line.contains('Result:FAIL')) {
                        assert false,line.substring(line.indexOf(' Desc:')+6)
                    } else if (line.toUpperCase().startsWith('CONSOLE:')) {
                        message line.substring(8), CONSOLE
                    } else if (line.contains('Result:OK')) {
                        assert true,line.substring(line.indexOf(' Desc:')+6)
                    } else {
                        message line, HEAD
                    }
                }
            }
            message "Result \u001B[91mSUCCESS\u001B[0m.", HEAD
        } catch (AssertionError ae) {
            message "Assert error. \u001B[91mFAIL\u001B[0m.", HEAD
            message "  ${ae.message}", HEAD
            throw ae
        } catch (e) {
            exception e.message, HEAD
            if (e.message && e.message.startsWith('Cannot run program')) {
                assert false,"Don't find PhantomJs: ${e.message}"
            } else {
                assert false,'Error executing PhantomJs: ' + e.message
            }
        } finally {
            File file = new File(nameFile)
            //new File('out.js').text = file.text
            if (file && file.exists()) {
                file.delete()
            }
        }
    }

    static String getParametersText(List parameters) {
        def parametersText = ''
        if (parameters) {
            parametersText = parameters.collect { value ->
                if (value instanceof String) {
                    return '"' + value + '"'
                } else {
                    return value as String
                }
            }.join(',')
        }
        return parametersText
    }
}
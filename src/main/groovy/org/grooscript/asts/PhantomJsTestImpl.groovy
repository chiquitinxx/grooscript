package org.grooscript.asts

import groovy.json.JsonSlurper
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.GrooScript
import org.grooscript.convert.GsConverter
import org.grooscript.util.Util

import static org.grooscript.util.GsConsole.*
import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.USER_HOME

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@SuppressWarnings('DuplicateStringLiteral')
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class PhantomJsTestImpl implements ASTTransformation {

    static final MAX_TIME_WAITING = 10000L
    static final HEAD = '[PhantomJs Test]'
    static final CONSOLE = '  [Console]'

    static final PHANTOM_JS_TEXT = '''
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
        console.log('GSRESULT:'+(result.result !== null ? JSON.stringify(result.result) : ''));
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

        var gSresult = { number:0 , tests: [], console: '', result: null};
        gs.assert = function(value, text) {
            var test = { result: value, text: value.toString()};
            if (arguments.length == 2 && arguments[1]!=null && arguments[1]!=undefined) {
                test.text = text;
            }
            gSresult.tests[gSresult.number++] = test;
        };

        {{GROOSCRIPT}}

        try {
            var returnResult = {{FUNCTION_CALL}}
            try {
                if (returnResult) {
                    gSresult.result = gs.toJavascript(returnResult);
                }
            } catch (ee) {
                gSresult.result = null;
            }
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
        console.log('  Result:FAIL Desc:Fail loading url: '+status);
        phantom.exit(1);
    } else {
        page.libraryPath = '{{LIBRARY_PATH}}'
        if (page.injectJs('jquery.min.js') && page.injectJs('grooscript.min.js')) {
            //console.log('Evaluating code...');
            evaluateAfterSeconds({{SECONDS}});
        } else {
            console.log(' Result: FAIL Desc: Fail in inject.');
            phantom.exit(1);
        }
    }
});'''

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //Start
        if (!nodes[0] instanceof AnnotationNode || !nodes[1] instanceof MethodNode) {
            return
        }

        MethodNode method = (MethodNode) nodes[1]
        method.returnType = ClassHelper.OBJECT_TYPE

        def messageError
        AnnotationNode annotationNode = (AnnotationNode) nodes[0]

        if (!annotationNode.getMember('url')) {
            sourceUnit.addError(new SyntaxException('Have to define url parameter',
                    annotationNode.lineNumber, annotationNode.columnNumber))
        }

        if (sourceUnit.errorCollector.errorCount > 0) {
            return
        }

        String url = annotationNode.getMember('url').text
        String capture = annotationNode.getMember('capture')?.text
        int waitSeconds = annotationNode.getMember('waitSeconds') ? annotationNode.getMember('waitSeconds').value : 0
        boolean withInfo = annotationNode.getMember('info')

        if (!url) {
            messageError = 'url not defined, use @PhantomJsTest(url=\'http://grooscript.org\')'
        }
        def jsTest = ''
        if (!messageError) {
            GsConverter converter = new GsConverter()

            try {
                converter.conversionOptions = GrooScript.defaultOptions
                converter.conversionOptions.mainContextScope = ['$', 'gs', 'window', 'document']
                jsTest = converter.processAstListToJs([method])
            } catch (e) {
                messageError = 'Error converting code ->' + e.message
            }
        }

        def textCode = "def listParams = [];\n${method.parameters.collect { "listParams << ${it.name}"}.join(';')};\n" +
                "def result = Class.forName('org.grooscript.asts.PhantomJsTestImpl').doPhantomJsTest('${url}', " +
                "\'\'\'" + jsTest + "\'\'\', '${method.name?:''}', ${waitSeconds}, '${capture?:''}',listParams, " +
                "'${messageError?:''}', ${withInfo}); return result;"
        method.declaringClass
        method.setCode new AstBuilder().buildFromString(CompilePhase.CLASS_GENERATION , textCode)
    }

    static String getJsLibrariesPath() {

        String jsHome = System.getProperty('JS_LIBRARIES_PATH')
        if (!jsHome) {

            try {
                def userHome = USER_HOME

                if (userHome) {
                    def version = Util.grooscriptVersion
                    def folder = new File(userHome + SEP + '.grooscript')
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                    def path = userHome + SEP + '.grooscript' + SEP + version
                    folder = new File(path)
                    if (folder.exists()) {
                        message 'Using js local files in ' + path, HEAD
                    } else {
                        folder.mkdirs()
                        ['grooscript.min.js', 'jquery.min.js'].each { fileName ->
                            new File(path + SEP + fileName).text =
                                GrooScript.classLoader.getResourceAsStream("META-INF${SEP}resources${SEP}" + fileName).text
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
                assert false, 'Need define property JS_LIBRARIES_PATH, folder with grooscript.min.js and jquery.min.js'
            }
        }
        jsHome
    }

    static def doPhantomJsTest(String url, String testCode, String methodName, int waitSeconds = 0,
                                String capture = null, List parameters = null, String messageError = null,
                                boolean withInfo = false) {
        def nameFile = 'phantomjs.js'
        def result = null
        try {
            if (messageError) {
                assert false, 'PhantomJs Initial Error: ' + messageError
            }
            def jsHome = jsLibrariesPath
            String phantomJsHome
            if (!System.getProperty('PHANTOMJS_HOME') && !System.getenv('PHANTOMJS_HOME')) {
                assert false, 'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder'
            } else {
                phantomJsHome = System.getProperty('PHANTOMJS_HOME') ?: System.getenv('PHANTOMJS_HOME')
            }
            message "Starting Test in ${url}", HEAD

            def sysOp = System.getProperty('os.name')
            if (sysOp && sysOp.toUpperCase().contains('WINDOWS')) {
                jsHome = jsHome.replace('\\', SEP)
                jsHome = (jsHome.indexOf(':') == 1 ? jsHome.substring(2) : jsHome)
            }

            //Save the file
            new File(nameFile).text = getScriptText(url, testCode, waitSeconds, capture, jsHome, methodName, parameters)

            //Execute PhantomJs
            String command = phantomJsHome
            if (sysOp && sysOp.toUpperCase().contains('WINDOWS')) {
                command += "${SEP}phantomjs.exe " + nameFile
            } else {
                command += "${SEP}bin${SEP}phantomjs " + nameFile
            }

            if (withInfo) {
                message '**************************************************** INFO BEGIN', HEAD
                message 'Url: ' + url, HEAD
                message 'MethodName: ' + methodName, HEAD
                message 'Parameters: ' + parameters, HEAD
                message 'MessageError: ' + messageError, HEAD
                message 'Operating system: ' + sysOp, HEAD
                message 'PhantomJs Home used: ' + phantomJsHome, HEAD
                message 'Execution command: ' + command, HEAD
                message 'Library path used: ' + jsHome, HEAD
                message '**************************************************** INFO END', HEAD
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
                        assert false, line.substring(line.indexOf(' Desc:') + 6)
                    } else if (line.toUpperCase().startsWith('CONSOLE:')) {
                        message line.substring(8), CONSOLE
                    } else if (line.contains('Result:OK')) {
                        assert true, line.substring(line.indexOf(' Desc:') + 6)
                    } else if (line.toUpperCase().startsWith('GSRESULT:')) {
                        if (line.substring(9).trim() != '') {
                            def slurper = new JsonSlurper()
                            result = slurper.parseText(line.substring(9))
                        }
                    } else {
                        if (line.trim())
                            message line, HEAD
                    }
                }
            }
            message "Result \u001B[92mSUCCESS\u001B[0m.", HEAD
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
            if (withInfo) {
                message '**************************************************** BEGIN JS TEST', HEAD
                file.text.eachLine { line ->
                    println line
                }
                message '**************************************************** END JS TEST', HEAD
            }
            if (file && file.exists()) {
                file.delete()
            }
        }
        result
    }

    private static String getParametersText(List parameters) {
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

    private static getScriptText(url, testCode, waitSeconds, capture, jsHome, methodName, parameters) {
        def scriptText
        scriptText = PHANTOM_JS_TEXT
        scriptText = scriptText.replace('{{URL}}', url)
        scriptText = scriptText.replace('{{GROOSCRIPT}}', testCode)
        scriptText = scriptText.replace('{{SECONDS}}', waitSeconds as String)

        if (capture) {
            scriptText = scriptText.replace('{{CAPTURE}}', "console.log('Capturing...');page.render('$capture');\n")
        } else {
            scriptText = scriptText.replace('{{CAPTURE}}', '')
        }

        scriptText = scriptText.replace('{{LIBRARY_PATH}}', jsHome)
        scriptText = scriptText.replace('{{FUNCTION_CALL}}', "${methodName}(${getParametersText(parameters)});")
        scriptText
    }
}
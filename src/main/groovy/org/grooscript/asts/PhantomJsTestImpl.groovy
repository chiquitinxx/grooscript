package org.grooscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.GsConverter

import java.lang.reflect.Modifier

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class PhantomJsTestImpl implements ASTTransformation {

    static final phantomJsText = '''
var page = require('webpage').create();

page.onConsoleMessage = function(msg) {
    console.log('CONSOLE: ' + msg);
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
            var test = { result: value, text: value.toString()}
            if (arguments.length == 2 && arguments[1]!=null && arguments[1]!=undefined) {
                test.text = text;
            }
            gSresult.tests[gSresult.number++] = test;
        };

        function gSprintln(value) {
            console.log(value);
        };

        function grooKimbo(selector,other) {
            //return gSlist(window.Kimbo(selector,other));
            var result = window.Kimbo(selector,other);
            result.size = function() {
                return this.length;
            }
            return result;
        }
        window.$ = grooKimbo;

        //Begin grooscript code
        {{GROOSCRIPT}}

        try {
            {{FUNCTION_CALL}}
        } catch (e) {
            var message = 'ERROR EXECUTING CODE: ' + e;
            console.log (message);
            var test = { result: false, text: message}
            gSresult.tests[gSresult.number++] = test;
        };
        //End  grooscript code

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

        def parametersText
        def listParams = []

        if (method.parameters) {
            parametersText = ''
            method.parameters?.each { param ->
                listParams << "(${param.name} instanceof String ? gJump + ${param.name} + gJump:${param.name})"
            }
            parametersText += listParams.join(' + \', \' + ')
        } else {
            parametersText = "\"\""
        }

        def textCode = '''
            def marker = new Throwable()
            def errorMessage = "''' + (messageError ?: '') + '''"
            if (errorMessage) {
                assert false, 'PhantomJs Error: '+errorMessage
            }
            def jsHome = System.getProperty('JS_LIBRARIES_PATH')
            if (!System.getProperty('JS_LIBRARIES_PATH')) {

                try {
                    def userHome = System.getProperty('user.home')
                    if (userHome) {
                        def version = Class.forName('org.grooscript.GsConverter').package.implementationVersion

                        def path = userHome + File.separator + '.grooscript' + (version ? File.separator + version : '')
                        def folder = new File(path)
                        if (folder.exists()) {
                            println 'Using js local files in ' + path
                        } else {
                            folder.mkdirs()
                            ['grooscript.js','kimbo.min.js'].each { fileName ->
                                new File(path + File.separator + fileName).text =
                                    this.class.classLoader.getResourceAsStream('META-INF/resources/' + fileName).text
                            }
                        }
                        jsHome = path
                    } else {
                        println "Error looking for js files: missing System.getProperty('user.home')"
                    }
                } catch (e) {
                    println "Error looking for js files: " + e.message
                }
                if (!jsHome) {
                    assert false, 'Need define property JS_LIBRARIES_PATH, folder with grooscript.js and kimbo.min.js'
                }
            }
            def phantomJsHome
            if (!System.getProperty('PHANTOMJS_HOME') && !System.getenv('PHANTOMJS_HOME')) {
                assert false, 'Need define PHANTOMJS_HOME as property or environment variable; the PhantomJs folder'
            } else {
                phantomJsHome = System.getProperty('PHANTOMJS_HOME') ?: System.getenv('PHANTOMJS_HOME')
            }
            println 'Starting PhantomJs test in ''' + url + '''\'
            def nameFile = 'phantomjs.js'
            try {
                //Save the file
                def finalText = \'\'\''''+ finalText +'''\'\'\'
                finalText = finalText.replace('{{LIBRARY_PATH}}', jsHome)
                def gJump = '"'
                def parametersText = '''+ parametersText +'''
                //println 'Parameters: '+parametersText
                finalText = finalText.replace('{{FUNCTION_CALL}}', \'''' + method.name + '''(' + parametersText + ');\')
                //println '*********************'
                //println finalText
                //println '*********************'
                new File(nameFile).text = finalText

                //Execute PhantomJs
                String command = phantomJsHome + "/bin/phantomjs " + nameFile
                def proc = command.execute()
                proc.waitForOrKill(10000L)
                def exit = proc.in.text
                if (!exit) {
                    println 'Error executing command: '+command
                    println 'return code: ' + proc.exitValue()
                    println 'stderr: ' + proc.err.text
                    assert false, 'Error launching PhantomJs'
                } else {
                    exit.eachLine { String line->
                        if (line.contains('Result:FAIL')) {
                            assert false,line.substring(line.indexOf(' Desc:')+6)
                        } else if (line.startsWith('Console:')) {
                            println line.substring(8)
                        } else if (line.contains('Result:OK')) {
                            assert true,line.substring(line.indexOf(' Desc:')+6)
                        } else {
                            println line
                        }
                    }
                }
            } catch (AssertionError ae) {
                println 'Assert error in PhantomJs test.'
                throw ae
            } catch (e) {
                println 'Error PhantomJs Test ->'+e.message
                if (e.message && e.message.startsWith('Cannot run program')) {
                    assert false,"Don't find PhantomJs: " + e.message
                } else {
                    assert false,'Error executing PhantomJs: ' + e.message
                }
            } finally {
                File file = new File(nameFile)
                if (file && file.exists()) {
                    file.delete()
                }
            }
            println 'PhantomJs test finished.'
            return null
        '''

        method.setCode new AstBuilder().buildFromString(CompilePhase.CLASS_GENERATION , textCode)
    }
}
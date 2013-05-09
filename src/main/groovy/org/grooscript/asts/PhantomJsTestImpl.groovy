package org.grooscript.asts

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.grooscript.GrooScript
import org.grooscript.GsConverter

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class PhantomJsTestImpl implements ASTTransformation {

    def phantomJsText = '''
var page = require('webpage').create();

page.onConsoleMessage = function(msg) {
    console.log('CONSOLE: ' + msg);
};

page.open('{{URL}}', function (status) {
    if (status !== 'success') {
        console.log('Fail loading url.');
        phantom.exit(1);
    } else {
        //page.libraryPath = '../../main/resources/META-INF/resources'
        page.libraryPath = '{{LIBRARY_PATH}}'
        if (page.injectJs('kimbo.min.js') && page.injectJs('grooscript.js')) {
            //console.log('Evaluating...');
            var result = page.evaluate(function() {

                var gSresult = { number:0 , tests: [], console: ''};
                function gSassert(value, text) {

                    var test = { result: value, text: value.toString()}
                    if (arguments.length == 2 && arguments[1]!=null && arguments[1]!=undefined) {
                        test.text = text;
                    }
                    gSresult.tests[gSresult.number++] = test;
                };

                function gSprintln(value) {
                    if (gSconsole != "") {
                        gSconsole = gSconsole + "\\n";
                    }
                    gSconsole = gSconsole + 'Console:' + value
                };

                function grooKimbo(selector,other) {
                    return gSlist(window.Kimbo(selector,other));
                }
                window.$ = grooKimbo;

                //gSconsoleOutput = true;

                //Begin grooscript code

                {{GROOSCRIPT}}

                //End  grooscript code

                gSresult.console = gSconsole;

                return gSresult;
            });
            console.log('Number of tests: '+result.number);
            if (result.number > 0) {
                var i;
                for (i=0;i<result.tests.length;i++) {
                    console.log('Test ('+i+') Result:'+(result.tests[i].result==true?'OK':'FAIL')+
                        ' Desc:'+result.tests[i].text);
                }
            }
            console.log(result.console);
            //window.setTimeout(function () {
            //    page.render('initial.png');
            //    phantom.exit();
            //}, 20000);
            //page.render('initial.png');
            phantom.exit()
        } else {
            console.log('Fail in inject.');
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

        AnnotationNode node = (AnnotationNode) nodes[0]
        String url = node.getMember('url').text
        //println 'url->'+url

        MethodNode method = (MethodNode) nodes[1]
        def Statement testCode = method.getCode()

        //println 'testCode->'+testCode

        def GsConverter converter = new GsConverter()

        def jsTest
        try {
            jsTest = converter.processAstListToJs([testCode])
        } catch (e) {
            println 'Error converting ->'+e.message
        }

        //println 'js code->'+jsTest

        String text = phantomJsText
        text = text.replace('{{URL}}',url)
        //text = text.replace('{{LIBRARY_PATH}}','/Users/jorgefrancoleza/desarrollo/grooscript/src/main/resources/META-INF/resources')
        text = text.replace('{{LIBRARY_PATH}}',node.getMember('jsPath').text)
        text = text.replace('{{GROOSCRIPT}}',jsTest)
        File fileJs = new File(GrooScript.JS_TEMP_FILE)
        fileJs.write(text)

        method.setCode new AstBuilder().buildFromCode {
            println 'Starting PhantomJs test ...'
            try {
                String command = "/Applications/phantomjs/bin/phantomjs ${org.grooscript.GrooScript.JS_TEMP_FILE}"
                //String command = "${System.getenv('PHANTOMJS_HOME')}/phantomjs ${org.grooscript.GrooScript.JS_TEMP_FILE}"
                def proc = command.execute()
                proc.waitFor()
                def exit = proc.in.text
                if (!exit) {
                    println 'Error executing command:'+command
                    println "return code: ${proc.exitValue()}"
                    println "stderr: ${proc.err.text}"
                    assert false, 'Error launching PhantomJs'
                } else {
                    //println "stdout: ${exit}"
                    if (exit.startsWith('Number of tests:')) {
                        exit.eachLine { String line->
                            //println 'Line.>'+line
                            if (line) {
                                if (line.contains('Result:FAIL')) {
                                    assert false,line.substring(line.indexOf(' Desc:')+6)
                                } else if (line.startsWith('Console:')) {
                                    println line.substring(8)
                                } else {
                                    assert true,line.substring(line.indexOf(' Desc:')+6)
                                }
                            }
                        }
                    } else {
                        println 'Error executing tests.\n'+proc.in.text
                        assert false, 'Error executing PhantomJs'
                    }
                }
                //println 'End PhantomJs test.'
            } catch (e) {
                println 'Error PhantomJs Test ->'+e.message
                if (e.message && e.message.startsWith('Cannot run program')) {
                    assert false,'Don\'t find PhantomJs: '+e.message
                } else {
                    assert false,'Error executing PhantomJs: '+e.message
                }
            } finally {
                File file = new File(org.grooscript.GrooScript.JS_TEMP_FILE)
                if (file && file.exists()) {
                    file.delete()
                }
            }
            return null
        }
    }
}
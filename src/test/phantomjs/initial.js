var page = require('webpage').create();

page.onConsoleMessage = function(msg) {
    console.log('CONSOLE: ' + msg);
};

page.open('file:///Users/jorgefrancoleza/desarrollo/grooscript/src/test/phantomjs/html/initial.html', function (status) {
    if (status !== 'success') {
        console.log('Fail loading url.');
        phantom.exit(1);
    } else {
        //page.includeJs("http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js", function() {
        page.libraryPath = '../../main/resources/META-INF/resources'
        if (page.injectJs('jquery.min.js') && page.injectJs('grooscript.js')) {
            console.log('Evaluating...');
            var result = page.evaluate(function() {
                $("body").append('<p>GrooScript</p>');

                var gSresult = { number:0 , tests: []};
                function gSassert(value, text) {

                    var test = { result: value, text: value.toString()}
                    if (arguments.length == 2 && arguments[1]!=null && arguments[1]!=undefined) {
                        test.text = text;
                    }
                    gSresult.tests[gSresult.number++] = test;
                };

                gSconsoleOutput = true;

                //Begin grooscript code

                gSprintln('Hello from grooscript!');

                gSassert(1==1,'1==1');
                gSassert(1==0,'1==0');
                gSassert(0,'0');
                gSassert(1,'1');
                gSassert($("title").text()=='GrooScript using PhantomJs','Title is "'+$("title").text()+'"');

                //End  grooscript code

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

            //page.render('initial.png');
            phantom.exit()
        } else {
            console.log('Fail in inject.');
            phantom.exit(1);
        }
        //});
    }
});
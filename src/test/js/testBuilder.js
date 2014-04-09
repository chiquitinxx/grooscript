//Need mocha installed to run this test, use sudo if fails
//npm install -g mocha
//to run tests: mocha
var gs = require('../../main/resources/META-INF/resources/grooscript.js');
var fs = require('fs');
eval(fs.readFileSync('src/main/resources/META-INF/resources/grooscript-builder.js')+'');

var assert = require("assert");

describe('test builder', function(){

    it('test process a basic html fragment', function(){
        assert.equal(HtmlBuilder.build(function(it) {
            return gs.mc(this,"body",gs.list([function(it) {
                return gs.mc(this,"p",gs.list(["hola"]));
            }]));
        }), '<body><p>hola</p></body>');
    });

    it('test process a basic html fragment 2', function(){
        assert.equal(HtmlBuilder.build(function(it) {
            return gs.mc(this, 'body', [gs.map({hello: 'hello'}), function(it) {
                return gs.mc(this, 'p', ['Wow'])
            }]);
        }), "<body hello='hello'><p>Wow</p></body>");
    });
});

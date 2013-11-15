//Need mocha installed to run this test, use sudo if fails
//npm install -g mocha
//to run tests: mocha
var gs = require('../../main/resources/META-INF/resources/grooscript.js');

var assert = require("assert")

describe('initial tests on gs', function(){
    it('initial values', function(){
        assert.equal(false, gs.fails);
        assert.equal(false, gs.consoleInfo);
        assert.equal('', gs.consoleData);
        assert.equal(true, gs.consoleOutput);
    })
    it('convert to javascript', function(){
        assert.equal(gs.toJavascript(5), 5);
        assert.equal(gs.toJavascript('hello'), 'hello');
        var list = gs.list([1, gs.map().add('hello', 'yes'), 3]);
        assert.equal(gs.toJavascript(list)[1]['hello'], 'yes');
    })
    it('convert to groovy', function(){
        assert.equal(gs.toGroovy(5), 5);
        assert.equal(gs.toGroovy('hello'), 'hello');
        var list = [1, [1, 2], {a:1, b:2}];
        var result = gs.equals(gs.toGroovy(list), gs.list([1, [1, 2], gs.map().add('a',1).add('b', 2)]));
        assert.equal(result, true);
    })
})
//Need mocha installed to run this test, use sudo if fails
//npm install -g mocha
//to run tests: mocha
var gs = require('../../main/resources/META-INF/resources/grooscript.js');

var assert = require("assert");

function add(a, b) {
    return a + b;
}

describe('initial tests on gs', function(){

    it('initial values', function(){
        assert.equal(false, gs.fails);
        assert.equal(false, gs.consoleInfo);
        assert.equal('', gs.consoleData);
        assert.equal(true, gs.consoleOutput);
    });

    it('convert to javascript', function(){
        assert.equal(gs.toJavascript(5), 5);
        assert.equal(gs.toJavascript('hello'), 'hello');
        var list = gs.list([1, gs.map().add('hello', 'yes'), 3]);
        assert.equal(gs.toJavascript(list)[1]['hello'], 'yes');
    });

    it('convert to groovy', function(){
        assert.equal(gs.toGroovy(5), 5);
        assert.equal(gs.toGroovy('hello'), 'hello');
        var list = [1, [1, 2], {a:1, b:2}];
        var result = gs.equals(gs.toGroovy(list), gs.list([1, [1, 2], gs.map().add('a',1).add('b', 2)]));
        assert.equal(result, true);
    });

    it('equals lists', function() {
        assert.equal(gs.equals([1], gs.list([1])), true);
        assert.notEqual(gs.list([1]), [1]);
    });

    it('equals maps', function() {
        assert.equal(gs.equals(gs.map().add('one', 1), {one: 1}), true);
        assert.notEqual(gs.map().add('one', 1), { one: 1});
    });

    it('test date format', function() {
        assert.equal(gs.date().parse("yyyy/MM/dd", "2009/09/01").format('yyyy-MM-dd'), '2009-09-01');
    });

    it('get a function from main scope', function() {
        assert.equal(typeof eval('add'), 'function');
        assert.equal(eval('add')(4, 5), 9);
        //Not working eval in gs.mc
        //assert.equal(gs.mc(this,"add",gs.list([4, 5]), eval), 9);
    });
});

//Need mocha installed to run this test, use sudo if fails
//npm install -g mocha
//to run tests: mocha
var gs = require('../../main/resources/META-INF/resources/grooscript.js');

var assert = require("assert");

function add(a, b) {
    return a + b;
}

function MyClass() {
    var gSobject = gs.inherit(gs.baseClass,'MyClass');
    gSobject.clazz = { name: 'Pepe', simpleName: 'MyClass'};
    gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
    gSobject.a = null;
    gSobject.b = null;
    gSobject.Pepe1 = function(map) { gs.passMapToObject(map,this); return this;};
    if (arguments.length==1) {gSobject.Pepe1(arguments[0]); }

    return gSobject;
};

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

    it('equals numbers', function() {
        assert.equal(5, 5.00);
        assert.equal(gs.toNumber(5), 5.00);
        assert.equal(gs.toNumber('7.02'), 7.02);
    });

    it('equals lists', function() {
        assert.equal(gs.equals([1], gs.list([1])), true);
        assert.equal(gs.equals([], gs.list()), true);
        assert.equal(gs.equals([], gs.list([])), true);
        assert.notEqual(gs.list([1]), [1]);
    });

    it('equals maps', function() {
        assert.equal(gs.equals(gs.map().add('one', 1), {one: 1}), true);
        assert.notEqual(gs.map().add('one', 1), { one: 1});
    });

    it('test date format', function() {
        assert.equal(gs.date().parse("yyyy/MM/dd", "2009/01/01").format('yyyy-MM-dd'), '2009-01-01');
        assert.equal(gs.date().parse("yyyy/MM/dd", "2009/09/01").format('yyyy-MM-dd'), '2009-09-01');
        assert.equal(gs.date().parse("yyyy/MM/dd", "2009/10/01").format('yyyy-MM-dd'), '2009-10-01');
        assert.equal(gs.date().parse("yyyy/MM/dd", "2009/12/01").format('yyyy-MM-dd'), '2009-12-01');
    });

    it('get a function from main scope', function() {
        assert.equal(typeof eval('add'), 'function');
        assert.equal(eval('add')(4, 5), 9);
        //Not working eval in gs.mc
        //assert.equal(gs.mc(this,"add",gs.list([4, 5]), eval), 9);
    });

    it('initialize a map with a javascript object', function() {
        var map = gs.map({a: 1, b: 2});
        assert.equal(map.a, 1);
        assert.equal(map.b, 2);
        assert.equal(map.size(), 2);
    });

    it('initialize a class with a javascript object', function() {
        var myClass = MyClass({a: 1, b: 2});
        assert.equal(myClass.a, 1);
        assert.equal(myClass.b, 2);
    });
});

var gs = require('../../../src/main/resources/grooscript.js');

var assert = require("assert");

function Container() {
    var gSobject = gs.init('Container');
    gSobject.clazz = { name: 'Container', simpleName: 'Container'};
    gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
    if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
    return gSobject;
};

describe('speed tests', function(){

    var numberTimes = 5000;

    it('javascript object creation', function() {
        var i, list = [], init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.push({one: "number "+ i, two: i});
        }
        console.log("Javascript object time: "+(new Date().getTime() - init.getTime()));
    });

    it('javascript other object creation', function() {
        var i, list = [], init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.push(Object.create({one: "number "+ i, two: i}));
        }
        console.log("Javascript object 2 time: "+(new Date().getTime() - init.getTime()));
    });

    it('javascript array basic', function() {
        var i, init = new Date();
        for (i = 0; i < numberTimes; i++) {
            var list = [1, 2, 3];
            list.push(4);
        }
        console.log("Javascript array time: "+(new Date().getTime() - init.getTime()));
    });

    it('grooscript array basic', function() {
        var i, init = new Date();
        for (i = 0; i < numberTimes; i++) {
            var list = gs.list([1, 2, 3]);
            list.add(4);
        }
        console.log("Grooscript array time: "+(new Date().getTime() - init.getTime()));
    });

    it('grooscript map creation', function() {
        var i, list = [], init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.push(gs.map().add("one", "number" + i + "").add("two", i));
        }
        console.log("Grooscript map time: "+(new Date().getTime() - init.getTime()));
    });

    it('grooscript object creation', function() {
        var i, list = [], init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.push(Container());
        }
        console.log("Grooscript object time: "+(new Date().getTime() - init.getTime()));
    });

    it('javascript method call on list', function() {
        var i, list = gs.list([]), init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.leftShift(i);
        }
        console.log("Javascript method call time: "+(new Date().getTime() - init.getTime()));
    });

    it('grooscript method call on list', function() {
        var i, list = gs.list([]), init = new Date();
        for (i = 0; i < numberTimes; i++) {
            gs.mc(list, 'leftShift', gs.list([i]));
        }
        console.log("grooscript method call time: "+(new Date().getTime() - init.getTime()));
    });
});
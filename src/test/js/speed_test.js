var gs = require('../../main/resources/META-INF/resources/grooscript.js');

var assert = require("assert");

describe('speed tests', function(){

    var numberTimes = 5000;

    it('javascript object creation', function() {
        var i, list = [], init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.push({one: "number "+ i, two: i});
        }
        console.log("Time: "+(new Date().getTime() - init.getTime()));
    });

    it('grooscript map creation', function() {
        var i, list = [], init = new Date();
        for (i = 0; i < numberTimes; i++) {
            list.push(gs.map().add("one", "number" + i + "").add("two", i));
        }
        console.log("Time: "+(new Date().getTime() - init.getTime()));
    });
});
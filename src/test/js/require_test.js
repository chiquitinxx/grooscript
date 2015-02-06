var requirejs = require('requirejs');

requirejs.config({
    //Pass the top-level main.js/index.js require
    //function to requirejs so that node modules
    //are loaded relative to the top-level JS file.
    baseUrl: __dirname,
    nodeRequire: require
});

var assert = require("assert");

describe('init require js', function(){

    it('import A module', function() {
        var A = requirejs('require/A');
        assert.equal(A(), 1);
    });

    it('import B module with dependencies', function() {
        var B = requirejs('require/B');
        assert.equal(B().data, 2);
        assert.equal(B().a, 1);
        assert.equal(B().aPlus, 1);
    });

    it('import initial module with dependencies', function() {
        var initial = requirejs('require/initial');
        assert.equal(initial, 'OK');
    });

    it('can use initial without return', function() {
        requirejs('require/initial');
    });
});

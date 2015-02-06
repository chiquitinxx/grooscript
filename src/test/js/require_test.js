var requirejs = require('requirejs');

requirejs.config({
    //Pass the top-level main.js/index.js require
    //function to requirejs so that node modules
    //are loaded relative to the top-level JS file.
    baseUrl: 'src/test/js/require',
    nodeRequire: require
});

var assert = require("assert");

describe('init require js', function(){

    it('import initial module', function() {
        var A = requirejs('A');
        assert.equal(A(), 1);
    });

    it('import module with dependencies', function() {
        var B = requirejs('B');
        assert.equal(B().data, 2);
        assert.equal(B().a, 1);
        assert.equal(B().aPlus, 1);
    });
});

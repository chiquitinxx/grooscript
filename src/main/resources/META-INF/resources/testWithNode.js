//var fs = require('fs');
// file is included here:
//eval(fs.readFileSync('grooscript.js')+'');
var gs = require('./grooscript.js');
gs.consoleOutput = true;
console.log('Ready to Test!');
//gs.consoleInfo = true;
/////////////////////////////////////////////////////////// Tests here

function Observable() {
    var gSobject = gs.inherit(gs.baseClass,'Observable');
    gSobject.clazz = { name: 'rx.Observable', simpleName: 'Observable'};
    gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
    gSobject.subscribers = gs.list([]);
    gSobject.sourceList = null;
    gSobject.chain = gs.list([]);
    gSobject.listen = function() { return Observable.listen(); }
    gSobject.from = function(x0) { return Observable.from(x0); }
    gSobject['produce'] = function(event) {
        return gs.mc(gSobject.subscribers,"each",[function(it) {
            return gs.mc(gSobject,"processFunction",[event, it]);
        }]);
    }
    gSobject['map'] = function(cl) {
        console.log("Map:"+cl);
        gs.mc(gSobject.chain,'leftShift', gs.list([cl]));
        return this;
    }
    gSobject['filter'] = function(cl) {
        console.log("Filter:"+cl);
        gs.mc(gSobject.chain,'leftShift', gs.list([function(it) {
            if ((cl.delegate!=undefined?gs.applyDelegate(cl,cl.delegate,[it]):gs.execCall(cl, this, [it]))) {
                return it;
            } else {
                throw "Exception";
            };
        }]));
        return this;
    }
    gSobject['subscribe'] = function(cl) {
        console.log("Subs:"+cl);
        while (gs.bool(gSobject.chain)) {
            cl = (gs.mc(cl,'leftShift', gs.list([gs.mc(gSobject.chain,"remove",[gs.minus(gs.mc(gSobject.chain,"size",[]), 1)])])));
        };
        gs.mc(gSobject.subscribers,'leftShift', gs.list([cl]));
        console.log("Subs list:"+gSobject.subscribers.length);
        console.log("Source list:"+gSobject.sourceList.length);
        if (gs.bool(gSobject.sourceList)) {
            return gs.mc(gSobject.sourceList,"each",[function(it) {
                console.log("In each: "+it);
                return gs.mc(gSobject,"processFunction",[it, cl]);
            }]);
        };
    }
    gSobject['processFunction'] = function(data, cl) {
        try {
            console.log("Cl:"+cl);
            console.log("Cl delegate:"+cl.delegate);
            (cl.delegate!=undefined?gs.applyDelegate(cl,cl.delegate,[data]):gs.execCall(cl, this, [data]));
        } catch (e) {
            console.log("Error:"+e);
        };
    }
    if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};

    return gSobject;
};
Observable.listen = function(it) {
    return Observable();
}
Observable.from = function(list) {
    return Observable(gs.map().add("sourceList",list));
}


var result = gs.list([]);
gs.mc(gs.mc(gs.mc(gs.execStatic(Observable,'from', this,[gs.list([1 , 5 , 9 , 12 , 3 , 8])]),"filter",[function(it) {
    console.log("1:"+it);
    return it < 5;
}]),"map",[function(it) {
    console.log("2:"+it);
    return gs.multiply("H", it);
}]),"subscribe",[function(event) {
    return gs.mc(result,'leftShift', gs.list([event]));
}]);
console.log("Result: "+result);
gs.assert(gs.equals(result, gs.list(["H" , "HHH"])), "Assertion fails: (result == [H, HHH])");

////////////////////////////////////////--------------------End Test here -> Resume

console.log('\nFails = '+gs.fails);

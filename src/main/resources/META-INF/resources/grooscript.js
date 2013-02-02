/////////////////////////////////////////////////////////////////
// assert and println conversions
/////////////////////////////////////////////////////////////////
//Will be true if any assert fails
var gSfails = false;

function gSassert(value) {
    if(value==false) {
          gSfails = true;
          var message = 'Assert Fails! - ';
          //gSprintln('tam-'+arguments.length);
          if (arguments.length == 2 && arguments[1]!=null) {
            message = arguments[1] + ' - ';
          }
          gSprintln(message+value);
    }
};

//Where all output is stored
var gSconsole = "";
//If true and console is available, all output will go through console
var gSconsoleOutput = false;
//If true and console is available, some methods will show info on console
var gSconsoleInfo = false;

//Function that used for print and println in groovy
function gSprintln(value) {
    if (gSconsoleOutput && console) {
        console.log(value);
    } else {
        if (gSconsole != "") {
            gSconsole = gSconsole + "\n";
        }
        gSconsole = gSconsole + value
    }
};

/////////////////////////////////////////////////////////////////
// Class functions
/////////////////////////////////////////////////////////////////
gsBaseClass = {
    //The with function, with is a reserved word in JavaScript
    gSwith : function(closure) { closure.apply(this,closure.arguments); },
    //gSclass : []
    getProperties : function() {
        var result = gSlist([]);
        for (ob in this) {
            if (typeof this[ob] !== "function" && ob!='gSclass') {
                result.add(ob);
            }
        }
        return result;
    },
    getMethods : function() {
        var result = gSlist([]);
        for (ob in this) {
            if (typeof this[ob] === "function") {
                var item = {
                    name: ob
                };
                result.add(item);
            }
        }
        return result;
    },
    invokeMethod: function(name,values) {
        var i,newArgs = [];
        if (values!=null && values!=undefined) {
            for (i=0;i<values.length;i++) {
                newArgs[i] = values[i];
            }
        }
        var f = this[name];
        return f.apply(this,newArgs);
    },
    gSconstructor : function() {
        return this;
    }
}

function gSexpando() {
    var object = inherit(gsBaseClass,'Expando');

    object.constructorWithMap = function(map) { gSpassMapToObject(map,this); return this;};
    if (arguments.length==1) {object.constructorWithMap(arguments[0]); }

    return object;
}

function ExpandoMetaClass() {
    var object = inherit(gsBaseClass,'ExpandoMetaClass');
    object.initialize = function() {
        return this;
    }
    return object;
}

function gSexpandWithMetaclass(item,objectName) {
    if (gSglobalMetaClass!=undefined && gSglobalMetaClass[objectName]!=null && gSglobalMetaClass[objectName]!=undefined) {
        var obj,map = gSglobalMetaClass[objectName];
        for (obj in map) {

            //Static methods
            var staticMap = map.getStatic();
            if (staticMap!=null && staticMap!=undefined) {
                var objStatic
                for (objStatic in staticMap) {
                    if (objStatic!='gSparent') {
                        //console.log('Adding static->'+objStatic);
                        item[obj] = staticMap[objStatic];
                    }
                }
            }
            //Non static methods and properties
            item[obj] = map[obj];
        }
    }
    return item;
}

function inherit(p,objectName) {
    if (p == null) throw TypeError();
    if (Object.create) {
        return gSexpandWithMetaclass(Object.create(p),objectName);
    }
    var t = typeof p;

    // If Object.create() is defined... // then just use it.
    // Otherwise do some more type checking
    if (t !== "object" && t !== "function")
        throw TypeError();

    function f() {};
    f.prototype = p;
    return gSexpandWithMetaclass(new f(),objectName);
}

/////////////////////////////////////////////////////////////////
// gSset - as Set and HashSet from groovy
/////////////////////////////////////////////////////////////////
function gSset(value) {
    var object; // = inherit(Array.prototype);

    if (arguments.length==0) {
        object = gSlist([]);
    } else {
        object = value;
    }

    object.gSisSet = true;

    object.gSwith = function(closure) {
        gSinterceptClosureCall(closure, this);
    }

    object.add = function(item) {
        if (!(this.contains(item))) {
            this[this.length]=item;
            return this;
        } else {
            return false;
        }
    }

    object.addAll = function(elements) {
        if (elements instanceof Array) {
            var i, fails = false;

            //Check if items not in set
            for (i=0;!fails && i<elements.length;i++) {
                if (this.contains(elements[i])) {
                    fails = true;
                }
            }
            if (fails) {
                return false;
            } else {
                //All ok, we add items to the set
                for (i=0;i<elements.length;i++) {
                    this.add(elements[i]);
                }

            }

        }
        return this;
    }


    object.equals = function(other) {
        if (!(other instanceof Array) || other.length!=this.length || !(other.gSisSet)) {
            return false;
        } else {
            var i;
            var result = true;
            for (i=0;i<this.length && result;i++) {
                if (!(other.contains(this[i]))) {
                    result = false;
                }
            }
            return result;
        }
    }

    object.toList = function() {
        var i,list = [];
        for (i=0;i<this.length;i++) {
            list[i] = this[i];
        }
        return gSlist(list);
    }

    object.plus = function(other) {
        var result = gSset();
        result.addAll(this);
        if (other instanceof Array) {
            var i;
            for (i=0;i<other.length;i++) {
                if (!(result.contains(other[i]))) {
                    result.add(other[i]);
                }
            }
        }
        return result;
    }

    object.minus = function(other) {
        var result = gSset();
        result.addAll(this);
        if (other instanceof Array) {
            var i;
            for (i=0;i<other.length;i++) {
                if (result.contains(other[i])) {
                    result.remove(other[i]);
                }
            }
        }
        return result;
    }

    object.remove = function(value) {
        var index = this.indexOf(value);
        if (index>=0) {
            this.splice(index,1);
        }
        return this;
    }

    return object;
}

/////////////////////////////////////////////////////////////////
// gSmap - [:] from groovy
/////////////////////////////////////////////////////////////////
function isgSmapProperty(name) {
    return name=='gSclass' || name == 'gSdefaultValue';
}

function gSmap() {
    var object = inherit(gsBaseClass,'LinkedHashMap');
    object.add = function(key,value) {
        this[key] = value;
        return this;
    }
    object.put = function(key,value) {
        return this.add(key,value);
    }
    object.leftShift = function(key,value) {
        return this.add(key,value);
    }
    object.putAt = function(key,value) {
        this.put(key,value);
    }
    object.size = function() {
        var number = 0;
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                number++;
            }
        }
        return number;
    }
    object.isEmpty = function() {
        return (this.size() == 0);
    }
    object.remove = function(key) {
        if (this[key]) {
            delete this[key];
        }
    }
    object.each = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                var f = arguments[0];
                //Nice, number of arguments in length property
                if (f.length==1) {
                    closure({key:ob, value:this[ob]});
                }
                if (f.length==2) {
                    closure(ob,this[ob]);
                }
            }
        }
    }

    object.count = function(closure) {
        var number = 0;
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                if (closure.length==1) {
                    if (closure({key:ob, value:this[ob]})) {
                        number++;
                    }
                }
                if (closure.length==2) {
                    if (closure(ob,this[ob])) {
                        number++;
                    }
                }
            }
        }
        return number;
    }

    object.any = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                var f = arguments[0];
                if (f.length==1) {
                    if (closure({key:ob, value:this[ob]})) {
                        return true;
                    }
                }
                if (f.length==2) {
                    if (closure(ob,this[ob])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    object.every = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                var f = arguments[0];
                if (f.length==1) {
                    if (!closure({key:ob, value:this[ob]})) {
                        return false;
                    }
                }
                if (f.length==2) {
                    if (!closure(ob,this[ob])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    object.find = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                var f = arguments[0];
                if (f.length==1) {
                    var entry = {key:ob, value:this[ob]};
                    if (closure(entry)) {
                        return entry;
                    }
                }
                if (f.length==2) {
                    if (closure(ob,this[ob])) {
                        return {key:ob, value:this[ob]};
                    }
                }
            }
        }
        return null;
    }

    object.findAll = function(closure) {
        var result = gSmap();
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                var f = arguments[0];
                if (f.length==1) {
                    var entry = {key:ob, value:this[ob]};
                    if (closure(entry)) {
                        result.add(entry.key,entry.value);
                    }
                }
                if (f.length==2) {
                    if (closure(ob,this[ob])) {
                        result.add(ob,this[ob]);
                    }
                }
            }
        }
        if (result.size()>0) {
            return result;
        } else {
            return null;
        }
    }

    object.collect = function(closure) {
        var result = gSlist([]);
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                var f = arguments[0];
                if (f.length==1) {
                    result.add(closure({key:ob, value:this[ob]}));
                }
                if (f.length==2) {
                    result.add(closure(ob,this[ob]));
                }
            }
        }
        if (result.size()>0) {
            return result;
        } else {
            return null;
        }
    }

    object.containsKey = function(key) {
        if (this[key]=='undefined' || this[key]==null) {
            return false;
        } else {
            return true;
        }
    }
    object.containsValue = function(value) {
        var gotIt = false;
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                if (gSequals(this[ob],value)) {
                    gotIt = true;
                    break;
                }
            }
        }
        return gotIt;
    }

    object.get = function(key,defaultValue) {
        if (!this.containsKey(key)) {
            this[key] = defaultValue;
        }
        return this[key];
    }

    object.toString = function() {
        var items = '';
        this.each (function(key,value) {
                     items = items + key+': '+value+' ,';
         });
        return 'gSmap->'+items;
    }

    object.equals = function(otherMap) {

        var result = true;
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                if (!gSequals(this[ob],otherMap[ob])) {
                    result = false;
                }
            }
        }
        return result;
    }

    object.values = function() {
        var result = gSlist([]);
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                result.add(this[ob]);
            }
        }
        return result;
    }

    object.gSdefaultValue = null;

    object.withDefault = function(closure) {
        this.gSdefaultValue = closure;
        return this;
    }

    object.inject = function(initial,closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                if (closure.length==2) {
                    var entry = {key:ob, value:this[ob]};
                    initial = closure(initial,entry);
                }
                if (closure.length==3) {
                    initial = closure(initial,ob,this[ob]);
                }
            }
        }
        //console.log('initial->'+initial);
        return initial;
    }

    object.putAll = function (items) {
        var i;
        for (i=0;i<items.length;i++) {
            var item = items[i];
            this.add(item.key,item.value);
        }
        return this;
    }

    object.plus = function(other) {
        var result = this.clone();
        if (other instanceof Array) {
            result.putAll(other);
        } else {
            for (ob in other) {
                if (typeof other[ob] !== "function" && !isgSmapProperty(ob)) {
                    result.add(ob,other[ob]);
                }
            }
        }
        //console.log('->'+result);
        return result;
    }

    object.clone = function() {
        var result = gSmap();
        for (ob in this) {
            if (typeof this[ob] !== "function" && !isgSmapProperty(ob)) {
                result.add(ob,this[ob]);
            }
        }

        return result;
    }

    object.minus = function(other) {
        var result = this.clone();
        for (ob in other) {
            if (typeof other[ob] !== "function" && !isgSmapProperty(ob)) {
                if (result[ob]!=null && result[ob]!=undefined && gSequals(result[ob],other[ob])) {
                    delete result[ob];
                }
            }
        }

        return result;
    }

    return object;
}

/////////////////////////////////////////////////////////////////
//gsList - [] from groovy
/////////////////////////////////////////////////////////////////
function gSlist(value) {
    var object = inherit(Array.prototype,'ArrayList');
    object = value;

    object.get = function(pos) {

        //Maybe comes a second parameter with default value
        if (arguments.length==2) {
            //console.log('uh->'+this[pos]);
            if (this[pos]==null || this[pos]=='undefined') {
                return arguments[1];
            } else {
                return this[pos];
            }
        } else {
            return this[pos];
        }
    }

    object.getAt = function(pos) {
        return this[pos];
    }

    object.gSwith = function(closure) {
        //closure.apply(this,closure.arguments);
        gSinterceptClosureCall(closure, this);
    }

    object.size = function() {
        return this.length;
    }

    object.isEmpty = function() {
        return this.length == 0;
    }

    object.add = function(element) {
        this[this.length]=element;
        return this;
    }

    object.addAll = function(elements) {
        if (elements instanceof Array) {
            var i;

            for (i=0;i<elements.length;i++) {
                this.add(elements[i]);
            }

        }
        return this;
    }

    object.clone = function() {
        var result = gSlist([]);
        result.addAll(this);
        return result;
    }

    object.plus = function(other) {
        var result = this.clone();
        result.addAll(other);
        return result;
    }

    object.minus = function(other) {
        var result = this.clone();
        result.removeAll(other);
        return result;
    }

    object.leftShift = function(element) {
        return this.add(element);
    }

    object.contains = function(object) {
        var gotIt,i;
        for (i=0;!gotIt && i<this.length;i++) {
            if (gSequals(this[i],object)) {
                //if (typeof this[i] === "function") continue;
                gotIt = true;
            }
        }
        return gotIt;
    }

    object.each = function(closure) {
        var i;
        for (i=0;i<this.length;i++) {
            //if (typeof this[i] === "function") continue;

            //TODO Beware this change, have to apply to all closure calls
            gSinterceptClosureCall(closure, this[i]);
            //closure(this[i]);
        }
        return this;
    }

    object.eachWithIndex = function(closure,index) {
        for (index=0;index<this.length;index++) {
            //if (typeof this[index] === "function") continue;
            closure(this[index],index);
        }
        return this;
    }

    object.any = function(closure) {
        var i;
        for (i=0;i<this.length;i++) {
            if (closure(this[i])) {
                return true;
            }
        }
        return false;
    }

    object.values = function() {
        var result = [];
        var i;
        for (i=0;i<this.length;i++) {
            result[i]=this[i];
        }
        return result;
    }
    //Remove only 1 item from the list
    object.remove = function(indexOrValue) {
        var index = -1;
        if (typeof indexOrValue == 'number') {
            index = indexOrValue;
        } else {
            index = this.indexOf(indexOrValue);
        }
        if (index>=0) {
            this.splice(index,1);
        }
        return this;
    }

    //Maybe too much complex, not much inspired
    object.removeAll = function(data) {
        if (data instanceof Array) {
            var result = []
            this.forEach(function(v, i, a) {
                if (data.contains(v)) {
                    result.push(i);
                }
            })
            //Now in result we have index of items to delete
            if (result.length>0) {
                var decremental = 0;
                var thisgSlist = this;
                result.forEach(function(v, i, a) {
                    //Had tho change this for thisgSlist, other scope on this here
                    thisgSlist.splice(v-decremental,1);
                    decremental=decremental+1;
                })
            }
        }
        return this;
    }

    object.collect = function(closure) {
        //this.forEach(closure)
        var i;
        for (i=0;i<this.length;i++) {
            //if (typeof this[i] === "function") continue;
            this[i] = closure(this[i]);
        }

        return this;
    }

    object.findAll = function(closure) {
        var values = this.filter(closure)
        return gSlist(values)
    }

    object.find = function(closure) {
        var result,i;
        for (i=0;!result && i<this.length;i++) {
            if (closure(this[i])) {
                result = this[i];
            }
        }
        return result;

    }

    object.first = function() {
        return this[0];
    }

    object.head = function() {
        return this[0];
    }

    object.last = function() {
        return this[this.length-1];
    }

    object.sum = function() {

        var result = 0;

        //can pass a closure to sum
        if (arguments.length == 1) {
            var i;
            for (i=0;i<this.length;i++) {
                //if (typeof this[i] === "function") continue;
                result = result + arguments[0](this[i]);
            }
        } else {

            if (this.length>0 && this[0]['plus']) {
                 var i;
                 var item = this[0];
                 for (i=0;i+1<this.length;i++) {
                     item = item.plus(this[i+1]);
                 }
                 return item;
            } else {
                 var i;
                 for (i=0;i<this.length;i++) {
                     result = result + this[i];
                 }
            }
        }
        return result;
    }

    object.inject = function() {

        var acc;
        //only 1 argument, just the closure
        if (arguments.length == 1) {

            acc = this[0];
            var i;
            for (i=1;i<this.length;i++) {
                //if (typeof this[i] === "function") continue;
                acc = arguments[0](acc,this[i]);
            }

        } else {
            //We suppose arguments = 2
            acc = arguments[0];
            //console.log('number->'+this.length);
            var j;
            for (j=0;j<this.length;j++) {
                //console.log('acc->'+acc);
                //if (typeof this[j] === "function") continue;
                acc = arguments[1](acc,this[j]);
                //console.log('fin acc->'+acc);
            }
        }
        return acc;
    }

    object.toList = function() {
        return this;
    }

    object.intersect = function(otherList) {
        var result = gSlist([]);
        var i;
        for (i=0;i<this.length;i++) {
            //if (typeof this[i] === "function") continue;
            if (otherList.contains(this[i])) {
                result.add(this[i]);
            }
        }
        return result;
    }

    object.max = function() {
        var result = null;
        var i;
        for (i=0;i<this.length;i++) {
            //if (typeof this[i] === "function") continue;
            if (result==null || this[i]>result) {
                result = this[i];
            }
        }
        return result;
    }

    object.min = function() {
        var result = null;
        var i;
        for (i=0;i<this.length;i++) {
            //if (typeof this[i] === "function") continue;
            if (result==null || this[i]<result) {
                result = this[i];
            }
        }
        return result;
    }

    object.toString = function() {

        if (this.length>0) {
            var i;
            var result = '[';
            for (i=0;i<this.length-1;i++) {
                //if (typeof this[i] === "function") continue;
                result = result + this[i] + ', ';
            }
            result = result + this[this.length-1] + ']';
            return result;
        } else {
            return '[]';
        }
    }

    object.grep = function(param) {
        if (param instanceof RegExp) {
            var i;
            var result = gSlist([]);
            for (i=0;i<this.length;i++) {
                //if (typeof this[i] === "function") continue;
                if (gSmatch(this[i],param)) {
                    result.add(this[i]);
                }
            }
            return result;
        } else if (param instanceof Array) {
            return this.intersect(param);
        } else if (typeof param === "function") {
            var i;
            var result = gSlist([]);
            for (i=0;i<this.length;i++) {
                //if (typeof this[i] === "function") continue;
                if (param(this[i])) {
                    result.add(this[i]);
                }
            }
            return result;

        } else {
            var i;
            var result = gSlist([]);
            for (i=0;i<this.length;i++) {
                //if (typeof this[i] === "function") continue;
                if (this[i]==param) {
                    result.add(this[i]);
                }
            }
            return result;

        }
    }

    object.equals = function(other) {
        //console.log('EQUALS!');
        if (!(other instanceof Array) || other.length!=this.length) {
            return false;
        } else {
            var i;
            var result = true;
            for (i=0;i<this.length && result;i++) {
                if (!gSequals(this[i],other[i])) {
                    result = false;
                }
            }
            //console.log('EQUALS RESULT-'+result);
            return result;
        }
    }

    object.gSjoin = function() {
        var separator = '';
        if (arguments.length == 1) {
            separator = arguments[0];
        }
        var i, result;
        result = '';
        for (i=0;i<this.length;i++) {
            result = result + this[i];
            if ((i+1)<this.length) {
                result = result + separator;
            }
        }
        return result;
    }

    object.sort = function() {
        var modify = true;
        if (arguments.length > 0 && arguments[0] == false) {
            modify = false;
        }
        var i,copy = [];
        //Maybe some closure as last parameter
        var tempFunction = null;
        if (arguments.length == 2 && typeof arguments[1] === "function") {
            tempFunction = arguments[1];
        }
        if (arguments.length == 1 && typeof arguments[0] === "function") {
            tempFunction = arguments[0];
        }
        //Copy all items
        for (i=0;i<this.length;i++) {
            //if (tempFunction!=null && tempFunction.length == 1) {
                //If closure has 1 parameter we apply it to all items
            //    [i] = tempFunction(this[i]);
            //}
            copy[i] = this[i];
        }
        //console.log('tempFunction->'+tempFunction);
        //If function has 2 parameter, inside compare both and return a number
        if (tempFunction!=null && tempFunction.length == 2) {
            copy.sort(tempFunction);
        }
        //If function has 1 parameter, we have to compare transformed items
        if (tempFunction!=null && tempFunction.length == 1) {
            copy.sort(function(a, b) {
                return gSspaceShip(tempFunction(a),tempFunction(b));
            });
        }
        if (tempFunction==null) {
            //console.log('Before-'+copy);
            copy.sort();
            //console.log('After-'+copy);
        }
        var result = gSlist(copy);
        if (modify) {
            for (i=0;i<this.length;i++) {
                this[i] = copy[i];
            }
            //console.log('Modify'+this.toString());
            return this;
        } else {
            //console.log('Not Modify-'+copy);
            return gSlist(copy);
        }
    }

    object.reverse = function() {
        var result;
        if (arguments.length == 1 && arguments[0]==true) {
            var i,count=0;
            for (i=this.length-1;i>count;i--) {
                var temp = this[count];
                this[count++] = this[i];
                this[i] = temp;
            }
            return this;
        } else {
            result = [];
            var i,count=0;
            for (i=this.length-1;i>=0;i--) {
                result[count++] = this[i];
            }
            return gSlist(result);
        }
    }

    object.take = function(number) {
        var result = [];
        var i;
        for (i=0;i<number;i++) {
            if (i<this.length) {
                result[i] = this[i];
            }
        }

        return gSlist(result);
    }

    object.takeWhile =  function(closure) {
        var result = [];
        var i,exit=false;
        for (i=0;!exit && i<this.length;i++) {
            if (closure(this[i])) {
                result[i] = this[i];
            } else {
                exit = true;
            }
        }

        return gSlist(result);
    }

    object.multiply = function(number) {
        if (number==0) {
            return gSlist([]);
        } else {
            var i, result = gSlist([]);
            for (i=0;i<number;i++) {
                var j;
                for (j=0;j<this.length;j++) {
                    result.add(this[j]);
                }
            }
            //console.log('list multiply->'+result);
            return result;
        }
    }

    return object;
}

/////////////////////////////////////////////////////////////////
//gSrange - [x..y] from groovy
//Only works with numbers atm
/////////////////////////////////////////////////////////////////
function gSrange(begin,end,inclusive) {
    var start = begin;
    var finish = end;
    var reverse = false;
    if (finish<start) {
        start = finish;
        finish = begin;
        reverse = true;
        if (!inclusive) {
            start = start + 1;
        }
    } else {
        if (!inclusive) {
            finish = finish - 1;
        }
    }

    var result,number,count;
    for (result=[], number=start, count=0 ; number<=finish ; number++,count++) {
        result[count] = number;
    }
    if (reverse) {
        result = result.reverse()
    }
    var object = gSlist(result);
    object.toList = function() {
        return gSlist(this.values());
    }
    return object;
}

/////////////////////////////////////////////////////////////////
//gSdate - Date() object from groovy / java
/////////////////////////////////////////////////////////////////
function gSdate() {

    var object;
    if (arguments.length==1) {
        object = new Date(arguments[0]);
    } else {
        object = new Date();
    }

    object.time = object.getTime();

    object.year = object.getFullYear();
    object.month = object.getMonth();
    object.date = object.getDay();
    object.plus = function(other) {
        if (typeof other == 'number') {
            var a = gSdate(this.time+(other*1440000));
            return a;
        } else {
            return this + other;
        }
    }
    object.minus = function(other) {
        if (typeof other == 'number') {
            var a = gSdate(this.time-(other*1440000));
            return a;
        } else {
            return this + other;
        }
    }
    object.format = function(rule) {
        //TODO
        var exit = '';
        if (rule) {
            exit = rule;
            exit = exit.replaceAll('yyyy',this.getFullYear());
            exit = exit.replaceAll('MM',gSfillZerosLeft(this.getMonth()+1,2));
            exit = exit.replaceAll('dd',gSfillZerosLeft(this.getUTCDate(),2));
            exit = exit.replaceAll('HH',gSfillZerosLeft(this.getHours(),2));
            exit = exit.replaceAll('mm',gSfillZerosLeft(this.getMinutes(),2));
            exit = exit.replaceAll('ss',gSfillZerosLeft(this.getSeconds(),2));
            exit = exit.replaceAll('yy',gSlastChars(this.getFullYear(),2));
        }
        return exit;
    }

    return object;
}

//TODO have to work on this
function gSrangeFromList(list,begin,end) {
    return list.slice(begin,end+1)
}

function gSfillZerosLeft(item,size) {
    var value = item + '';
    while (value.length<size) {
        value = '0'+value;
    }
    return value;
}

function gSlastChars(item,number) {
    var value = item + '';
    value = value.substring(value.length-number);
    return value;
}


/////////////////////////////////////////////////////////////////
//gSexactMatch - For regular expressions
/////////////////////////////////////////////////////////////////
function gSexactMatch(text,regExp) {
    var mock = text;
    //console.log('r->'+regExp instanceof RegExp);
    //console.log('t->'+text instanceof String);
    if (regExp instanceof RegExp) {
        mock = mock.replace(regExp,"#");
    } else {
        mock = mock.replace(new RegExp(regExp),"#");
    }

    //console.log('After->'+mock);
    return mock == "#";
}

function gSmatch(text,regExp) {
    var pos;

    if (regExp instanceof RegExp) {
        pos = text.search(regExp)
    }

    //console.log('After->'+pos+' - '+text+' - '+regExp);
    return (pos>=0);
}


/////////////////////////////////////////////////////////////////
//gSregExp - For regular expressions
/////////////////////////////////////////////////////////////////
function gSregExp(text,ppattern) {

    var patt;
    if (ppattern instanceof RegExp) {
        patt = new RegExp(ppattern.source,'g');
    } else {
        //g for search all occurences
        patt = new RegExp(ppattern,'g');
    }

    var object;

    //var object;
    var data = patt.exec(text);//text.match(patt);
    //console.log('data->'+data);
    if (data==null || data=='undefined') {
        return null;
    } else {
        var list = [];
        var i = 0;

        while (data!=null && data!='undefined') {
            //console.log('adding data->'+data);
            if (data instanceof Array && data.length>1) {
                list[i] = gSlist(data);
            } else {
                list[i] = data;
            }
            i = i + 1;
            data = patt.exec(text);
        }
        object = inherit(gSlist(list),'RegExp');
    }

    object.pattern = patt;
    object.text = text;

    object.replaceFirst = function(data) {
        return this.text.replaceFirst(this[0],data);
    }

    object.replaceAll = function(data) {
        return this.text.replaceAll(this.pattern,data);
    }

    object.reset = function() {
        return this;
    }

    return object;
}

/////////////////////////////////////////////////////////////////
//Pattern
/////////////////////////////////////////////////////////////////
function gSpattern(pattern) {
    var object = inherit(gsBaseClass,'Pattern');
    object.value = pattern;
    return object;
}

/////////////////////////////////////////////////////////////////
// Regular Expresions - For regular expressions
/////////////////////////////////////////////////////////////////
function gSmatcher(item,regExpression) {

    var object = inherit(gsBaseClass,'Matcher');

    object.data = item;
    object.regExp = regExpression;

    object.matches = function() {
        return gSexactMatch(this.data,this.regExp);
    }

    return object;
}

RegExp.prototype.matcher = function(item) {
    return gSmatcher(item,this);
}

/////////////////////////////////////////////////////////////////
//Number functions
/////////////////////////////////////////////////////////////////
Number.prototype.times = function(closure) {
    var i;
    for (i=0;i<this;i++) {
        closure(i);
    }
}

Number.prototype.upto = function(number,closure) {
    var i;
    for (i=this.value;i<=number;i++) {
        closure(i);
    }
}

Number.prototype.step = function(number,jump,closure) {
    var i;
    for (i=this.value;i<number;) {
        closure(i);
        i=i+jump;
    }
}


/////////////////////////////////////////////////////////////////
//String functions
/////////////////////////////////////////////////////////////////
String.prototype.contains = function(value) {
    return this.indexOf(value)>=0;
}

String.prototype.startsWith = function(value) {
    return this.indexOf(value)==0;
}

String.prototype.endsWith = function(value) {
    return this.indexOf(value)==(this.length - value.length);
}

String.prototype.count = function(value) {
    var reg = new RegExp(value,'g');
    var result = this.match(reg);
    if (result!=null && result!='undefined') {
        return result.length;
    } else {
        return 0;
    }
}

String.prototype.size = function() {
    return this.length;
}

String.prototype.replaceAll = function(oldValue,newValue) {
    var reg;
    if (oldValue instanceof RegExp) {
        reg = new RegExp(oldValue.source,'g');
    } else {
        reg = new RegExp(oldValue,'g');
    }
    return this.replace(reg,newValue);
}

String.prototype.replaceFirst = function(oldValue,newValue) {
    return this.replace(oldValue,newValue);
}


String.prototype.reverse = function() {
    return this.split("").reverse().join("");
}

String.prototype.tokenize = function() {
    var str = " ";
    if (arguments.length==1 && arguments[0]!=null && arguments[0]!=undefined) {
        str = arguments[0];
    }
    var list = this.split(str);
    return gSlist(list);
}

String.prototype.multiply = function(value) {
    if (typeof(value)=='number') {
        var result = '';
        var i;
        for (i=0;i<(value | 0);i++) {
            result = result + this;
        }

        return result;
    }
}

String.prototype.eachLine = function(closure) {
    var items = this.split('\n');
    var i;
    for (i=0;i<items.length;i++) {
        var item = items[i];
        //Closure with 2 arguments, line and count
        if (closure.length == 2) {
            closure(item,i);
        } else {
            closure(item);
        }
    }
}

String.prototype.readLines = function() {
    var items = this.split('\n');
    return gSlist(items);
}

String.prototype.padRight = function(number) {
    var sep = ' ';
    if (arguments.length==2) {
        sep = arguments[1];
    }
    var item = this;
    while (item.length<number) {
        item = item + sep;
    }
    return item;
}

String.prototype.padLeft = function(number) {
    var sep = ' ';
    if (arguments.length==2) {
        sep = arguments[1];
    }
    var item = this;
    while (item.length<number) {
        item = sep + item;
    }
    return item;
}

String.prototype.isNumber = function() {
    if (this.trim() == '') {
        return false;
    } else {
        var res = Number(this);
        //console.log('res->'+res+' is '+ isNaN(res));
        if (isNaN(res)) {
            return false;
        } else {
            return true;
        }

    }
}

/////////////////////////////////////////////////////////////////
// Misc Functions
/////////////////////////////////////////////////////////////////
function gSclassForName(name) {
    var result = null;
    try {
        var pos = name.indexOf(".");
        while (pos>=0) {
            name = name.substring(pos+1);
            pos = name.indexOf(".");
        }
        //console.log('Evaluating->'+name);
        result = eval(name);
    } catch (err) {
        result = null;
    }

    return result;
}

function gSstaticMethods(item) {
    this.gSparent = item;
}

var gSglobalMetaClass = {};
function gSmetaClass(item) {
    var type = typeof item;
    //console.log('typeof before-'+typeof item);
    //If type is a function, it's metaClass from a Class
    if (type === "function") {
        if (!gSglobalMetaClass[item.name]) {
            gSglobalMetaClass[item.name] = {
                gSstatic: new gSstaticMethods(item),
                getStatic : function() {
                    return this.gSstatic;
                }
            };
        }
        item = gSglobalMetaClass[item.name];
    }

    if (type == "string") {
        item = new String(item);
    }
    if (type == "number") {
        item = new Number(item);
    }
    //console.log('typeof after-'+typeof item);

    return item;
}

function gSpassMapToObject(source,destination) {
    for (prop in source) {
        if (typeof source[prop] === "function") continue;
        //destination[prop] = source[prop];
        gSsetProperty(destination,prop,source[prop]);
    }
}

function gSequals(value1, value2) {
    //console.log('going eq:'+value1+ ' = '+value2+' -> '+value1.equals);
    if (value1==null || value1=='undefined' || value1.equals=='undefined' || value1.equals==null || !(typeof value1.equals === "function")) {
        //console.log(' 1 ');
        if (value2!=null && value2!='undefined' && value2.equals!='undefined' && value2.equals!=null && (typeof value2.equals === "function")) {
            return value2.equals(value1);
        } else {
            return value1==value2;
        }
    } else {
        //console.log(' 2 ');
        return value1.equals(value2);
    }
}

function gSinterceptClosureCall(func, param) {
    if ((param instanceof Array) && func.length>1) {
        func.apply(func,param);
    } else {
        func(param);
    }
}

function gSrandom() {
    var object = inherit(gsBaseClass,'Random');
    object.nextInt = function(number) {
        var ran = Math.ceil(Math.random()*number);
        return ran - 1;
    }
    object.nextBoolean = function() {
        var ran = Math.random();
        return ran < 0.5;
    }
    return object;
};

function gSbool(item) {
    //console.log('item->'+item+' - '+item.isEmpty+' - '+(item.isEmpty === "function"));
    //console.log('type->'+typeof(item));
    if (item!=null && item!='undefined' && item.isEmpty!=null) {
        //console.log('bool yeah->'+!item.isEmpty());
        return !item.isEmpty();
    } else {
        if (typeof(item)=='number' && item==0) {
            return false;
        } else if (typeof(item)=='string' && item=='') {
            return false;
        } else if (typeof(item)=='string' && item!='') {
            return true;
        }
        return item;
    }
}

function gSless(itemLeft,itemRight) {
    return itemLeft < itemRight;
}

function gSgreater(itemLeft,itemRight) {
    return itemLeft > itemRight;
}

// Operator <=>
function gSspaceShip(itemLeft, itemRight) {
    if (gSequals(itemLeft,itemRight)) {
        return 0;
    }
    if (gSless(itemLeft,itemRight)) {
        return -1;
    }
    if (gSgreater(itemLeft,itemRight)) {
        return 1;
    }
}

//InstanceOf function
function gSinstanceOf(item,name) {
    var classItem;
    var gotIt = false;

    if (name=="java.lang.String")  {
        return typeof(item)=='string';
    } else if (name=="java.lang.Number") {
        return typeof(item)=='number';
    } else if (item.gSclass) {
        classItem = item.gSclass;
        while (classItem!=null && classItem!='undefined' && !gotIt) {
            if (classItem.name == name || classItem.simpleName == name) {
                gotIt = true;
            } else {
                classItem = classItem.superclass;
            }
        }
    }
    return gotIt;
}

//Elvis operator
function gSelvis(booleanExpression,trueExpression,falseExpression) {
    if (gSbool(booleanExpression)) {
        return trueExpression;
    } else {
        return falseExpression;
    }
}

// * operator
function gSmultiply(a,b) {
     if (a==null || a=='undefined' || a.multiply=='undefined' || a.multiply==null || !(typeof a.multiply === "function")) {
          if (b==null || b=='undefined' || b.multiply=='undefined' || b.multiply==null || !(typeof b.multiply === "function")) {
            return a*b;
          } else {
            return b.multiply(a);
          }

     } else {
        return a.multiply(b);
     }
}

// + operator
function gSplus(a,b) {
    if (a==null || a=='undefined' || a.plus=='undefined' || a.plus==null || !(typeof a.plus === "function")) {
        if (b==null || b=='undefined' || b.plus=='undefined' || b.plus==null || !(typeof b.plus === "function")) {
            return a+b;
        } else {
            return b.plus(a);
        }

    } else {
        return a.plus(b);
    }
}

// - operator
function gSminus(a,b) {
    if (a==null || a=='undefined' || a.minus=='undefined' || a.minus==null || !(typeof a.minus === "function")) {
        return a-b;
    } else {
        //console.log('a.minus(b)'+a+' '+b);
        return a.minus(b);
    }
}

// in operator
function gSin(item,group) {
    if (group!=null && group !='undefined' && (typeof group.contains === "function")) {
        return group.contains(item);
    } else {
        return false
    }
}

//For some special cases where access a property with this."${name}"
//This can be a closure
function gSthisOrObject(thisItem,objectItem) {
    //this can only be used for our objects, our object must have gSwith function
    if (thisItem['gSwith']==undefined && objectItem!=null && objectItem!=undefined) {
        return objectItem;
    } else {
        return thisItem;
    }
}

/////////////////////////////////////////////////////////////////
// Beans functions - From groovy beans
/////////////////////////////////////////////////////////////////

//Set a property of a class
function gSsetProperty(item,nameProperty,value) {

    if (nameProperty=='setProperty') {
        item[nameProperty] = value;
    } else if (nameProperty=='getProperty') {
        item[nameProperty] = value;
    } else if (item!=null && item instanceof gSstaticMethods) {
        //console.log('Setting static!');
        item[nameProperty] = value;
        item.gSparent[nameProperty] = value;
    } else {

        if (item['setProperty']=='undefined' || item['setProperty']==null || !(typeof item['setProperty'] === "function")) {

            var nameFunction = 'set' + nameProperty.charAt(0).toUpperCase() + nameProperty.slice(1);

            if (item[nameFunction]=='undefined' || item[nameFunction]==null || !(typeof item[nameFunction] === "function")) {
                //console.log('Setting->'+item+' - '+nameProperty+' - '+value);
                item[nameProperty] = value;
            } else {
                item[nameFunction](value);
            }
        } else {
            item.setProperty(nameProperty,value)
        }
    }
    //return value;
}

//Calling a setMethod
function gSsetMethod(item,methodName,value) {

    if (item[methodName]=='undefined' || item[methodName]==null || !(typeof item[methodName] === "function")) {

        var nameProperty = methodName.charAt(3).toLowerCase() + methodName.slice(4);
        item[nameProperty] = value;
    } else {
        item[methodName](value);
    }

}

//Calling a getMethod
function gSgetMethod(item,methodName) {

    if (item[methodName]=='undefined' || item[methodName]==null || !(typeof item[methodName] === "function")) {

        var nameProperty = methodName.charAt(3).toLowerCase() + methodName.slice(4);
        var res = function () { return item[nameProperty];}
        return res;

    } else {
        return item[methodName];
    }

}

//Get a property of a class
function gSgetProperty(item,nameProperty) {

    //console.log('item->'+item+' property->'+nameProperty);

    if (item['getProperty']=='undefined' || item['getProperty']==null || !(typeof item['getProperty'] === "function")) {

        var nameFunction = 'get' + nameProperty.charAt(0).toUpperCase() + nameProperty.slice(1);
        //console.log('Name func->'+nameFunction);
        if (item[nameFunction]=='undefined' || item[nameFunction]==null || !(typeof item[nameFunction] === "function")) {
            if (typeof item[nameProperty] === "function" && nameProperty == 'size') {
                return item[nameProperty]();
            } else {
                //console.log('property------'+nameProperty+' = '+item[nameProperty]);
                if (item[nameProperty]!=undefined) {
                    return item[nameProperty];
                } else {
                    if (item['gSdefaultValue']!=undefined && (typeof item['gSdefaultValue'] === "function")) {
                        item[nameProperty] = item['gSdefaultValue']();
                    }
                    return item[nameProperty];
                }
            }
        } else {
            //console.log('Got it! Name func->'+nameFunction);
            return item[nameFunction]();
        }
    } else {
        //console.log('ah');
        return item.getProperty(nameProperty)
    }

}

//Control property changes with ++,--
function gSplusplus(item,nameProperty,plus,before) {
    var value = gSgetProperty(item,nameProperty);
    var newValue = value;
    if (plus) {
        gSsetProperty(item,nameProperty,value + 1);
        newValue++;
    } else {
        gSsetProperty(item,nameProperty,value - 1)
        newValue--;
    }
    if (before) {
        return newValue;
    } else {
        return value;
    }
}

//Control all method calls
function gSmethodCall(item,methodName,values) {

    //console.log('Going!->'+methodName);
    if (gSconsoleInfo && console) {
        console.log('[INFO] gSmethodCall ('+item+').'+methodName+ ' params:'+values);
    }

    if (typeof(item)=='string' && methodName=='split') {
        return item.tokenize(values[0]);
    }
    if (typeof(item)=='string' && methodName=='length') {
        return item.length;
    }
    if ((item instanceof Array) && methodName=='join') {
        if (values.size()>0) {
            return item.gSjoin(values[0]);
        } else {
            return item.gSjoin();
        }
    }

    if (item[methodName]=='undefined' || item[methodName]==null || !(typeof item[methodName] === "function")) {

        //console.log('Not Going! '+methodName+ ' - '+item);
        //var nameProperty = methodName.charAt(3).toLowerCase() + methodName.slice(4);
        //var res = function () { return item[nameProperty];}
        //return res;

        if (methodName.startsWith('get') || methodName.startsWith('set')) {
            var varName = methodName.charAt(3).toLowerCase() + methodName.slice(4);
            var properties = item.getProperties();
            if (properties.contains(varName)) {
                if (methodName.startsWith('get')) {
                    return gSgetProperty(item,varName);
                } else {
                    return gSsetProperty(item,varName,values[0]);
                }

            }
        }

        //Check newInstance
        if (methodName=='newInstance') {
            return item();
        } else {

            if (item['methodMissing']) {
               return item['methodMissing'](methodName,values);
            } else {
                //Not exist the method, throw exception
                throw 'gSmethodCall Method '+ methodName + ' not exist in '+item;
            }
        }

    } else {
        var f = item[methodName];
        return f.apply(item,values);
    }
}
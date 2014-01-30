(function() {
    var gs = function(obj) {
        if (obj instanceof gs) return obj;
        if (!(this instanceof gs)) return new gs(obj);
    };
    var root = this;

    if (typeof exports !== 'undefined') {
        if (typeof module !== 'undefined' && module.exports) {
            exports = module.exports = gs;
        }
        exports.gs = gs;
    } else {
        root.gs = gs;
    }

    //Fails
    gs.fails = false;
    //Local console
    gs.consoleData = '';
    //If true and console is available, all output will go through console
    gs.consoleOutput = true;
    //If true and console is available, some methods will show info on console
    gs.consoleInfo = false;

    var globalMetaClass = {};

    //Categories
    var categories = [];

    // Mixins
    var mixins = [];
    var mixinsObjects = [];

    //Delegate
    var actualDelegate = null;

    gs.myCategories = {};

    /////////////////////////////////////////////////////////////////
    // assert and println
    /////////////////////////////////////////////////////////////////
    gs.assert = function(value) {
        if(value === false) {
            gs.fails = true;
            var message = 'Assert Fails! - ';
            if (arguments.length == 2 && arguments[1] !== null) {
                message = arguments[1] + ' - ';
            }
            gs.println(message+value);
        }
    };

    //Function that used for print and println in groovy
    gs.println = function(value) {
        if (gs.consoleOutput && console) {
            console.log(value);
        } else {
            if (gs.consoleData !== "") {
                gs.consoleData = gs.consoleData + "\n";
            }
            gs.consoleData = gs.consoleData + value;
        }
    };

    //TODO We don't know if a function is constructor, atm if function name starts with uppercase, it is
    function isConstructor(name, func) {
        return name[0] == name[0].toUpperCase();
    }

    /////////////////////////////////////////////////////////////////
    // Class functions
    /////////////////////////////////////////////////////////////////
    gs.baseClass = {
        //The with function, with is a reserved word in JavaScript
        withz : function(closure) { closure.apply(this,closure.arguments); },
        getProperties : function() {
            var result = gs.list([]), ob;
            for (ob in this) {
                if (typeof this[ob] !== "function" && ob != 'clazz') {
                    result.add(ob);
                }
            }
            return result;
        },
        getMethods : function() {
            var result = gs.list([]), ob;
            for (ob in this) {
                if (typeof this[ob] === "function") {

                    if (ob!='getStatic' && ob!='withz' && ob!='getProperties' && ob!='getMethods' && ob!='constructor' &&
                        !(isConstructor(ob, this[ob]))) {
                        var item = {
                            name: ob
                        };
                        result.add(item);
                    }
                }
            }
            return result;
        },
        invokeMethod: function(name,values) {
            var i,newArgs = [];
            if (values !== null && values !== undefined) {
                for (i=0; i < values.length ; i++) {
                    newArgs[i] = values[i];
                }
            }
            var f = this[name];
            return f.apply(this,newArgs);
        },
        constructor : function() {
            return this;
        }
    };

    function isObjectProperty(name) {
        return ['clazz','gSdefaultValue','leftShift',
            'minus','plus','equals','toString',
            'clone','withz','getProperties',
            'getMethods','invokeMethod','constructor'].indexOf(name) >= 0;
    }

    gs.expando = function() {
        var object = gs.inherit(gs.baseClass, 'Expando');

        object.constructorWithMap = function(map) { gs.passMapToObject(map,this); return this;};
        if (arguments.length==1) {object.constructorWithMap(arguments[0]); }

        return object;
    };

    gs.expandoMetaClass = function() {
        var object = gs.inherit(gs.baseClass,'ExpandoMetaClass');
        object.initialize = function() {
            return this;
        };
        return object;
    };

    function expandWithMetaclass(item, objectName) {
        if (globalMetaClass !== undefined && globalMetaClass[objectName] !== null && globalMetaClass[objectName] !== undefined) {
            var obj,map = globalMetaClass[objectName];
            for (obj in map) {

                //Static methods
                var staticMap = map.getStatic();
                if (staticMap !== null && staticMap !== undefined) {
                    var objStatic;
                    for (objStatic in staticMap) {
                        if (objStatic != 'gSparent') {
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

    gs.inherit = function(p,objectName) {
    //    function inherit(p,objectName) {
        if (p === null) throw TypeError();
        if (Object.create) {
            return expandWithMetaclass(Object.create(p),objectName);
        }
        var t = typeof p;

        // If Object.create() is defined... // then just use it.
        // Otherwise do some more type checking
        if (t !== "object" && t !== "function") {
            throw TypeError();
        }
        function f() {}
        f.prototype = p;
        return expandWithMetaclass(new f(),objectName);
    };

    function createClassNames(item, items) {
        var number = items.length, i, container;
        for (i=0; i < number ; i++) {
            if (i === 0) {
                container = {};
                item.clazz = container;
            }
            container.name = items[i];
            container.simpleName = getSimpleName(items[i]);
            if (i < number) {
                container.superclass = {};
                container = container.superclass;
            }
        }
    }

    function getSimpleName(name) {
        var pos = name.indexOf(".");
        while (pos >= 0) {
            name = name.substring(pos + 1);
            pos = name.indexOf(".");
        }
        return name;
    }

    /////////////////////////////////////////////////////////////////
    // set - as Set and HashSet from groovy
    /////////////////////////////////////////////////////////////////
    gs.set = function(value) {
        var object;

        if (arguments.length === 0) {
            object = gs.list([]);
        } else {
            object = value;
        }

        createClassNames(object,['java.util.HashSet']);

        object.isSet = true;

        object.withz = function(closure) {
            interceptClosureCall(closure, this);
        };

        object.add = function(item) {
            if (!(this.contains(item))) {
                this[this.length]=item;
                return this;
            } else {
                return false;
            }
        };

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
        };

        object.equals = function(other) {
            if (!(other instanceof Array) || other.length!=this.length || !(other.isSet)) {
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
        };

        object.toList = function() {
            var i,list = [];
            for (i=0;i<this.length;i++) {
                list[i] = this[i];
            }
            return gs.list(list);
        };

        object.plus = function(other) {
            var result = gs.set();
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
        };

        object.minus = function(other) {
            var result = gs.set();
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
        };

        object.remove = function(value) {
            var index = this.indexOf(value);
            if (index>=0) {
                this.splice(index,1);
            }
            return this;
        };

        return object;
    };

    /////////////////////////////////////////////////////////////////
    // map - [:] from groovy
    /////////////////////////////////////////////////////////////////
    function isMapProperty(name) {
        return ['clazz','gSdefaultValue','any','collect',
            'collectEntries','collectMany','countBy','dropWhile',
            'each','eachWithIndex','every','find','findAll',
            'findResult','findResults','get','getAt','groupBy',
            'inject','intersect','leftShift','max','min',
            'minus','plus','putAll','putAt','reverseEach',
            'sort','spread','subMap','add','take','takeWhile',
            'withDefault','count','drop','equals','toString',
            'put','size','isEmpty','remove','containsKey',
            'containsValue','values','clone','withz','getProperties',
            'getMethods','invokeMethod','constructor'].indexOf(name) >= 0;
    }

    gs.map = function() {
        var object = new GsGroovyMap();
        //gs.inherit(gs.baseClass,'LinkedHashMap');
        expandWithMetaclass(object, 'LinkedHashMap');

        return object;
    };

    function GsGroovyMap() {
        this.clazz = { name: 'java.util.LinkedHashMap', simpleName: 'LinkedHashMap',
            superclass: { name: 'java.util.HashMap', simpleName: 'HashMap'}};
        this.add = function(key,value) {
            if (key=="spreadMap") {
                //We insert items of the map, from spread operator
                var ob;
                for (ob in value) {
                    if (!isMapProperty(ob)) {
                        this[ob] = value[ob];
                    }
                }
            } else {
                this[key] = value;
            }
            return this;
        };
        this.put = function(key,value) {
            return this.add(key,value);
        };
        this.leftShift = function(key,value) {
            if (arguments.length == 1) {
                return this.plus(arguments[0]);
            } else {
                return this.add(key,value);
            }
        };
        this.putAt = function(key,value) {
            this.put(key,value);
        };
        this.size = function() {
            var number = 0,ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    number++;
                }
            }
            return number;
        };
        this.isEmpty = function() {
            return (this.size() === 0);
        };
        this.remove = function(key) {
            if (this[key]) {
                delete this[key];
            }
        };
        this.each = function(closure) {
            var ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
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
        };

        this.count = function(closure) {
            var number = 0, ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
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
        };

        this.any = function(closure) {
            var ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
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
        };

        this.every = function(closure) {
            var ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
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
        };

        this.find = function(closure) {
            var ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
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
        };

        this.findAll = function(closure) {
            var result = gs.map(), ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    var f = arguments[0];
                    if (f.length == 1) {
                        var entry = {key:ob, value:this[ob]};
                        if (closure(entry)) {
                            result.add(entry.key, entry.value);
                        }
                    }
                    if (f.length==2) {
                        if (closure(ob,this[ob])) {
                            result.add(ob, this[ob]);
                        }
                    }
                }
            }
            if (result.size()>0) {
                return result;
            } else {
                return null;
            }
        };

        this.collect = function(closure) {
            var result = gs.list([]), ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
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
        };

        this.containsKey = function(key) {
            if (this[key] === undefined || this[key] === null) {
                return false;
            } else {
                return true;
            }
        };

        this.containsValue = function(value) {
            var ob, gotIt = false;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    if (gs.equals(this[ob],value)) {
                        gotIt = true;
                        break;
                    }
                }
            }
            return gotIt;
        };

        this.get = function(key, defaultValue) {
            if (!this.containsKey(key)) {
                this[key] = defaultValue;
            }
            return this[key];
        };

        this.toString = function() {
            var items = '';
            this.each (function(key,value) {
                items = items + key+': '+value+' ,';
            });
            return '[' + items + ']';
        };

        this.equals = function(otherMap) {

            var result = true, ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    if (!gs.equals(this[ob],otherMap[ob])) {
                        result = false;
                    }
                }
            }
            return result;
        };

        this.values = function() {
            var result = gs.list([]), ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    result.add(this[ob]);
                }
            }
            return result;
        };

        this.gSdefaultValue = null;

        this.withDefault = function(closure) {
            this.gSdefaultValue = closure;
            return this;
        };

        this.inject = function(initial,closure) {
            var ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    if (closure.length == 2) {
                        var entry = {key:ob, value:this[ob]};
                        initial = closure(initial, entry);
                    }
                    if (closure.length == 3) {
                        initial = closure(initial, ob, this[ob]);
                    }
                }
            }
            return initial;
        };

        this.putAll = function (items) {
            if (items instanceof Array) {
                var i;
                for (i=0;i<items.length;i++) {
                    var item = items[i];
                    this.add(item.key,item.value);
                }
            } else {
                var ob;
                for (ob in items) {
                    if (!isMapProperty(ob)) {
                        this.add(ob,items[ob]);
                    }
                }
            }
            return this;
        };

        this.plus = function(other) {
            var result = this.clone();
            if (other instanceof Array) {
                result.putAll(other);
            } else {
                var ob;
                for (ob in other) {
                    if (!isMapProperty(ob)) {
                        result.add(ob,other[ob]);
                    }
                }
            }
            return result;
        };

        this.clone = function() {
            var result = gs.map(), ob;
            for (ob in this) {
                if (!isMapProperty(ob)) {
                    result.add(ob, this[ob]);
                }
            }
            return result;
        };

        this.minus = function(other) {
            var result = this.clone(), ob;
            for (ob in other) {
                if (!isMapProperty(ob)) {
                    if (result[ob] !== null && result[ob] !==undefined && gs.equals(result[ob],other[ob])) {
                        delete result[ob];
                    }
                }
            }
            return result;
        };
    }

    /////////////////////////////////////////////////////////////////
    // Array prototype changes
    /////////////////////////////////////////////////////////////////
    Array.prototype.get = function(pos) {

        //Maybe comes a second parameter with default value
        if (arguments.length==2) {
            //console.log('uh->'+this[pos]);
            if (this[pos] === null || this[pos] === undefined) {
                return arguments[1];
            } else {
                return this[pos];
            }
        } else {
            return this[pos];
        }
    };

    Array.prototype.getAt = function(pos) {
        return this[pos];
    };

    Array.prototype.withz = function(closure) {
        interceptClosureCall(closure, this);
    };

    Array.prototype.size = function() {
        return this.length;
    };

    Array.prototype.isEmpty = function() {
        return this.length === 0;
    };

    Array.prototype.add = function(element) {
        this[this.length]=element;
        return this;
    };

    Array.prototype.addAll = function(elements) {
        var i;
        if (arguments.length == 1) {
            if (elements instanceof Array) {
                for (i = 0; i < elements.length; i++) {
                    this.add(elements[i]);
                }
            } else {
                this[this.length] = elements;
            }
        } else {
            //Two parameters index and collection
            var index = arguments[0];
            var data = arguments[1];
            for (i=0; i < data.length; i++) {
                this.splice(index+i, 0, data[i]);
            }
        }
        return true;
    };

    Array.prototype.clone = function() {
        var result = gs.list([]);
        result.addAll(this);
        return result;
    };

    Array.prototype.plus = function(other) {
        var result = this.clone();
        result.addAll(other);
        return result;
    };

    Array.prototype.minus = function(other) {
        var result = this.clone();
        result.removeAll(other);
        return result;
    };

    Array.prototype.leftShift = function(element) {
        return this.add(element);
    };

    Array.prototype.contains = function(object) {
        var gotIt, i;
        for (i=0; !gotIt && i < this.length; i++) {
            if (gs.equals(this[i], object)) {
                gotIt = true;
            }
        }
        return gotIt;
    };

    Array.prototype.each = function(closure) {
        var i;
        for (i=0;i<this.length;i++) {
            //TODO Beware this change, have to apply to all closure calls
            interceptClosureCall(closure, this[i]);
        }
        return this;
    };

    Array.prototype.reverseEach = function(closure) {
        var i;
        for (i=this.length-1;i>=0;i--) {
            interceptClosureCall(closure, this[i]);
        }
        return this;
    };

    Array.prototype.eachWithIndex = function(closure,index) {
        for (index=0;index<this.length;index++) {
            closure(this[index],index);
        }
        return this;
    };

    Array.prototype.any = function(closure) {
        var i;
        for (i=0;i<this.length;i++) {
            if (closure(this[i])) {
                return true;
            }
        }
        return false;
    };

    Array.prototype.values = function() {
        var result = [];
        var i;
        for (i=0;i<this.length;i++) {
            result[i]=this[i];
        }
        return result;
    };
    //Remove only 1 item from the list
    Array.prototype.remove = function(indexOrValue) {
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
    };

    //Maybe too much complex, not much inspired
    Array.prototype.removeAll = function(data) {
        if (data instanceof Array) {
            var result = [];
            this.forEach(function(v, i, a) {
                if (data.contains(v)) {
                    result.push(i);
                }
            });
            //Now in result we have index of items to delete
            if (result.length>0) {
                var decremental = 0;
                var thisList = this;
                result.forEach(function(v, i, a) {
                    //Had tho change this for thisList, other scope on this here
                    thisList.splice(v-decremental,1);
                    decremental=decremental+1;
                });
            }
        } else if (typeof data === "function") {
            var i;
            for (i=this.length-1;i>=0;i--) {
                if (data(this[i])) {
                    this.remove(i);
                }
            }
        }
        return this;
    };

    Array.prototype.collect = function(closure) {
        var result = gs.list([]);
        var i;
        for (i=0;i<this.length;i++) {
            result[i] = closure(this[i]);
        }
        return result;
    };

    Array.prototype.collectMany = function(closure) {
        var result = gs.list([]);
        var i;
        for (i=0;i<this.length;i++) {
            result.addAll(closure(this[i]));
        }
        return result;
    };

    Array.prototype.takeWhile = function(closure) {
        var result = gs.list([]);
        var i;
        for (i=0;i<this.length;i++) {
            if (closure(this[i])) {
                result[i] = this[i];
            } else {
                break;
            }
        }
        return result;
    };

    Array.prototype.dropWhile = function(closure) {
        var result = gs.list([]);
        var i,j=0, insert = false;
        for (i=0;i<this.length;i++) {
            if (!closure(this[i])) {
                insert=true;
            }
            if (insert) {
                result[j++] = this[i];
            }
        }
        return result;
    };

    Array.prototype.findAll = function(closure) {
        var values = this.filter(closure);
        return gs.list(values);
    };

    Array.prototype.find = function(closure) {
        var result,i;
        for (i=0;!result && i<this.length;i++) {
            if (closure(this[i])) {
                result = this[i];
            }
        }
        return result;
    };

    Array.prototype.first = function() {
        return this[0];
    };

    Array.prototype.head = function() {
        return this.first();
    };

    Array.prototype.last = function() {
        return this[this.length-1];
    };

    Array.prototype.sum = function() {

        var i, result = 0;
        //can pass a closure to sum
        if (arguments.length == 1) {
            for (i=0;i<this.length;i++) {
                result = result + arguments[0](this[i]);
            }
        } else {
            if (this.length>0 && this[0].plus) {
                var item = this[0];
                for (i=0;i+1<this.length;i++) {
                    item = item.plus(this[i+1]);
                }
                return item;
            } else {
                for (i = 0; i < this.length; i++) {
                    result = result + this[i];
                }
            }
        }
        return result;
    };

    Array.prototype.inject = function() {

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
            var j;
            for (j=0;j<this.length;j++) {
                acc = arguments[1](acc,this[j]);
            }
        }
        return acc;
    };

    Array.prototype.toList = function() {
        return this;
    };

    Array.prototype.intersect = function(otherList) {
        var result = gs.list([]);
        var i;
        for (i=0;i<this.length;i++) {
            if (otherList.contains(this[i])) {
                result.add(this[i]);
            }
        }
        return result;
    };

    Array.prototype.max = function() {
        var result = null;
        var i;
        for (i=0;i<this.length;i++) {
            if (result === null || this[i] > result) {
                result = this[i];
            }
        }
        return result;
    };

    Array.prototype.min = function() {
        var result = null;
        var i;
        for (i=0;i<this.length;i++) {
            if (result === null || this[i] < result) {
                result = this[i];
            }
        }
        return result;
    };

    Array.prototype.toString = function() {
        if (this.length>0) {
            var i;
            var result = '[';
            for (i=0;i<this.length-1;i++) {
                result = result + this[i] + ', ';
            }
            result = result + this[this.length-1] + ']';
            return result;
        } else {
            return '[]';
        }
    };

    Array.prototype.grep = function(param) {
        var i, result = gs.list([]);
        if (param instanceof RegExp) {
            for (i = 0; i < this.length; i++) {
                if (gs.match(this[i],param)) {
                    result.add(this[i]);
                }
            }
            return result;
        } else if (param instanceof Array) {
            return this.intersect(param);
        } else if (typeof param === "function") {
            for (i = 0; i < this.length; i++) {
                if (param(this[i])) {
                    result.add(this[i]);
                }
            }
            return result;
        } else {
            for (i = 0; i < this.length ;i++) {
                if (this[i]==param) {
                    result.add(this[i]);
                }
            }
            return result;
        }
    };

    Array.prototype.equals = function(other) {
        if (!(other instanceof Array) || other.length!=this.length) {
            return false;
        } else {
            var i;
            var result = true;
            for (i=0;i<this.length && result;i++) {
                if (!gs.equals(this[i],other[i])) {
                    result = false;
                }
            }
            return result;
        }
    };

    Array.prototype.gSjoin = function() {
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
    };

    Array.prototype.oldSort = Array.prototype.sort;

    Array.prototype.sort = function() {
        var modify = true;
        if (arguments.length > 0 && arguments[0] === false) {
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
            copy[i] = this[i];
        }
        //If function has 2 parameter, inside compare both and return a number
        if (tempFunction !== null && tempFunction.length == 2) {
            copy.oldSort(tempFunction);
        }
        //If function has 1 parameter, we have to compare transformed items
        if (tempFunction !== null && tempFunction.length == 1) {
            copy.oldSort(function(a, b) {
                return gs.spaceShip(tempFunction(a),tempFunction(b));
            });
        }
        if (tempFunction === null) {
            copy.oldSort();
        }
        if (modify) {
            for (i=0;i<this.length;i++) {
                this[i] = copy[i];
            }
            return this;
        } else {
            return gs.list(copy);
        }
    };

    Array.prototype.unique = function() {
        var modify = true;
        if (arguments.length > 0 && arguments[0] === false) {
            modify = false;
        }
        var i, copy = [];
        //Copy all items
        for (i = 0; i < this.length; i++) {
            if (!copy.contains(this[i])) {
                copy[copy.length] = this[i];
            }
        }

        if (modify) {
            this.length = 0;
            for (i = 0; i < copy.length; i++) {
                this[i] = copy[i];
            }
            return this;
        } else {
            return gs.list(copy);
        }
    };

    Array.prototype.reverse = function() {
        var i, count = 0;
        if (arguments.length == 1 && arguments[0] === true) {
            for (i = this.length - 1; i > count; i--) {
                var temp = this[count];
                this[count++] = this[i];
                this[i] = temp;
            }
            return this;
        } else {
            var result = [];
            for (i = this.length - 1; i >= 0; i--) {
                result[count++] = this[i];
            }
            return gs.list(result);
        }
    };

    Array.prototype.take = function(number) {
        var result = [];
        var i;
        for (i=0;i<number;i++) {
            if (i<this.length) {
                result[i] = this[i];
            }
        }
        return gs.list(result);
    };

    Array.prototype.takeWhile = function(closure) {
        var result = [];
        var i,exit=false;
        for (i=0;!exit && i<this.length;i++) {
            if (closure(this[i])) {
                result[i] = this[i];
            } else {
                exit = true;
            }
        }
        return gs.list(result);
    };

    Array.prototype.multiply = function(number) {
        if (number === 0) {
            return gs.list([]);
        } else {
            var i, result = gs.list([]);
            for (i=0;i<number;i++) {
                var j;
                for (j=0;j<this.length;j++) {
                    result.add(this[j]);
                }
            }
            return result;
        }
    };

    Array.prototype.flatten = function() {
        var result = gs.list([]);
        gs.flatten(result,this);

        return result;
    };

    Array.prototype.collate = function(number) {
        var step = number,times = 0;
        if (arguments.length == 2) {
            step = arguments[1];
        }
        var result = gs.list([]);
        while (step * times < this.length) {
            var items = gs.list([]);
            var pos = step * times;
            while (pos<this.length && items.size()<number) {
                items.add(this[pos++]);
            }
            result.add(items);
            times++;
        }
        return result;
    };

    /////////////////////////////////////////////////////////////////
    //list - [] from groovy
    /////////////////////////////////////////////////////////////////
    gs.list = function(value) {

        var data = [];

        if (value && value.length>0) {
            var i;
            for (i=0;i<value.length;i++) {
                if (value[i] instanceof gs.spread) {
                    var values = value[i].values;
                    if (values.length>0) {
                        var j;
                        for (j=0;j<values.length;j++) {
                            data[data.length]=values[j];
                        }
                    }
                } else {
                    data[data.length]=value[i];
                }
            }
        }
        var object = data;

        createClassNames(object,['java.util.ArrayList']);

        return object;
    };

    gs.flatten = function(result, list) {
        list.each(function (it) {
            if (it instanceof Array) {
                if (it.length>0) {
                    gs.flatten(result,it);
                }
            } else {
                result.add(it);
            }
        });
    };

    /////////////////////////////////////////////////////////////////
    //range - [x..y] from groovy
    /////////////////////////////////////////////////////////////////
    gs.range = function(begin, end, inclusive) {
        var start = begin;
        var finish = end;
        var areChars = (typeof(begin) == 'string');
        if (areChars) {
            start = start.charCodeAt(0);
            finish = finish.charCodeAt(0);
        }
        var reverse = false;
        if (finish < start) {
            var oldStart = start;
            start = finish;
            finish = oldStart;
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
            if (areChars) {
                result[count] = String.fromCharCode(number);
            } else {
                result[count] = number;
            }
        }
        if (reverse) {
            result = result.reverse();
        }
        var object = gs.list(result);
        object.toList = function() {
            return gs.list(this.values());
        };
        return object;
    };

    /////////////////////////////////////////////////////////////////
    //date - Date() object from groovy / java
    /////////////////////////////////////////////////////////////////
    gs.date = function() {

        var object;
        if (arguments.length==1) {
            object = new Date(arguments[0]);
        } else {
            object = new Date();
        }

        createClassNames(object,['java.util.Date']);

        object.time = object.getTime();

        object.year = object.getFullYear();
        object.month = object.getMonth();
        object.date = object.getDay();
        object.plus = function(other) {
            if (typeof other == 'number') {
                var a = gs.date(this.time+(other * 1440000));
                return a;
            } else {
                return this + other;
            }
        };
        object.minus = function(other) {
            if (typeof other == 'number') {
                var a = gs.date(this.time-(other * 1440000));
                return a;
            } else {
                return this + other;
            }
        };
        object.format = function(rule) {
            //TODO complete
            var exit = '';
            if (rule) {
                exit = rule;
                exit = exit.replaceAll('yyyy',this.getFullYear());
                exit = exit.replaceAll('MM',fillZerosLeft(this.getMonth()+1,2));
                exit = exit.replaceAll('dd',fillZerosLeft(this.getUTCDate(),2));
                exit = exit.replaceAll('HH',fillZerosLeft(this.getHours(),2));
                exit = exit.replaceAll('mm',fillZerosLeft(this.getMinutes(),2));
                exit = exit.replaceAll('ss',fillZerosLeft(this.getSeconds(),2));
                exit = exit.replaceAll('yy',lastChars(this.getFullYear(),2));
            }
            return exit;
        };
        object.parse = function(rule,text) {
            //TODO complete
            var pos = rule.indexOf('yyyy');
            if (pos>=0) {
                this.setFullYear(text.substr(pos,4));
            } else {
                pos = rule.indexOf('yy');
                if (pos>=0) {
                    this.setFullYear(text.substr(pos,2));
                }
            }
            pos = rule.indexOf('MM');
            if (pos>=0) {
                this.setMonth(text.substr(pos,2)-1);
            }
            pos = rule.indexOf('dd');
            if (pos>=0) {
                this.setUTCDate(text.substr(pos,2));
            }
            pos = rule.indexOf('HH');
            if (pos>=0) {
                this.setHours(text.substr(pos,2));
            }
            pos = rule.indexOf('mm');
            if (pos>=0) {
                this.setMinutes(text.substr(pos,2));
            }
            pos = rule.indexOf('ss');
            if (pos>=0) {
                this.setSeconds(text.substr(pos,2));
            }
            return this;
        };

        return object;
    };

    gs.rangeFromList = function(list, begin, end) {
        return list.slice(begin,end+1);
    };

    function fillZerosLeft(item,size) {
        var value = item + '';
        while (value.length<size) {
            value = '0'+value;
        }
        return value;
    }

    function lastChars(item,number) {
        var value = item + '';
        value = value.substring(value.length-number);
        return value;
    }

    /////////////////////////////////////////////////////////////////
    //exactMatch - For regular expressions
    /////////////////////////////////////////////////////////////////
    gs.exactMatch = function(text, regExp) {
        var mock = text;
        if (regExp instanceof RegExp) {
            mock = mock.replace(regExp, "#");
        } else {
            mock = mock.replace(new RegExp(regExp), "#");
        }
        return mock == "#";
    };

    gs.match = function(text, regExp) {
        var pos;
        if (regExp instanceof RegExp) {
            pos = text.search(regExp);
        }
        return (pos>=0);
    };

    /////////////////////////////////////////////////////////////////
    //regExp - For regular expressions
    /////////////////////////////////////////////////////////////////
    gs.regExp = function(text, ppattern) {

        var patt;
        if (ppattern instanceof RegExp) {
            patt = new RegExp(ppattern.source, 'g');
        } else {
            //g for search all occurences
            patt = new RegExp(ppattern, 'g');
        }

        var object;

        var data = patt.exec(text);
        if (data === null || data === undefined) {
            return null;
        } else {
            var list = gs.list([]);
            var i = 0;

            while (data !== null && data !== undefined) {
                if (data instanceof Array && data.length<2) {
                    list[i] = data[0];
                } else {
                    list[i] = gs.list(data);
                }
                i = i + 1;
                data = patt.exec(text);
            }
            object = gs.inherit(list, 'RegExp');
        }

        createClassNames(object,['java.util.regex.Matcher']);

        object.pattern = patt;
        object.text = text;

        object.replaceFirst = function(data) {
            return this.text.replaceFirst(this[0],data);
        };

        object.replaceAll = function(data) {
            return this.text.replaceAll(this.pattern,data);
        };

        object.reset = function() {
            return this;
        };

        return object;
    };

    /////////////////////////////////////////////////////////////////
    //Pattern
    /////////////////////////////////////////////////////////////////
    gs.pattern = function(pattern) {
        var object = gs.inherit(gs.baseClass,'Pattern');

        createClassNames(object,['java.util.regex.Pattern']);

        object.value = pattern;
        return object;
    };

    /////////////////////////////////////////////////////////////////
    // Regular Expresions
    /////////////////////////////////////////////////////////////////
    gs.matcher = function(item, regExpression) {

        var object = gs.inherit(gs.baseClass, 'Matcher');
        createClassNames(object, ['java.util.regex.Matcher']);

        object.data = item;
        object.regExp = regExpression;
        object.matches = function() {
            return gs.exactMatch(this.data, this.regExp);
        };

        return object;
    };

    RegExp.prototype.matcher = function(item) {
        return gs.matcher(item, this);
    };

    /////////////////////////////////////////////////////////////////
    //Number functions
    /////////////////////////////////////////////////////////////////
    Number.prototype.times = function(closure) {
        var i;
        for (i=0; i<this; i++) {
            closure(i);
        }
    };

    Number.prototype.upto = function(number,closure) {
        var i;
        for (i=this.value; i<=number; i++) {
            closure(i);
        }
    };

    Number.prototype.step = function(number,jump,closure) {
        var i;
        for (i=this.value; i<number;) {
            closure(i);
            i=i+jump;
        }
    };

    Number.prototype.multiply = function(number) {
        return this * number;
    };

    Number.prototype.power = function(number) {
        return Math.pow(this,number);
    };

    /////////////////////////////////////////////////////////////////
    //String functions
    /////////////////////////////////////////////////////////////////
    String.prototype.contains = function(value) {
        return this.indexOf(value)>=0;
    };

    String.prototype.startsWith = function(value) {
        return this.indexOf(value) === 0;
    };

    String.prototype.endsWith = function(value) {
        return this.indexOf(value)==(this.length - value.length);
    };

    String.prototype.count = function(value) {
        var reg = new RegExp(value,'g');
        var result = this.match(reg);
        if (result !== null && result !== undefined) {
            return result.length;
        } else {
            return 0;
        }
    };

    String.prototype.size = function() {
        return this.length;
    };

    String.prototype.replaceAll = function(oldValue,newValue) {
        var reg;
        if (oldValue instanceof RegExp) {
            reg = new RegExp(oldValue.source,'g');
        } else {
            reg = new RegExp(oldValue,'g');
        }
        return this.replace(reg,newValue);
    };

    String.prototype.replaceFirst = function(oldValue,newValue) {
        return this.replace(oldValue,newValue);
    };


    String.prototype.reverse = function() {
        return this.split("").reverse().join("");
    };

    String.prototype.tokenize = function() {
        var str = " ";
        if (arguments.length == 1 && arguments[0] !== null && arguments[0] !== undefined) {
            str = arguments[0];
        }
        var list = this.split(str);
        return gs.list(list);
    };

    String.prototype.multiply = function(value) {
        if (typeof(value)=='number') {
            var result = '';
            var i;
            for (i=0;i<(value | 0);i++) {
                result = result + this;
            }
            return result;
        }
    };

    String.prototype.capitalize = function() {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    function getItemsMultiline(text) {
        var items = text.split('\n');
        if (items.length > 1 && items[items.length-1] === '') {
            items.splice(items.length - 1, 1);
        }
        return items;
    }

    String.prototype.eachLine = function(closure) {
        var items = getItemsMultiline(this);
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
    };

    String.prototype.readLines = function() {
        var items = getItemsMultiline(this);
        return gs.list(items);
    };

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
    };

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
    };

    String.prototype.isNumber = function() {
        if (this.trim() === '') {
            return false;
        } else {
            var res = Number(this);
            if (isNaN(res)) {
                return false;
            } else {
                return true;
            }
        }
    };

    String.prototype.plus = function(other) {
        return this + other.toString();
    };

    /////////////////////////////////////////////////////////////////
    // Misc Functions
    /////////////////////////////////////////////////////////////////
    gs.classForName = function(name) {
        var result = null;
        try {
            var pos = name.indexOf(".");
            while (pos>=0) {
                name = name.substring(pos+1);
                pos = name.indexOf(".");
            }
            result = eval(name);
        } catch (err) {
            result = null;
        }
        return result;
    };

    function StaticMethods(item) {
        this.gSparent = item;
    }

    gs.metaClass = function(item) {
        var type = typeof item;

        if (type == "string") {
            item = new String(item);
        } else if (type == "number") {
            item = new Number(item);
            //If type is a function, it's metaClass from a Class
        } else if (type === "function") {
            if (!globalMetaClass[item.name]) {
                globalMetaClass[item.name] = {
                    gSstatic: new StaticMethods(item),
                    getStatic : function() {
                        return this.gSstatic;
                    }
                };
            }
            item = globalMetaClass[item.name];
        }
        return item;
    };

    gs.passMapToObject = function(source, destination) {
        var prop;
        for (prop in source) {
            if (typeof source[prop] === "function") continue;
            if (prop != 'clazz') {
                gs.sp(destination, prop, source[prop]);
            }
        }
    };

    gs.equals = function(value1, value2) {
        if (!hasFunc(value1, 'equals')) {
            if (hasFunc(value2, 'equals')) {
                return value2.equals(value1);
            } else {
                return value1==value2;
            }
        } else {
            return value1.equals(value2);
        }
    };

    function interceptClosureCall(func, param) {
        if ((param instanceof Array) && func.length>1) {
            func.apply(func,param);
        } else {
            func(param);
        }
    }

    gs.random = function() {
        var object = gs.inherit(gs.baseClass,'Random');
        object.nextInt = function(number) {
            var ran = Math.ceil(Math.random()*number);
            return ran - 1;
        };
        object.nextBoolean = function() {
            var ran = Math.random();
            return ran < 0.5;
        };
        return object;
    };

    gs.bool = function(item) {
        if (item !== null && item !== undefined && item.isEmpty !== undefined) {
            return !item.isEmpty();
        } else {
            if (typeof(item) == 'number' && item === 0) {
                return false;
            } else if (typeof(item) == 'string' && item === '') {
                return false;
            } else if (typeof(item) == 'string' && item !== '') {
                return true;
            }
            return item;
        }
    };

    gs.less = function(itemLeft, itemRight) {
        return itemLeft < itemRight;
    };

    gs.greater = function(itemLeft, itemRight) {
        return itemLeft > itemRight;
    };

    // Operator <=>
    gs.spaceShip = function(itemLeft, itemRight) {
        if (gs.equals(itemLeft, itemRight)) {
            return 0;
        }
        if (gs.less(itemLeft, itemRight)) {
            return -1;
        }
        if (gs.greater(itemLeft, itemRight)) {
            return 1;
        }
    };

    //InstanceOf function
    gs.instanceOf = function(item, name) {
        var classItem;
        var gotIt = false;

        if (name=="String")  {
            return typeof(item)=='string';
        } else if (name=="Number") {
            return typeof(item)=='number';
        } else if (item.clazz) {
            classItem = item.clazz;
            while (classItem !== null && classItem !== undefined && !gotIt) {
                if (classItem.name == name || classItem.simpleName == name) {
                    gotIt = true;
                } else {
                    classItem = classItem.superclass;
                }
            }
        } else if (typeof item === "function" && name == 'Closure') {
            gotIt = true;
        }
        return gotIt;
    };

    //Elvis operator
    gs.elvis = function(booleanExpression, trueExpression, falseExpression) {
        if (gs.bool(booleanExpression)) {
            return trueExpression;
        } else {
            return falseExpression;
        }
    };

    // * operator
    gs.multiply = function(a, b) {
        if (!hasFunc(a, 'multiply')) {
            if (!hasFunc(b, 'multiply')) {
                return a * b;
            } else {
                return b.multiply(a);
            }

        } else {
            return a.multiply(b);
        }
    };

    // + operator
    gs.plus = function(a, b) {
        if (!hasFunc(a, 'plus')) {
            if (!hasFunc(b, 'plus')) {
                if ((typeof a == 'number') && (typeof b == 'number') && ( a+b < 1)) {
                    return ((a*1000)+(b*1000))/1000;
                } else {
                    return a + b;
                }
            } else {
                return b.plus(a);
            }

        } else {
            return a.plus(b);
        }
    };

    // - operator
    gs.minus = function(a, b) {
        if (!hasFunc(a, 'minus')) {
            return a - b;
        } else {
            return a.minus(b);
        }
    };

    // in operator
    gs.gSin = function(item, group) {
        if (group !== null && group !== undefined && (typeof group.contains === "function")) {
            return group.contains(item);
        } else {
            return false;
        }
    };

    //For some special cases where access a property with this."${name}"
    //This can be a closure
    gs.thisOrObject = function(thisItem,objectItem) {
        //this can only be used for our objects, our object must have withz function
        if (thisItem.withz === undefined && objectItem !== null && objectItem !== undefined) {
            return objectItem;
        } else {
            return thisItem;
        }
    };

    // spread operator (*)
    gs.spread = function(item) {
        if (item !== null && item !== undefined) {
            if (item instanceof Array) {
                this.values = item;
            }
        }
    };

    /////////////////////////////////////////////////////////////////
    // Beans functions - From groovy beans
    /////////////////////////////////////////////////////////////////
    //If an object has a function by name
    function hasFunc(item,name) {
        if (item === null || item === undefined ||
            item[name] === undefined || item[name] === null || (typeof item[name] !== "function")) {
            return false;
        } else {
            return true;
        }
    }

    //Set a property of a class
    gs.sp = function(item,nameProperty,value) {

        if (nameProperty == 'setProperty') {
            item[nameProperty] = value;
        } else if (nameProperty == 'getProperty') {
            item[nameProperty] = value;
        } else if (item !== null && item instanceof StaticMethods) {
            item[nameProperty] = value;
            item.gSparent[nameProperty] = value;
        } else {

            if (!hasFunc(item, 'setProperty')) {

                var nameFunction = 'set' + nameProperty.charAt(0).toUpperCase() + nameProperty.slice(1);

                if (item[nameFunction] === undefined || item[nameFunction] === null || (typeof item[nameFunction] != "function")) {
                    item[nameProperty] = value;
                } else {
                    item[nameFunction](value);
                }
            } else {
                item.setProperty(nameProperty,value);
            }
        }
        //return value;
    };

    //Calling a setMethod
    function setMethod(item,methodName,value) {

        if (!hasFunc(item,methodName)) {

            var nameProperty = methodName.charAt(3).toLowerCase() + methodName.slice(4);
            item[nameProperty] = value;
        } else {
            item[methodName](value);
        }

    }

    //Calling a getMethod
    function getMethod(item,methodName) {

        if (!hasFunc(item,methodName)) {

            var nameProperty = methodName.charAt(3).toLowerCase() + methodName.slice(4);
            var res = function () { return item[nameProperty];};
            return res;

        } else {
            return item[methodName];
        }

    }

    //Get a property of a class
    gs.gp = function(item, nameProperty) {

        //It's a get with safe operator as item?.data
        if (arguments.length == 3) {
            if (item === null || item === undefined) {
                return null;
            }
        }

        if (!hasFunc(item,'getProperty')) {
            var nameFunction = 'get' + nameProperty.charAt(0).toUpperCase() + nameProperty.slice(1);
            if (!hasFunc(item,nameFunction)) {
                if (typeof item[nameProperty] === "function" && nameProperty == 'size') {
                    return item[nameProperty]();
                } else {
                    if (item[nameProperty] !== undefined) {
                        return item[nameProperty];
                    } else {
                        if (item.gSdefaultValue !== undefined && (typeof item.gSdefaultValue === "function")) {
                            item[nameProperty] = item.gSdefaultValue();
                        }
                        return item[nameProperty];
                    }
                }
            } else {
                return item[nameFunction]();
            }
        } else {
            return item.getProperty(nameProperty);
        }
    };

    //Control property changes with ++,--
    gs.plusPlus = function(item, nameProperty, plus, before) {
        var value = gs.gp(item,nameProperty);
        var newValue = value;
        if (plus) {
            gs.sp(item, nameProperty, value + 1);
            newValue++;
        } else {
            gs.sp(item, nameProperty, value - 1);
            newValue--;
        }
        if (before) {
            return newValue;
        } else {
            return value;
        }
    };

    //Control all method calls
    gs.mc = function(item, methodName, values) {

        if (gs.consoleInfo && console) {
            console.log('[INFO] gs.mc (' + item + ').' + methodName + ' params:' + values);
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

        if (!hasFunc(item, methodName)) {

            if (methodName.startsWith('get') || methodName.startsWith('set')) {
                var varName = methodName.charAt(3).toLowerCase() + methodName.slice(4);
                var properties = item.getProperties();
                if (properties.contains(varName)) {
                    if (methodName.startsWith('get')) {
                        return gs.gp(item, varName);
                    } else {
                        return gs.sp(item, varName, values[0]);
                    }

                }
            }

            //Check newInstance
            if (methodName=='newInstance') {
                return item();
            } else {
                var whereExecutes;
                //Lets check if in any category we have the static method
                if (categories.length > 0) {
                    whereExecutes = categorySearching(methodName);
                    if (whereExecutes !== null) {
                        return whereExecutes[methodName].apply(item, joinParameters(item, values));
                    }
                }

                //In @Category
                var ob;
                for (ob in annotatedCategories) {
                    if (annotatedCategories[ob] == item.clazz.simpleName) {
                        var categoryItem = gs.myCategories[ob]();
                        if (categoryItem[methodName] && typeof categoryItem[methodName] === "function") {
                            return categoryItem[methodName].apply(item, joinParameters(item, values));
                        }
                    }
                }

                //Lets check in mixins classes
                if (mixins.length > 0) {
                    whereExecutes = mixinSearching(item,methodName);
                    if (whereExecutes !== null) {
                        return whereExecutes[methodName].apply(item, joinParameters(item, values));
                    }
                }
                //Lets check in mixins objects
                if (mixinsObjects.length>0) {
                    whereExecutes = mixinObjectsSearching(item, methodName);
                    if (whereExecutes !== null) {
                        return whereExecutes[methodName].apply(item,joinParameters(item, values));
                    }
                }

                //Lets check in delegate
                if (actualDelegate !== null && actualDelegate[methodName] !== undefined) {
                    return actualDelegate[methodName].apply(item, values);
                }
                if (actualDelegate !== null && item.methodMissing === undefined && actualDelegate.methodMissing !== undefined) {
                    return gs.mc(actualDelegate, methodName, values);
                }

                if (item.methodMissing) {
                    return item.methodMissing(methodName, values);

                } else {
                    //Maybe there is a function in the script with the name of the method
                    //In Node.js 'this.xxFunction()' in the main context fails
                    if (typeof eval(methodName) === 'function') {
                        return eval(methodName).apply(this, values);
                    }

                    //Not exist the method, throw exception
                    throw 'gs.mc Method ' + methodName + ' not exist in ' + item;
                }
            }

        } else {
            var f = item[methodName];
            return f.apply(item, values);
        }
    };

    function joinParameters(item,items) {
        var listParameters = [item],i;
        for (i=0; i < items.size(); i++) {
            listParameters[listParameters.length] = items[i];
        }
        return listParameters;
    }

    ////////////////////////////////////////////////////////////
    // Categories
    ////////////////////////////////////////////////////////////
    gs.categoryUse = function(item, closure) {
        var ob, categoryCreated;
        if (existAnnotatedCategory(item)) {
            categoryCreated = gs.myCategories[item]();
            for (ob in categoryCreated) {
                if (!isObjectProperty(ob) && !isConstructor(ob, categoryCreated[ob]) &&
                    typeof categoryCreated[ob] === "function") {
                    addFunctionToClassIfPrototyped(ob, categoryCreated[ob], annotatedCategories[item]);
                }
            }
        } else {
            categories[categories.length] = item;
        }
        closure();
        if (existAnnotatedCategory(item)) {
            categoryCreated = gs.myCategories[item]();
            for (ob in categoryCreated) {
                if (!isObjectProperty(ob) && !isConstructor(ob, categoryCreated[ob]) &&
                    typeof categoryCreated[ob] === "function") {
                    removeFunctionToClass(ob, categoryCreated[ob], annotatedCategories[item]);
                }
            }
        } else {
            categories.splice(categories.length - 1, 1);
        }
    };

    function getProtoypeOfClass(className) {
        if (className == 'String') {
            return String.prototype;
        }
        if (className == 'Number') {
            return Number.prototype;
        }
        if (className == 'ArrayList') {
            return Array.prototype;
        }
        return null;
    }

    function addFunctionToClassIfPrototyped(name, func, className) {
        var proto = getProtoypeOfClass(className);
        if  (proto !== null) {
            if (proto[name] === undefined) {
                proto[name] = func;
            }
        }
    }

    function removeFunctionToClass(name, func, className) {
        var proto = getProtoypeOfClass(className);
        if  (proto !== null) {
            if (proto[name] == func) {
                proto[name] = null;
            }
        }
    }

    function categorySearching(methodName) {
        var result = null;
        var i;
        for (i = categories.length - 1; i >= 0 && result === null; i--) {
            var name = categories[i];
            if (eval(name)[methodName]) {
                result = eval(name);
            }
        }
        return result;
    }

    function existAnnotatedCategory(name) {
        return (annotatedCategories[name] !== null && annotatedCategories[name] !== undefined);
    }

    var annotatedCategories = {};
    gs.addAnnotatedCategory = function(nameCategory, nameClass) {
        annotatedCategories[nameCategory] = nameClass;
    };

    ////////////////////////////////////////////////////////////
    // Mixins
    ////////////////////////////////////////////////////////////
    gs.mixinClass = function(item, classes) {

        //First check in that class has mixins
        var gotIt = false;
        if (mixins.length > 0) {
            var i;
            for (i=0; i < mixins.length && !gotIt; i++) {
                if (mixins[i].name==item) {
                    var j;
                    for (j=0; j < classes.length; j++) {
                        mixins[i].items[mixins[i].items.length] = classes[j];
                    }
                    gotIt = true;
                }
            }
        }
        if (!gotIt) {
            mixins[mixins.length] = { name: item, items: classes};
        }
    };

    gs.mixinObject = function(item, classes) {

        var gotIt = false;
        if (mixinsObjects.length > 0) {
            var i;
            for (i=0; i < mixinsObjects.length && !gotIt; i++) {
                if (mixinsObjects[i].item == item) {
                    var j;
                    for (j=0; j < classes.length; j++) {
                        mixinsObjects[i].items[mixinsObjects[i].items.length] = classes[j];
                    }
                    gotIt = true;
                }
            }
        }
        if (!gotIt) {
            mixinsObjects[mixinsObjects.length] = { item:item, items:classes};
        }
        //TODO make any kinda cleanup if mixinsObjects growing
    };

    function mixinSearching(item,methodName) {
        var result = null;
        var className = null;
        if (typeof(item) == 'string') {
            className = 'String';
        }
        if (typeof(item) == 'object' && item.clazz !== undefined && item.clazz.simpleName !== undefined) {
            className = item.clazz.simpleName;
        }
        //console.log(' className:'+className);
        if (className !== null) {
            var i, ourMixin=null;
            for (i = mixins.length - 1; i >= 0 && ourMixin === null; i--) {
                var data = mixins[i];
                //console.log(' mixin: '+data.name);
                if (data.name == className) {
                    ourMixin = data.items;
                }
            }
            if (ourMixin !== null) {
                //console.log(' our: '+ourMixin+' - '+methodName);
                for (i = 0; i < ourMixin.length && result === null; i++) {
                    if (eval(ourMixin[i])[methodName]) {
                        result = eval(ourMixin[i]);
                    } else {
                        var classItem = eval(ourMixin[i]+'()');
                        if (classItem) {
                            var notStatic = classItem[methodName];
                            if (notStatic !== null && typeof notStatic === "function") {
                                result = classItem;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    function mixinObjectsSearching(item,methodName) {

        var result = null;
        var i, ourMixin=null;
        for (i = mixinsObjects.length - 1; i >= 0 && ourMixin === null; i--) {
            var data = mixinsObjects[i];
            if (data.item == item) {
                ourMixin = data.items;
            }
        }
        if (ourMixin !== null) {
            for (i=0 ; i < ourMixin.length && result === null; i++) {
                if (eval(ourMixin[i])[methodName]) {
                    result = eval(ourMixin[i]);
                }
            }
        }

        return result;
    }

    ////////////////////////////////////////////////////////////
    // StringBuffer - very basic support, for add with <<
    ////////////////////////////////////////////////////////////
    gs.stringBuffer = function() {

        var object = gs.inherit(gs.baseClass,'StringBuffer');
        object.value = '';

        if (arguments.length==1 && typeof arguments[0] === 'string') {
            object.value = arguments[0];
        }

        object.toString = function() {
            return this.value;
        };

        object.leftShift = function(value) {
            return this.append(value);
        };

        object.plus = function(value) {
            return this.append(value);
        };

        object.size = function() {
            return this.value.length;
        };

        object.append = function(value) {
            this.value = this.value + value;
            return this;
        };
        return object;
    };

    ////////////////////////////////////////////////////////////
    // Delegate
    ////////////////////////////////////////////////////////////
    gs.applyDelegate = function(func, delegate, params) {
        var oldDelegate = actualDelegate;
        //console.log('setting delegate');
        actualDelegate = delegate;
        var result = func.apply(delegate, params);
        //console.log('desetting delegate');
        actualDelegate = oldDelegate;
        return result;
    };

    ////////////////////////////////////////////////////////////
    // Functional
    ////////////////////////////////////////////////////////////
    Function.prototype.curry = function () {
        var slice = Array.prototype.slice,
            args = slice.apply(arguments),
            that = this;
        return function () {
            return that.apply(null, args.concat(slice.apply(arguments)));
        };
    };

    Function.prototype.rcurry = function () {
        var slice = Array.prototype.slice,
            args = slice.apply(arguments),
            that = this;
        return function () {
            return that.apply(null, (slice.apply(arguments)).concat(args));
        };
    };

    Function.prototype.ncurry = function () {
        var slice = Array.prototype.slice,
            args = slice.apply(arguments, [1]),
            begin = arguments[0],
            that = this;
        return function () {
            return that.apply(null, slice.apply(arguments, [0, begin]).concat(args).concat(slice.apply(arguments, [begin])));
        };
    };

    Function.prototype.leftShift = function () {
        var func = arguments[0],
            that = this;
        return function () {
            return that(func.apply(null, arguments));
        };
    };

    Function.prototype.rightShift = function () {
        var func = arguments[0],
            that = this;
        return function () {
            return func(that.apply(null, arguments));
        };
    };

    //MISC Find scope of a var
    gs.fs = function(name, thisScope) {
        if (thisScope !== undefined && thisScope[name] !== undefined) {
            return thisScope[name];
        } else {
            var value = gs.gp(thisScope, name);
            if (value === undefined) {
                var func = new Function("return " + name);
                return func();
            } else {
                return value;
            }
        }
    };

    gs.toJavascript = function(message) {
        var result;
        if (message !== null && message !== undefined && typeof(message) !== "function") {
            if (message instanceof Array) {
                result = [];
                var i;
                for (i = 0; i < message.length; i++) {
                    result[result.length] = gs.toJavascript(message[i]);
                }
            } else {
                if (message instanceof Object) {
                    result = {};
                    var ob;
                    for (ob in message) {
                        if (!isMapProperty(ob)) {
                            result[ob] = gs.toJavascript(message[ob]);
                        }
                    }
                } else {
                    result = message;
                }
            }
        }
        return result;
    };

    gs.toGroovy = function(message) {
        var result;
        if (message !== null && message !== undefined && typeof(message) !== "function") {
            if (message instanceof Array) {
                result = gs.list([]);
                var i;
                for (i = 0; i < message.length; i++) {
                    result.add(gs.toGroovy(message[i]));
                }
            } else {
                var ob;
                if (message instanceof Object) {
                    result = gs.map();
                    for (ob in message) {
                        result.add(ob, gs.toGroovy(message[ob]));
                    }
                } else {
                    result = message;
                }
            }
        }
        return result;
    };

    gs.toNumber = function(number) {
        if (number) {
            if (typeof(number) == 'string') {
                return parseFloat(number);
            } else {
                return number;
            }
        }
    };

}).call(this);
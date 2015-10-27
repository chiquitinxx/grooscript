/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    var delegates = [];

    //Static this
    var aStT = null;

    //@Delegate
    var mapAddDelegate = {};

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
            gs.println(message + value);
        }
    };

    //Function that used for print and println in groovy
    gs.println = function(value) {
        if (gs.consoleOutput) {
            console.log(value);
        } else {
            if (gs.consoleData !== "") {
                gs.consoleData = gs.consoleData + "\n";
            }
            gs.consoleData = gs.consoleData + value;
        }
    };

    gs.printNashorn = function(value) {
        print(value);
    };

    //TODO We don't know if a function is constructor, atm if function name starts with uppercase, it is
    function isConstructor(name, func) {
        return name[0] == name[0].toUpperCase();
    }

    function isFunction(f) {
        return typeof(f) === "function";
    }

    function getterSetterRemove(name) {
        return name.charAt(3).toLowerCase() + name.slice(4);
    }

    /////////////////////////////////////////////////////////////////
    // Class functions
    /////////////////////////////////////////////////////////////////
    function BaseClass() { };
    gs.BaseClass = BaseClass;
    //gs.baseClass = {
        //The with function, with is a reserved word in JavaScript
    BaseClass.prototype.clazz = {};
    BaseClass.prototype.withz = function(closure) { return closure.apply(this, closure.arguments); },
    BaseClass.prototype.getProperties = function() {
            var result = gs.map(), ob;
            for (ob in this) {
                if (isFunction(this[ob]) && ob.startsWith('get') &&
                    this[ob].length == 0 && ob !== 'getProperties' && ob !== 'getMethods' &&
                    ob !== 'getMetaClass') {
                    result.add(getterSetterRemove(ob), this[ob]());
                } else if (!isFunction(this[ob]) && ob != 'clazz' && ob.indexOf('__') < 0) {
                    result.add(ob, this[ob]);
                }
            }
            return result;
        };
    BaseClass.prototype.getMethods = function() {
            var result = gs.list([]), ob;
            for (ob in this) {
                if (isFunction(this[ob])) {
                    if (!isObjectProperty(ob) && !(isConstructor(ob, this[ob]))) {
                        var item = {
                            name: ob
                        };
                        result.add(item);
                    }
                }
            }
            return result;
        };
    BaseClass.prototype.invokeMethod = function(name, values) {
            var i, newArgs = [];
            if (values) {
                for (i=0; i < values.length; i++) {
                    newArgs[i] = values[i];
                }
            }
            var f = this[name];
            return f.apply(this, newArgs);
        };
    BaseClass.prototype.constructor = function() {
            return this;
        };
    BaseClass.prototype.asType = function(type) {
            if (hasFunc(type, 'gSaT')) {
                type.gSaT(this);
            }
            return this;
        };
    BaseClass.prototype.withTraits = function() {
            var i;
            for (i = 0; i < arguments.length; i++) {
                arguments[i].gSaT(this);
            }
            return this;
        };
    BaseClass.prototype.getClass = function() {
            return this.clazz;
        };
    BaseClass.prototype.getMetaClass = function() {
            return gs.metaClass(this);
        };

    function applyBaseClassFunctions(item) {
        item.asType = BaseClass.prototype.asType;
    }

    function isObjectProperty(name) {
        return ['clazz','gSdefaultValue','leftShift',
            'minus','plus','equals','toString',
            'clone','withz','getProperties','getStatic', 'getClass', 'getMetaClass',
            'getMethods','invokeMethod','constructor', 'asType', 'withTraits'].indexOf(name) >= 0;
    }

    gs.expando = function() {
        var object = gs.init('Expando');

        object.constructorWithMap = function(map) { gs.passMapToObject(map, this); return this;};
        if (arguments.length == 1) {object.constructorWithMap(arguments[0]); }

        return object;
    };

    gs.expandoMetaClass = function() {
        var object = gs.init('ExpandoMetaClass');
        object.initialize = function() {
            return this;
        };
        return object;
    };

    function expandWithMetaClass(item, objectName) {
        if (globalMetaClass && globalMetaClass[objectName]) {
            var obj, map = globalMetaClass[objectName];
            for (obj in map) {

                //Static methods
                var staticMap = map.getStatic();
                if (staticMap) {
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

    gs.init = function(name) {
        return expandWithMetaClass(new BaseClass(), name);
    };

    function createClassNames(item, items) {
        var number = items.length, i, container;
        for (i = 0; i < number ; i++) {
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

        createClassNames(object, ['java.util.HashSet']);

        object.isSet = true;

        object.withz = function(closure) {
            return interceptClosureCall(closure, this);
        };

        object.add = function(item) {
            if (!(this.contains(item))) {
                this.push(item);
                return this;
            } else {
                return false;
            }
        };

        object.addAll = function(elements) {
            if (elements instanceof Array) {
                var i, fails = false;

                //Check if items not in set
                for (i = 0; !fails && i < elements.length; i++) {
                    if (this.contains(elements[i])) {
                        fails = true;
                    }
                }
                if (fails) {
                    return false;
                } else {
                    //All ok, we add items to the set
                    for (i = 0; i < elements.length; i++) {
                        this.add(elements[i]);
                    }
                }
            }
            return this;
        };

        object.equals = function(other) {
            if (!(other instanceof Array) || other.length != this.length || !(other.isSet)) {
                return false;
            } else {
                var i, result = true;
                for (i = 0; i < this.length && result; i++) {
                    if (!(other.contains(this[i]))) {
                        result = false;
                    }
                }
                return result;
            }
        };

        object.toList = function() {
            var i, list = [];
            for (i = 0; i < this.length; i++) {
                list[i] = this[i];
            }
            return gs.list(list);
        };

        object.plus = function(other) {
            var result = gs.set();
            result.addAll(this);
            if (other instanceof Array) {
                var i;
                for (i = 0; i < other.length; i++) {
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
                for (i = 0;i < other.length; i++) {
                    if (result.contains(other[i])) {
                        result.remove(other[i]);
                    }
                }
            }
            return result;
        };

        object.remove = function(value) {
            var index = this.indexOf(value);
            if (index >= 0) {
                this.splice(index, 1);
                return true;
            } else {
                return false;
            }
        };

        return object;
    };

    /////////////////////////////////////////////////////////////////
    // map - [:] from groovy
    /////////////////////////////////////////////////////////////////
    function isMapProperty(name) {
        return isObjectProperty(name) || ['any','collect',
            'collectEntries','collectMany','countBy','dropWhile',
            'each','eachWithIndex','every','find','findAll',
            'findResult','findResults','get','getAt','groupBy',
            'inject','intersect','max','min',
            'putAll','putAt','reverseEach', 'clear',
            'sort','spread','subMap','add','take','takeWhile',
            'withDefault','count','drop','keySet',
            'put','size','isEmpty','remove','containsKey',
            'containsValue','values'].indexOf(name) >= 0;
    }

    gs.map = function() {
        var gSobject = new GsGroovyMap();
        expandWithMetaClass(gSobject, 'LinkedHashMap');
        applyBaseClassFunctions(gSobject);
        if (arguments.length == 1 && arguments[0] instanceof Object) {
            gs.passMapToObject(arguments[0], gSobject);
        }

        return gSobject;
    };

    function GsGroovyMap() {}

    GsGroovyMap.prototype.clazz = { name: 'java.util.LinkedHashMap', simpleName: 'LinkedHashMap',
        superclass: { name: 'java.util.HashMap', simpleName: 'HashMap'}};
    GsGroovyMap.prototype.gSdefaultValue = null;
    GsGroovyMap.prototype.withz = BaseClass.prototype.withz;
    GsGroovyMap.prototype.add = function(key,value) {
        if (key == "spreadMap") {
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
    GsGroovyMap.prototype.put = function(key,value) {
        return this.add(key,value);
    };
    GsGroovyMap.prototype.leftShift = function(key,value) {
        if (arguments.length == 1) {
            return this.plus(arguments[0]);
        } else {
            return this.add(key,value);
        }
    };
    GsGroovyMap.prototype.putAt = function(key,value) {
        this.put(key,value);
    };
    GsGroovyMap.prototype.size = function() {
        var number = 0,ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                number++;
            }
        }
        return number;
    };
    GsGroovyMap.prototype.isEmpty = function() {
        return (this.size() === 0);
    };
    GsGroovyMap.prototype.remove = function(key) {
        if (this[key]) {
            delete this[key];
        }
    };
    GsGroovyMap.prototype.each = function(closure) {
        var ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                var f = arguments[0];
                //Nice, number of arguments in length property
                if (f.length == 1) {
                    closure({key: ob, value: this[ob]});
                }
                if (f.length == 2) {
                    closure(ob,this[ob]);
                }
            }
        }
    };

    GsGroovyMap.prototype.count = function(closure) {
        var number = 0, ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                if (closure.length == 1) {
                    if (closure({key: ob, value: this[ob]})) {
                        number++;
                    }
                }
                if (closure.length == 2) {
                    if (closure(ob, this[ob])) {
                        number++;
                    }
                }
            }
        }
        return number;
    };

    GsGroovyMap.prototype.any = function(closure) {
        var ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                var f = arguments[0];
                if (f.length == 1) {
                    if (closure({key:ob, value: this[ob]})) {
                        return true;
                    }
                }
                if (f.length == 2) {
                    if (closure(ob, this[ob])) {
                        return true;
                    }
                }
            }
        }
        return false;
    };

    GsGroovyMap.prototype.every = function(closure) {
        var ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                var f = arguments[0];
                if (f.length == 1) {
                    if (!closure({key: ob, value: this[ob]})) {
                        return false;
                    }
                }
                if (f.length == 2) {
                    if (!closure(ob, this[ob])) {
                        return false;
                    }
                }
            }
        }
        return true;
    };

    GsGroovyMap.prototype.find = function(closure) {
        var ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                var f = arguments[0];
                if (f.length == 1) {
                    var entry = {key: ob, value: this[ob]};
                    if (closure(entry)) {
                        return entry;
                    }
                }
                if (f.length == 2) {
                    if (closure(ob, this[ob])) {
                        return {key: ob, value: this[ob]};
                    }
                }
            }
        }
        return null;
    };

    GsGroovyMap.prototype.dropWhile = function(closure) {
        var result = gs.map(), ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                var entry = {key: ob, value: this[ob]};

                var f = arguments[0];
                if (f.length == 1) {
                    if (!closure(entry)) {
                        result.add(entry.key, entry.value);
                    }
                }
                if (f.length == 2) {
                    if (!closure(entry.key, entry.value)) {
                        result.add(entry.key, entry.value);
                    }
                }
            }
        }
        return result;
    };

    GsGroovyMap.prototype.drop = function(number) {
        var result = gs.map(), ob, count = 0;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                count ++;
                if (count > number) {
                    result.add(ob, this[ob]);
                }
            }
        }
        return result;
    };

    GsGroovyMap.prototype.findAll = function(closure) {
        var result = gs.map(), ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                var f = arguments[0];
                if (f.length == 1) {
                    var entry = {key: ob, value: this[ob]};
                    if (closure(entry)) {
                        result.add(entry.key, entry.value);
                    }
                }
                if (f.length == 2) {
                    if (closure(ob, this[ob])) {
                        result.add(ob, this[ob]);
                    }
                }
            }
        }
        return result;
    };

    GsGroovyMap.prototype.collect = function(closure) {
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

    GsGroovyMap.prototype.containsKey = function(key) {
        if (this[key] === undefined || this[key] === null) {
            return false;
        } else {
            return true;
        }
    };

    GsGroovyMap.prototype.containsValue = function(value) {
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

    GsGroovyMap.prototype.get = function(key, defaultValue) {
        if (!this.containsKey(key)) {
            this[key] = defaultValue;
        }
        return this[key];
    };

    GsGroovyMap.prototype.toString = function() {
        var items = '';
        this.each (function(key,value) {
            items = items + key+': '+value+' ,';
        });
        return '[' + items + ']';
    };

    GsGroovyMap.prototype.equals = function(otherMap) {
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

    GsGroovyMap.prototype.keySet = function() {
        var result = gs.list([]), ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                result.add(ob);
            }
        }
        return result;
    };

    GsGroovyMap.prototype.values = function() {
        var result = gs.list([]), ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                result.add(this[ob]);
            }
        }
        return result;
    };

    GsGroovyMap.prototype.withDefault = function(closure) {
        this.gSdefaultValue = closure;
        return this;
    };

    GsGroovyMap.prototype.inject = function(initial,closure) {
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

    GsGroovyMap.prototype.putAll = function (items) {
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

    GsGroovyMap.prototype.plus = function(other) {
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

    GsGroovyMap.prototype.clone = function() {
        var result = gs.map(), ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                result.add(ob, this[ob]);
            }
        }
        return result;
    };

    GsGroovyMap.prototype.minus = function(other) {
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

    GsGroovyMap.prototype.clear = function() {
        var ob;
        for (ob in this) {
            if (!isMapProperty(ob)) {
                delete this[ob];
            }
        }
    };

    /////////////////////////////////////////////////////////////////
    // Array prototype changes
    /////////////////////////////////////////////////////////////////
    Array.prototype.get = function(pos) {

        //Maybe comes a second parameter with default value
        if (arguments.length == 2) {
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
        return interceptClosureCall(closure, this);
    };

    Array.prototype.size = function() {
        return this.length;
    };

    Array.prototype.isEmpty = function() {
        return this.length === 0;
    };

    Array.prototype.add = function(pos, element) {
        if (element === undefined) {
            this.push(pos);
        } else {
            this[pos] = element;
        }
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
                this.push(elements);
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

    Array.prototype.containsAll = function(list) {
        var i, numberEq = 0;
        for (i = 0; i < list.length; i++) {
            if (this.contains(list[i])) {
                numberEq++;
            }
        }
        if (numberEq == list.length) {
            return true;
        } else {
            return false;
        }
    };

    Array.prototype.each = function(closure) {
        var i;
        for (i = 0; i < this.length; i++) {
            //TODO Beware this change, have to apply to all closure calls
            interceptClosureCall(closure, this[i]);
        }
        return this;
    };

    Array.prototype.reverseEach = function(closure) {
        var i;
        for (i = this.length - 1; i >= 0; i--) {
            interceptClosureCall(closure, this[i]);
        }
        return this;
    };

    Array.prototype.eachWithIndex = function(closure,index) {
        for (index=0; index < this.length; index++) {
            closure(this[index], index);
        }
        return this;
    };

    Array.prototype.any = function(closure) {
        var i;
        for (i = 0;i < this.length; i++) {
            if (closure(this[i])) {
                return true;
            }
        }
        return false;
    };

    Array.prototype.values = function() {
        var i, result = [];
        for (i = 0; i < this.length; i++) {
            result[i]=this[i];
        }
        return result;
    };
    //Remove only 1 item from the list
    Array.prototype.remove = function(indexOrValue) {
        var result = false,index = -1;
        if (typeof indexOrValue == 'number') {
            index = indexOrValue;
            result = this[index];
        } else {
            index = this.indexOf(indexOrValue);
            if (index >= 0) {
                result = true;
            }
        }
        if (index >= 0) {
            this.splice(index, 1);
        }
        return result;
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
        } else if (isFunction(data)) {
            var i;
            for (i = this.length - 1; i >= 0; i--) {
                if (data(this[i])) {
                    this.remove(i);
                }
            }
        }
        return this;
    };

    Array.prototype.collect = function(closure) {
        var i, result = gs.list([]);
        for (i = 0; i < this.length; i++) {
            result[i] = closure(this[i]);
        }
        return result;
    };

    Array.prototype.collectMany = function(closure) {
        var i, result = gs.list([]);
        for (i = 0;i < this.length; i++) {
            result.addAll(closure(this[i]));
        }
        return result;
    };

    Array.prototype.takeWhile = function(closure) {
        var i, result = gs.list([]);
        for (i = 0; i < this.length; i++) {
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
        var i, j=0, insert = false;
        for (i = 0; i < this.length; i++) {
            if (!closure(this[i])) {
                insert=true;
            }
            if (insert) {
                result[j++] = this[i];
            }
        }
        return result;
    };

    Array.prototype.drop = function(number) {
        var i, result = gs.list([]);
        for (i = number; i < this.length; i++) {
            result.push(this[i]);
        }
        return result;
    };

    Array.prototype.findAll = function(closure) {
        var values = this.filter(closure);
        return gs.list(values);
    };

    Array.prototype.find = function(closure) {
        var result, i;
        for (i = 0; !result && i < this.length; i++) {
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
        return this[this.length - 1];
    };

    Array.prototype.sum = function() {

        var i, result = 0;
        //can pass a closure to sum
        if (arguments.length == 1) {
            for (i = 0; i < this.length; i++) {
                result = result + arguments[0](this[i]);
            }
        } else {
            if (this.length > 0 && this[0].plus) {
                var item = this[0];
                for (i = 0; i + 1 < this.length; i++) {
                    item = item.plus(this[i + 1]);
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
        var i, result = gs.list([]);
        for (i = 0;i < this.length; i++) {
            if (otherList.contains(this[i])) {
                result.add(this[i]);
            }
        }
        return result;
    };

    Array.prototype.max = function() {
        var i, result = null;
        for (i = 0; i < this.length; i++) {
            if (result === null || this[i] > result) {
                result = this[i];
            }
        }
        return result;
    };

    Array.prototype.min = function() {
        var i, result = null;
        for (i = 0; i < this.length; i++) {
            if (result === null || this[i] < result) {
                result = this[i];
            }
        }
        return result;
    };

    Array.prototype.oldToString = Array.prototype.toString;

    Array.prototype.toString = function() {
        if (this['clazz'] === undefined) {
            return this.oldToString();
        } else if (this.length > 0) {
            return '[' + this.join(', ') + ']';
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
        } else if (isFunction(param)) {
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
            var i, result = true;
            for (i = 0;i < this.length && result; i++) {
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
        var i, result = '';
        for (i = 0; i < this.length; i++) {
            result = result + this[i];
            if ((i + 1) < this.length) {
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
        if (arguments.length == 2 && isFunction(arguments[1])) {
            tempFunction = arguments[1];
        }
        if (arguments.length == 1 && isFunction(arguments[0])) {
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
                copy.push(this[i]);
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
        var i, result = [];
        for (i = 0; i < number; i++) {
            if (i < this.length) {
                result[i] = this[i];
            }
        }
        return gs.list(result);
    };

    Array.prototype.takeWhile = function(closure) {
        var result = [], i, exit=false;
        for (i = 0; !exit && i < this.length; i++) {
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
        gs.flatten(result, this);

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
            while (pos < this.length && items.size() < number) {
                items.add(this[pos++]);
            }
            result.add(items);
            times++;
        }
        return result;
    };

    Array.prototype.groupBy = function(closure) {
        var i, result = gs.map();
        for (i=0;i<this.length;i++) {
            var r = closure(this[i]);
            var l = result[r];
            if (l) {
                l.add(this[i]);
            } else {
                result.add(r, gs.list().add(this[i]));
            }
        }
        return result;
    };

    Array.prototype.putAt = function(position, value) {
        this[position] = value;
    };

    Array.prototype.clear = function() {
        this.splice(0, this.length)
    };

    Array.prototype.count = function(value) {
        var i, result = 0;
        if (isFunction(value)) {
            for (i = 0; i < this.length; i++) {
                if (gs.bool(value(this[i]))) {
                    result++;
                }
            }
        } else {
            for (i = 0; i < this.length; i++) {
                if (gs.equals(value, this[i])) {
                    result++;
                }
            }
        }
        return result;
    };

    Array.prototype.tail = function() {
        var i, result = [];
        for (i = 1; i < this.length; i++) {
            result.push(this[i]);
        }
        return gs.list(result);
    };

    Array.prototype.init = function() {
        var i, result = [];
        for (i = 0; i < this.length - 1; i++) {
            result.push(this[i]);
        }
        return gs.list(result);
    };

    /////////////////////////////////////////////////////////////////
    //list - [] from groovy
    /////////////////////////////////////////////////////////////////
    gs.list = function(value) {
        var data = [];
        if (value && value.length > 0) {
            var i;
            for (i = 0; i < value.length; i++) {
                if (value[i] instanceof gs.spread) {
                    var values = value[i].values;
                    if (values.length > 0) {
                        var j;
                        for (j = 0; j < values.length; j++) {
                            data.push(values[j]);
                        }
                    }
                } else {
                    data.push(value[i]);
                }
            }
        }
        var object = data;
        object.clazz = {name: 'java.util.ArrayList', simpleName: 'ArrayList'};
        applyBaseClassFunctions(object);
        return object;
    };

    gs.flatten = function(result, list) {
        list.each(function (it) {
            if (it instanceof Array) {
                if (it.length>0) {
                    gs.flatten(result, it);
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
        for (result=[], number = start, count = 0 ; number <= finish ; number++, count++) {
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

        var gSobject;
        if (arguments.length == 1) {
            gSobject = new Date(arguments[0]);
        } else {
            gSobject = new Date();
        }

        createClassNames(gSobject, ['java.util.Date']);
        gSobject.withz = BaseClass.prototype.withz;

        gSobject.time = gSobject.getTime();
        gSobject.setTime = function(milis) {
            gSobject.time = milis;
        };

        gSobject.year = gSobject.getFullYear();
        gSobject.month = gSobject.getMonth();
        gSobject.date = gSobject.getDay();
        gSobject.plus = function(other) {
            if (typeof other == 'number') {
                return gs.date(gSobject.time + (other * 1440000));
            } else {
                return gSobject + other;
            }
        };
        gSobject.minus = function(other) {
            if (typeof other == 'number') {
                return gs.date(gSobject.time - (other * 1440000));
            } else {
                return gSobject - other;
            }
        };
        gSobject.format = function(rule) {
            //TODO complete
            var exit = '';
            if (rule) {
                exit = rule;
                exit = exit.replaceAll('yyyy', gSobject.getFullYear());
                exit = exit.replaceAll('MM', fillZerosLeft(gSobject.getMonth() + 1, 2));
                exit = exit.replaceAll('dd', fillZerosLeft(gSobject.getUTCDate(), 2));
                exit = exit.replaceAll('HH', fillZerosLeft(gSobject.getHours(), 2));
                exit = exit.replaceAll('mm', fillZerosLeft(gSobject.getMinutes(), 2));
                exit = exit.replaceAll('ss', fillZerosLeft(gSobject.getSeconds(), 2));
                exit = exit.replaceAll('yy', lastChars(gSobject.getFullYear(), 2));
            }
            return exit;
        };
        gSobject.parse = function(rule, text) {
            //TODO complete
            var pos = rule.indexOf('MM');
            if (pos >= 0) {
                var newMonth = text.substr(pos, 2) - 1;
                while (gSobject.getMonth() != newMonth) {
                    gSobject.setMonth(newMonth);
                }
            }
            pos = rule.indexOf('dd');
            if (pos >= 0) {
                var newDay = text.substr(pos, 2);
                while (gSobject.getUTCDate() != newDay) {
                    gSobject.setUTCDate(newDay);
                }
            }
            pos = rule.indexOf('yyyy');
            if (pos >= 0) {
                gSobject.setFullYear(text.substr(pos, 4));
            } else {
                pos = rule.indexOf('yy');
                if (pos >= 0) {
                    gSobject.setFullYear(text.substr(pos, 2));
                }
            }
            pos = rule.indexOf('HH');
            if (pos >= 0) {
                gSobject.setHours(text.substr(pos, 2));
            }
            pos = rule.indexOf('mm');
            if (pos >= 0) {
                gSobject.setMinutes(text.substr(pos, 2));
            }
            pos = rule.indexOf('ss');
            if (pos >= 0) {
                gSobject.setSeconds(text.substr(pos, 2));
            }

            return gSobject;
        };
        gSobject.clearTime = function() {
            gSobject.setHours(0, 0, 0, 0);
            return gSobject;
        };
        gSobject.equals = function(other) {
            return gSobject.time == other.time;
        };
        gSobject.before = function(other) {
            return gSobject.time < other.time;
        };
        gSobject.after = function(other) {
            return gSobject.time > other.time;
        };
        return gSobject;
    };

    gs.rangeFromList = function(list, begin, end) {
        return list.slice(begin, end + 1);
    };

    function fillZerosLeft(item, size) {
        var value = item + '';
        while (value.length < size) {
            value = '0' + value;
        }
        return value;
    }

    function lastChars(item, number) {
        var value = item + '';
        value = value.substring(value.length - number);
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

            while (data) {
                if (data instanceof Array && data.length < 2) {
                    list[i] = data[0];
                } else {
                    list[i] = gs.list(data);
                }
                i = i + 1;
                data = patt.exec(text);
            }
            object = expandWithMetaClass(list, 'RegExp');
        }

        createClassNames(object, ['java.util.regex.Matcher']);

        object.pattern = patt;
        object.text = text;

        object.replaceFirst = function(data) {
            return this.text.replaceFirst(this[0], data);
        };

        object.replaceAll = function(data) {
            return this.text.replaceAll(this.pattern, data);
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
        var object = gs.init('Pattern');

        createClassNames(object, ['java.util.regex.Pattern']);

        object.value = pattern;
        return object;
    };

    /////////////////////////////////////////////////////////////////
    // Regular Expresions
    /////////////////////////////////////////////////////////////////
    gs.matcher = function(item, regExpression) {

        var object = gs.init('Matcher');
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
        for (i = 0; i < this; i++) {
            closure(i);
        }
    };

    Number.prototype.upto = function(number, closure) {
        var i;
        for (i = this.value; i <= number; i++) {
            closure(i);
        }
    };

    Number.prototype.step = function(number, jump, closure) {
        var i;
        for (i = this.value; i < number;) {
            closure(i);
            i = i + jump;
        }
    };

    Number.prototype.multiply = function(number) {
        return this * number;
    };

    Number.prototype.power = function(number) {
        return Math.pow(this,number);
    };

    Number.prototype.byteValue = Number.prototype.doubleValue = Number.prototype.shortValue =
        Number.prototype.floatValue = Number.prototype.longValue = function() {
        return this;
    };

    Number.prototype.intValue = function() {
        return Math.floor(this);
    };

    /////////////////////////////////////////////////////////////////
    //String functions
    /////////////////////////////////////////////////////////////////
    String.prototype.contains = function(value) {
        return this.indexOf(value) >= 0;
    };

    String.prototype.startsWith = function(value) {
        return this.indexOf(value) === 0;
    };

    String.prototype.endsWith = function(value) {
        return this.indexOf(value) > -1 && this.indexOf(value) == (this.length - value.length);
    };

    String.prototype.count = function(value) {
        var reg = new RegExp(value, 'g');
        var result = this.match(reg);
        if (result) {
            return result.length;
        } else {
            return 0;
        }
    };

    String.prototype.size = function() {
        return this.length;
    };

    String.prototype.replaceAll = function(oldValue, newValue) {
        var reg;
        if (oldValue instanceof RegExp) {
            reg = new RegExp(oldValue.source, 'g');
        } else {
            reg = new RegExp(oldValue, 'g');
        }
        return this.replace(reg, newValue);
    };

    String.prototype.replaceFirst = function(oldValue, newValue) {
        return this.replace(oldValue, newValue);
    };


    String.prototype.reverse = function() {
        return this.split("").reverse().join("");
    };

    String.prototype.tokenize = function() {
        var str = " ";
        if (arguments.length == 1 && arguments[0]) {
            str = arguments[0];
        }
        var list = this.split(str);
        return gs.list(list);
    };

    String.prototype.multiply = function(value) {
        if (typeof(value) == 'number') {
            var result = '';
            var i;
            for (i=0; i < (value | 0); i++) {
                result = result + this;
            }
            return result;
        }
    };

    String.prototype.capitalize = function() {
        return this.charAt(0).toUpperCase() + this.slice(1);
    };

    String.prototype.each = function(closure) {
        var list = gs.list(this.split(''));
        list.each(closure);
    };

    String.prototype.inject = function(initial, closure) {
        var list = gs.list(this.split(''));
        return list.inject(initial, closure);
    };

    function getItemsMultiline(text) {
        var items = text.split('\n');
        if (items.length > 1 && items[items.length - 1] === '') {
            items.splice(items.length - 1, 1);
        }
        return items;
    }

    String.prototype.eachLine = function(closure) {
        var i, items = getItemsMultiline(this);
        for (i = 0; i < items.length; i++) {
            var item = items[i];
            //Closure with 2 arguments, line and count
            if (closure.length == 2) {
                closure(item, i);
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
        while (item.length < number) {
            item = item + sep;
        }
        return item;
    };

    String.prototype.padLeft = function(number) {
        var sep = ' ';
        if (arguments.length == 2) {
            sep = arguments[1];
        }
        var item = this;
        while (item.length < number) {
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
        var addText = 'null';
        if (other !== undefined && other !== null) {
            if (other['toString'] !== undefined) {
                addText = other.toString();
            } else {
                addText = other;
            }
        }
        return this + addText;
    };

    String.prototype.toInteger = function() {
        return parseInt(this);
    };

    /////////////////////////////////////////////////////////////////
    // Misc Functions
    /////////////////////////////////////////////////////////////////
    gs.classForName = function(name, obj) {
        var result = null;
        try {
            var pos = name.indexOf(".");
            while (pos >= 0) {
                name = name.substring(pos + 1);
                pos = name.indexOf(".");
            }
            result = eval(name);
        } catch (err) {
            result = obj;
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
            if (isFunction(source[prop])) continue;
            if (!isMapProperty(prop)) {
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

    gs.is = function(value1, value2) {
        if (value1 !== null && hasFunc(value1, 'is')) {
            var count, params = gs.list([value2]);
            for (count = 2; count < arguments.length; count++) {
                params.add(arguments[count]);
            }
            return gs.mc(value1, 'is', params);
        } else {
            return value1 == value2;
        }
    };

    function interceptClosureCall(func, param) {
        if ((param instanceof Array) && func.length > 1) {
            return func.apply(func, param);
        } else {
            return func(param);
        }
    }

    gs.random = function() {
        var object = gs.init('Random');
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
        if (item && item.isEmpty !== undefined) {
            return !item.isEmpty();
        } else {
            if (item) {
                if (item['asBoolean']) {
                    return item['asBoolean']();
                } else if (typeof(item) == 'number' && item === 0) {
                    return false;
                } else if (typeof(item) == 'string') {
                    return item !== '';
                }
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
        var gotIt = false;

        if (name == "String")  {
            return typeof(item) == 'string';
        } else if (name == "Number") {
            return typeof(item) == 'number';
        } else if (item.clazz) {
            var classInfo;
            classInfo = item.clazz;
            while (classInfo && !gotIt) {
                if (classInfoContainsName(classInfo, name)) {
                    gotIt = true;
                } else {
                    classInfo = classInfo.superclass;
                }
            }
            if (!gotIt && item.clazz.interfaces) {
                var i;
                for (i = 0; i < item.clazz.interfaces.length && !gotIt; i++) {
                    if (classInfoContainsName(item.clazz.interfaces[i], name)) {
                        gotIt = true;
                    }
                }
            }
        } else if (isFunction(item) && name == 'Closure') {
            gotIt = true;
        }
        return gotIt;
    };

    function classInfoContainsName(classInfo, name) {
        return classInfo.name == name || classInfo.simpleName == name;
    }

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
            return a * b;
        } else {
            return a.multiply(b);
        }
    };

    // / operator
    gs.div = function(a, b) {
        if (!hasFunc(a, 'div')) {
            return a / b;
        } else {
            return a.div(b);
        }
    };

    // ** operator
    gs.power = function(a, b) {
        if (!hasFunc(a, 'power')) {
            return Math.pow(a, b);
        } else {
            return a.power(b);
        }
    };

    // mod operator
    gs.mod = function(a, b) {
        if (!hasFunc(a, 'mod')) {
            return a % b;
        } else {
            return a.mod(b);
        }
    };

    // + operator
    gs.plus = function(a, b) {
        if (!hasFunc(a, 'plus')) {
            if ((typeof a == 'number') && (typeof b == 'number') && (a + b < 1)) {
                return ((a * 1000) + (b * 1000)) / 1000;
            } else {
                return a + b;
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
        if (group && (isFunction(group.contains))) {
            return group.contains(item);
        } else {
            return false;
        }
    };

    //For some special cases where access a property with this."${name}"
    //This can be a closure
    gs.thisOrObject = function(thisItem, objectItem) {
        return objectItem || thisItem;
    };

    // spread operator (*)
    gs.spread = function(item) {
        if (item && item instanceof Array) {
            this.values = item;
        }
    };

    /////////////////////////////////////////////////////////////////
    // Beans functions - From groovy beans
    /////////////////////////////////////////////////////////////////
    //If an object has a function by name
    function hasFunc(item, name) {
        if (item === null || item === undefined || item[name] === undefined ||
            (!isFunction(item[name]))) {
            return false;
        } else {
            return true;
        }
    }

    //Set a property of a class
    gs.sp = function(item, nameProperty, value) {

        if (nameProperty == 'setProperty') {
            item[nameProperty] = value;
        } else if (nameProperty == 'getProperty') {
            item[nameProperty] = value;
        } else if (item !== null && item instanceof StaticMethods) {
            item[nameProperty] = value;
            item.gSparent[nameProperty] = value;
        } else {
            if (nameProperty === 'methodMissing' && value) {
                item[nameProperty] = value;
            } else if (!item['setProperty']) {
                var nameFunction = 'set' + nameProperty.charAt(0).toUpperCase() + nameProperty.slice(1);

                if (!item[nameFunction]) {
                    if (item[nameProperty] === undefined &&
                        item.setPropertyMissing !== undefined &&
                        isFunction(item.setPropertyMissing)) {
                        item.setPropertyMissing(nameProperty, value);
                    } else {
                        item[nameProperty] = value;
                    }
                } else {
                    item[nameFunction](value);
                }
            } else {
                item.setProperty(nameProperty,value);
            }
        }
    };

    //Get a property of a class
    gs.gp = function(item, nameProperty, inDelegates) {

        //It's a get with safe operator as item?.data
        if (arguments.length == 3) {
            if (item === null || item === undefined) {
                return null;
            }
        } else if (item == null || item === undefined) {
            throw 'gs.gp Get property: ' + nameProperty + ' on null or undefined object.'
        }

        if (!item['getProperty']) {
            return propFromObject(item, nameProperty, inDelegates);
        } else {
            var res = item.getProperty(nameProperty);
            return (res !== undefined ? res : propFromObject(item, nameProperty, inDelegates))
        }
    };

    function propFromObject(item, nameProperty, inDelegates) {
        var nameFunction = 'get' + nameProperty.charAt(0).toUpperCase() + nameProperty.slice(1);
        if (!item[nameFunction]) {
            if (nameProperty == 'size' && isFunction(item[nameProperty])) {
                return item[nameProperty]();
            } else {
                if (item[nameProperty] !== undefined) {
                    return item[nameProperty];
                } else {
                    //Lets check gp in @Delegate
                    if (item.clazz !== undefined) {
                        var addDelegate = mapAddDelegate[item.clazz.simpleName];
                        if (addDelegate !== null && addDelegate !== undefined) {
                            var i;
                            for (i = 0; i < addDelegate.length; i++) {
                                var prop = addDelegate[i];
                                var target = item[prop][nameProperty];
                                if (target !== undefined) {
                                    return item[prop][nameProperty];
                                }
                            }
                        }
                    }
                    //Default value of a map
                    if (item.gSdefaultValue !== undefined && (isFunction(item.gSdefaultValue))) {
                        item[nameProperty] = item.gSdefaultValue();
                    }
                    //Maybe in categories
                    if (categories.length > 0 && item[nameProperty] === undefined) {
                        var whereExecutes = categorySearching(nameFunction);
                        if (whereExecutes !== null) {
                            return whereExecutes[nameFunction].apply(item, [item]);
                        }
                    }

                    if (item.propertyMissing !== undefined && isFunction(item.propertyMissing)) {
                        return item.propertyMissing(nameProperty);
                    } else {
                        if (!inDelegates && delegates.length > 0) {
                            return findPropertyInDelegates(nameProperty, item);
                        } else {
                            return item[nameProperty];
                        }
                    }
                }
            }
        } else {
            return item[nameFunction]();
        }
    }

    function findPropertyInDelegates(nameProperty, item) {
        var i = delegates.length;
        var found = false;
        var result;
        while (i > 0 && !found && item ) {
            i = i - 1;
            result = gs.gp(delegates[i], nameProperty, true);
            if (result !== undefined) {
                found = true;
            }
        }
        return result;
    }

    //Control property changes with ++,--
    gs.plusPlus = function(item, nameProperty, plus, before) {
        var value = gs.gp(item, nameProperty);
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

    function exFn(we, mn, it, val) {
        return we[mn].apply(it, joinParameters(it, val));
    }

    //Control all method calls
    gs.mc = function(item, methodName, values, objectVar, isSafe) {

        if (gs.consoleInfo && console) {
            console.log('[INFO] gs.mc (' + item + ').' + methodName + ' params:' + values);
        }

        if (item === null || item === undefined) {
            if (isSafe) {
                return null;
            } else {
                throw 'gs.mc Calling method: ' + methodName + ' on null or undefined object.';
            }
        }

        if (methodName == 'split' && typeof(item) == 'string') {
            return item.tokenize(values[0]);
        }
        if (methodName == 'length' && typeof(item) == 'string') {
            return item.length;
        }
        if (methodName == 'join' && (item instanceof Array)) {
            if (values.size() > 0) {
                return item.gSjoin(values[0]);
            } else {
                return item.gSjoin();
            }
        }

        if (objectVar) {
            try {
                //First, try to execute function in object
                return gs.mc(objectVar, methodName, values);
            } catch(e) {}
        }

        if (!item[methodName]) {

            if (methodName.startsWith('get') || methodName.startsWith('set')) {
                var varName = getterSetterRemove(methodName);
                if (item[varName] !== undefined && !hasFunc(item, varName)) {
                    if (methodName.startsWith('get')) {
                        return gs.gp(item, varName);
                    } else {
                        return gs.sp(item, varName, values[0]);
                    }
                }
            }

            if (methodName.startsWith('is')) {
                var varName = methodName.charAt(2).toLowerCase() + methodName.slice(3);
                if (item[varName] !== undefined && !hasFunc(item, varName)) {
                    return gs.gp(item, varName);
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
                        return exFn(whereExecutes, methodName, item, values);
                    }
                }

                //In @Category
                var ob;
                for (ob in annotatedCategories) {
                    if (annotatedCategories[ob] == item.clazz.simpleName) {
                        var categoryItem = gs.myCategories[ob]();
                        if (categoryItem[methodName] && isFunction(categoryItem[methodName])) {
                            return exFn(categoryItem, methodName, item, values);
                        }
                    }
                }

                //Lets check in mixins classes
                if (mixins.length > 0) {
                    whereExecutes = mixinSearching(item, methodName);
                    if (whereExecutes !== null) {
                        return exFn(whereExecutes, methodName, item, values);
                    }
                }

                //Lets check in mixins objects
                if (mixinsObjects.length > 0) {
                    whereExecutes = mixinObjectsSearching(item, methodName);
                    if (whereExecutes !== null) {
                        return exFn(whereExecutes, methodName, item, values);
                    }
                }
                //Lets check mc in @Delegate
                if (item.clazz !== undefined) {
                    var addDelegate = mapAddDelegate[item.clazz.simpleName];
                    if (addDelegate) {
                        var i;
                        for (i = 0; i < addDelegate.length; i++) {
                            var prop = addDelegate[i];
                            var target = item[prop][methodName];
                            if (target !== undefined) {
                                return exFn(item[prop], methodName, item[prop], values);
                            }
                        }
                    }
                }

                //Lets check in delegate
                if (delegates.length > 0) {
                    var delegateFunc = delegatesFunc(methodName);
                    if (delegateFunc) {
                        return delegateFunc[methodName].apply(item, values);
                    }
                }

                if (item.methodMissing) {
                    return item.methodMissing(methodName, values);
                } else if (delegates.length > 0 && delegatesFunc('methodMissing')) {
                    return gs.mc(delegatesFunc('methodMissing'), methodName, values);
                } else {
                    if (item.invokeMethod && item.invokeMethod !== BaseClass.prototype.invokeMethod) {
                        return item.invokeMethod(methodName, values);
                    } else {
                        //Maybe there is a function in the script with the name of the method
                        //In Node.js 'this.xxFunction()' in the main context fails
                        if (isFunction(eval(methodName))) {
                            return eval(methodName).apply(this, values);
                        }

                        //Not exist the method, throw exception
                        throw 'gs.mc Method ' + methodName + ' not exist in ' + item;
                    }
                }
            }

        } else {
            var f = item[methodName];
            if (f['apply']) {
                return f.apply(item, values);
            } else {
                return gs.execCall(f, item, values);
            }
        }
    };

    function delegatesFunc(nameMethod) {
        var result = null;
        if (delegates.length > 0) {
            var i;
            for (i = delegates.length - 1; i >= 0 && !result; i--) {
                if (delegates[i][nameMethod]) {
                    result = delegates[i];
                }
            }
        }
        return result;
    }

    function joinParameters(item, items) {
        var listParameters = [item],i;
        for (i=0; i < items.size(); i++) {
            listParameters.push(items[i]);
        }
        return listParameters;
    }

    ////////////////////////////////////////////////////////////
    // Categories
    ////////////////////////////////////////////////////////////
    gs.categoryUse = function(item, itemClass, closure) {
        var ob, categoryCreated;
        if (existAnnotatedCategory(item)) {
            categoryCreated = gs.myCategories[item]();
            for (ob in categoryCreated) {
                if (!isObjectProperty(ob) && !isConstructor(ob, categoryCreated[ob]) &&
                    isFunction(categoryCreated[ob])) {
                    addFunctionToClassIfPrototyped(ob, categoryCreated[ob], annotatedCategories[item]);
                }
            }
        } else {
            categories.push(itemClass);
        }
        closure();
        if (existAnnotatedCategory(item)) {
            categoryCreated = gs.myCategories[item]();
            for (ob in categoryCreated) {
                if (!isObjectProperty(ob) && !isConstructor(ob, categoryCreated[ob]) &&
                    isFunction(categoryCreated[ob])) {
                    removeFunctionToClass(ob, categoryCreated[ob], annotatedCategories[item]);
                }
            }
        } else {
            categories.splice(categories.length - 1, 1);
        }
    };

    function getPrototypeOfClass(className) {
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
        var proto = getPrototypeOfClass(className);
        if  (proto !== null) {
            if (proto[name] === undefined) {
                proto[name] = func;
            }
        }
    }

    function removeFunctionToClass(name, func, className) {
        var proto = getPrototypeOfClass(className);
        if  (proto !== null) {
            if (proto[name] == func) {
                proto[name] = null;
            }
        }
    }

    function categorySearching(methodName) {
        var i, result = null;
        for (i = categories.length - 1; i >= 0 && result === null; i--) {
            var itemClass = categories[i];
            if (itemClass[methodName]) {
                result = itemClass;
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
            for (i = 0; i < mixins.length && !gotIt; i++) {
                if (mixins[i].name == item) {
                    var j;
                    for (j=0; j < classes.length; j++) {
                        mixins[i].items.push(classes[j]);
                    }
                    gotIt = true;
                }
            }
        }
        if (!gotIt) {
            mixins.push({ name: item, items: classes});
        }
    };

    gs.mixinObject = function(item, classes) {

        var gotIt = false;
        if (mixinsObjects.length > 0) {
            var i;
            for (i = 0; i < mixinsObjects.length && !gotIt; i++) {
                if (mixinsObjects[i].item == item) {
                    var j;
                    for (j = 0; j < classes.length; j++) {
                        mixinsObjects[i].items.push(classes[j]);
                    }
                    gotIt = true;
                }
            }
        }
        if (!gotIt) {
            mixinsObjects.push({ item: item, items: classes});
        }
        //TODO make any kinda cleanup if mixinsObjects growing
    };

    function mixinSearching(item, methodName) {
        var result = null, className = null;
        if (typeof(item) == 'string') {
            className = 'String';
        }
        if (item.clazz && item.clazz.simpleName && typeof(item) == 'object') {
            className = item.clazz.simpleName;
        }
        if (className !== null) {
            var i, ourMixin=null;
            for (i = mixins.length - 1; i >= 0 && ourMixin === null; i--) {
                var data = mixins[i];
                if (data.name == className) {
                    ourMixin = data.items;
                }
            }
            if (ourMixin !== null) {
                for (i = 0; i < ourMixin.length && result === null; i++) {
                    if (ourMixin[i][methodName]) {
                        result = ourMixin[i];
                    } else {
                        var classItem = ourMixin[i]();
                        if (classItem) {
                            var notStatic = classItem[methodName];
                            if (notStatic !== null && isFunction(notStatic)) {
                                result = classItem;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    function mixinObjectsSearching(item, methodName) {

        var result = null, i, ourMixin = null;
        for (i = mixinsObjects.length - 1; i >= 0 && ourMixin === null; i--) {
            var data = mixinsObjects[i];
            if (data.item == item) {
                ourMixin = data.items;
            }
        }
        if (ourMixin !== null) {
            for (i=0 ; i < ourMixin.length && result === null; i++) {
                if (ourMixin[i][methodName]) {
                    result = ourMixin[i];
                }
            }
        }

        return result;
    }

    ////////////////////////////////////////////////////////////
    // StringBuffer - very basic support, for add with <<
    ////////////////////////////////////////////////////////////
    gs.stringBuffer = function() {

        var object = gs.init('StringBuffer');
        object.value = '';

        if (arguments.length == 1 && typeof arguments[0] === 'string') {
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
    // @Delegate
    ////////////////////////////////////////////////////////////
    gs.astDelegate = function (baseClass, nameField) {
        var currentDelegate = mapAddDelegate[baseClass];
        if (currentDelegate === null || currentDelegate === undefined) {
            currentDelegate = [];
        }
        currentDelegate.push(nameField);
        mapAddDelegate[baseClass] = currentDelegate;
    };

    ////////////////////////////////////////////////////////////
    // Delegate
    ////////////////////////////////////////////////////////////
    function applyDelegate (func, delegate, params) {
        delegates.push(delegate);
        var result = func.apply(delegate, params);
        delegates.pop();
        return result;
    }

    gs.execCall = function (func, thisObject, params) {
        if (func.delegate !== undefined) {
            return applyDelegate(func, func.delegate, params);
        } else {
            if (func['call'] !== undefined && typeof func === 'object') {
                return func['call'].apply(func, params);
            } else {
                return func.apply(thisObject, params);
            }
        }
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

    Function.prototype.run = function() {
        return this();
    };

    Function.prototype.memoize = function() {
        var that = this;
        that._input = [];
        that._output = [];
        return function() {
            var i, result, foundPos = -1, inputs = Array.prototype.slice.call(arguments);
            for (i = 0; i < that._input.length && foundPos < 0; i++) {
                if (gs.equals(inputs, that._input[i])) {
                    foundPos = i;
                }
            }
            if (foundPos > -1) {
                result = that._output[foundPos];
            } else {
                that._input.push(inputs);
                result = that.apply(null, inputs);
                that._output.push(result);
            }
            return result;
        };
    };

    //MISC Find scope of a var
    gs.fs = function(name, thisScope) {
        if (thisScope && thisScope[name] !== undefined) {
            return thisScope[name];
        } else {
            var value = gs.gp(thisScope, name);
            if (value === undefined) {
                if (aStT && aStT[name] !== undefined) {
                    return aStT[name];
                } else {
                    var func = new Function("return " + name);
                    return func();
                }
            } else {
                return value;
            }
        }
    };

    //Convert a groovy object to javascript, but only properties
    gs.toJavascript = function(obj) {
        if (obj && gs.isGroovyObj(obj)) {
            var result;
            if (obj && !isFunction(obj)) {
                if (obj instanceof Array) {
                    result = [];
                    var i;
                    for (i = 0; i < obj.length; i++) {
                        result.push(gs.toJavascript(obj[i]));
                    }
                } else {
                    if (obj instanceof Object) {
                        result = {};
                        var ob;
                        for (ob in obj) {
                            if (!isMapProperty(ob) && !isFunction(obj[ob])) {
                                result[ob] = gs.toJavascript(obj[ob]);
                            }
                        }
                    } else {
                        result = obj;
                    }
                }
            }
            return result;
        } else {
            return obj;
        }
    };

    //Convert a javascript object to 'groovy', if you define groovy type, will use it, and not a map
    gs.toGroovy = function(obj, objClass) {
        var result;
        if (obj && !isFunction(obj)) {
            if (obj instanceof Array) {
                result = gs.list([]);
                var i;
                for (i = 0; i < obj.length; i++) {
                    result.add(gs.toGroovy(obj[i], objClass));
                }
            } else {
                if (obj instanceof Object) {
                    var ob;
                    if (objClass) {
                        result = objClass();
                        for (ob in obj) {
                            result[ob] = gs.toGroovy(obj[ob]);
                        }
                    } else {
                        result = gs.map();
                        for (ob in obj) {
                            result.add(ob, gs.toGroovy(obj[ob]));
                        }
                    }
                } else {
                    result = obj;
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

    gs.isGroovyObj = function(maybeGroovyObject) {
        return maybeGroovyObject !== null && maybeGroovyObject !== undefined && maybeGroovyObject.clazz !== undefined;
    };

    gs.execStatic = function(obj, methodName, thisObject, params) {
        var old = aStT;
        aStT = thisObject;
        var res = obj[methodName].apply(thisObject, params);
        aStT = old;
        return res;
    };

    gs.asChar = function(value) {
        return value.charCodeAt(0);
    };

    //Convert a groovy map to javascript object, including functions in the map
    gs.toJsObj = function(obj) {
        if (gs.isGroovyObj(obj)) {
            var ob, result = {};
            for (ob in obj) {
                if (!isMapProperty(ob)) {
                    if (isFunction(obj[ob])) {
                        result[ob] = obj[ob];
                    } else {
                        result[ob] = gs.toJsObj(obj[ob]);
                    }
                }
            }
            return result;
        } else {
            return obj;
        }
    };

}).call(this);
gsClass = {

}

function gScreateExpando() {
    var object = inherit(gsClass);
     return object;
}

function inherit(p) {
    if (p == null) throw TypeError();
    if (Object.create)
        return Object.create(p);
    var t = typeof p;

    // If Object.create() is defined... // then just use it.
    // Otherwise do some more type checking
    if (t !== "object" && t !== "function")
        throw TypeError();

    function f() {};
    f.prototype = p;
    return new f();
}

/////////////////////////////////////////////////////////////////
// gSmap
/////////////////////////////////////////////////////////////////
function gSmap() {
    var object = inherit(gsClass);
    object.add = function(key,value) {
        this[key] = value;
        return this;
    }
    object.put = function(key,value) {
        return this.add(key,value)
    }
    object.size = function() {
        var number = 0;
        for (ob in this) {
            if (typeof this[ob] !== "function") {
                number++;
            }
        }
        return number;
    }
    object.each = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function") {
                var f = arguments[0];
                //Nice, number of arguments in length property
                if (f.length==1) {
                    closure({key:ob, value:this[ob]})
                }
                if (f.length==2) {
                    closure(ob,this[ob]);
                }
            }
        }
    }
    object.get = function(key,defaultValue) {
        if (arguments.length ==2 && (this[key]=='undefined' || this[key]==null)) {
            return defaultValue;
        } else {
            return this[key];
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
            if (typeof this[ob] !== "function") {
                if (this[ob]==value) {
                    gotIt = true;
                    break;
                }
            }
        }
        return gotIt;
    }
    return object;
}

/////////////////////////////////////////////////////////////////
//gsList
/////////////////////////////////////////////////////////////////
function gSlist(value) {
    var object = inherit(Array.prototype);
    object = value;

    object.get = function(pos) {
        return this[pos];
    }

    object.size = function() {
        return this.length;
    }

    object.add = function(element) {
        this[this.length]=element;
        return this;
    }

    object.contains = function(object) {
        var gotIt,i;
        for (i=0;!gotIt && i<this.length;i++) {
            if (this[i]==object) {
                if (typeof this[i] === "function") continue;
                gotIt = true;
            }
        }
        return gotIt;
    }
    object.each = function(closure) {
        for (i=0;i<this.length;i++) {
            if (typeof this[i] === "function") continue;
            closure(this[i]);
        }
        return this;
    }

    object.values = function() {
        var result = []
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
            if (typeof this[i] === "function") continue;
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
    /*
    object.recorre = function() {
        for (element in this) {
            if (typeof this[element] === "function") continue;
            console.log('El->'+this[element]);
        }
    }
    */

    return object;
}

/////////////////////////////////////////////////////////////////
//gSrange
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
//gSdate
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

    return object;
}

//TODO have to work on this
function gSrangeFromList(list,begin,end) {
    return list.slice(begin,end+1)
}

/////////////////////////////////////////////////////////////////
//gSexactMatch
/////////////////////////////////////////////////////////////////
function gSexactMatch(text,regExp) {
    var mock = text;

    if (regExp instanceof RegExp) {
        mock = mock.replace(regExp,"#");
    } else {
        mock = mock.replace(new RegExp(regExp),"#");
    }

    //console.log('After->'+mock);
    return mock == "#";
}

/////////////////////////////////////////////////////////////////
//gSregExp
/////////////////////////////////////////////////////////////////
function gSregExp(text,pattern) {
    var object = inherit(gsClass);
    if (pattern instanceof RegExp) {
        object.pattern = new RegExp(pattern.source,'g');
    } else {
        //g for search all occurences
        object.pattern = new RegExp(pattern,'g');
    }
    object.text = text;

    object.each = function(closure) {
        //console.log('text->'+this.text);
        //console.log('pattern->'+this.pattern);
        //match function dont work as expected, only returns 1 result
        var result = this.text.match(this.pattern);
        if (result != null) {
            //console.log('res->'+result);
            var i;
            for (i=0;i<result.length;i++) {
                closure(result[i]);
            }
        }

    }

    return object;
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
    var reg = new RegExp(oldValue,'g');
    return this.replace(reg,newValue);
}

/////////////////////////////////////////////////////////////////
// Misc Functions
/////////////////////////////////////////////////////////////////
function gSmetaClass(item) {
    var type = typeof item;
    //console.log('typeof before-'+typeof item);
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
        destination[prop] = source[prop];
    }
}
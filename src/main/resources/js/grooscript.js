/////////////////////////////////////////////////////////////////
// assert and println conversions
/////////////////////////////////////////////////////////////////
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

var gSconsole = "";

function gSprintln(value) {
    //console.log(value);
    if (gSconsole != "") {
        gSconsole = gSconsole + "\n"
    }
    gSconsole = gSconsole + value
};

/////////////////////////////////////////////////////////////////
// Class functions
/////////////////////////////////////////////////////////////////
gsClass = {
    //The with function, with is a reserved word in JavaScript
    gSwith : function(closure) { closure.apply(this,closure.arguments); }
}

function gSexpando() {
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
    object.putAt = function(key,value) {
        this.put(key,value)
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
    object.isEmpty = function() {
        return (this.size() == 0);
    }
    object.each = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function") {
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

    object.any = function(closure) {
        for (ob in this) {
            if (typeof this[ob] !== "function") {
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
            if (typeof this[ob] !== "function") {
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
            if (typeof this[ob] !== "function") {
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
            if (typeof this[ob] !== "function") {
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
            if (typeof this[ob] !== "function") {
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
            if (typeof this[ob] !== "function") {
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

        /*if (otherMap==null || otherMap=='undefined' || otherMap.toString==null ||  otherMap.toString== 'undefined' || !(typeof otherMap.toString === "function")) {
            return false;
        } else {
            return this.toString() == otherMap.toString();
        }
        */
        var result = true;
        for (ob in this) {
            if (typeof this[ob] !== "function") {
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
            if (typeof this[ob] !== "function") {
                result.add(this[ob]);
            }
        }
        return result;
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

    /*
    object.recorre = function() {
        for (element in this) {
            if (typeof this[element] === "function") continue;
            console.log('El->'+this[element]);
        }
    }
    */
    //object.equals = function(other) {
    //    return true;
    //}
    //Array.prototype.equals = function(other) {
    //    return true;
    //}
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
//gSregExp
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
        object = inherit(gSlist(list));
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
    var object = inherit(gsClass);
    object.value = pattern;
    return object;
}

/////////////////////////////////////////////////////////////////
// Regular Expresions
/////////////////////////////////////////////////////////////////
function gSmatcher(item,regExpression) {

    var object = inherit(gsClass);

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
    if (arguments.length==1) {
        str = arguments[0];
    }
    var list = this.split(str);
    return gSlist(list);
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

/*
function gSControlParameters(params) {
    gSprintln('Going!'+params+' '+params[0]);
    //if (params.length > 1) {
        gSprintln('1-'+(params instanceof Array)+ ' 2-'+params.arguments+' 3-'+params.length);
        if ((params instanceof Array) && params[1]==null && params[0].size()==params.length) {
            gSprintln('YEAH!');
        }
    //}
}
*/
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
    var object = inherit(gsClass);
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
    if (item!=null && item!='undefined' && item.isEmpty!=null) {
        //console.log('bool yeah->'+!item.isEmpty());
        return !item.isEmpty();
    } else {
        return item;
    }
}





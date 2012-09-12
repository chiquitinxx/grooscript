gsClass = {

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
    var object = inherit(gsClass)
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

/*
function gSassert(value) {
    console.log('Assert-'+value);
    if (!value) {
         console.log('FAILS!');
    } else {
         console.log('OK.');
    }
}
*/

/*
var doubled = gSlist([1 , 2 , 3]).collect(function(item) {
  return item * 2;
});
console.log('l->'+doubled);
gSassert((doubled [ 0]) == 2);
gSassert((doubled [ 1]) == 4);
gSassert((doubled [ 2]) == 6);
gSassert(doubled.size() == 3);
*/

/*
list = gSlist(["a" , "b" , "c" , "b" , "d" , "c"]);
list.removeAll(gSlist(["b" , "c"]));
gSassert(list.size() == 2);
gSassert((list [ 1]) == "d");
*/

/*
var list = gSlist([1 , 2]);
gSassert((list.reverse() [ 0]) == 2);
list.add(3);
gSassert(list.size() == 3);
gSassert((list.sort() [ 0]) == 1);
list = gSlist([1 , "A" , 3]);
list.remove(2);
list.remove("A");
gSassert(list.size() == 1);
gSassert((list [ 0]) == 1);
*/

/*
for (element in  [5,6,7,8,9]) { //gSrange(5, 9).values()) {
    console.log('it element->'+element);
  log += element;
};
*/

/*
log = "";
for (element in gSrange(9, 5)) {
  log += element;
};
gSassert(log == "98765");
log = "";
gSrange(9, 5).each(function(element) {
  log += element;
});
gSassert(log == "9876");
*/

/*
var list = gSlist([5 , 6 , 7 , 8]);
console.log('list->'+list);
console.log('range->'+gSrangeFromList(list, 1, 2));
console.log('range->'+gSlist([6 , 7]));
console.log('siono->'+((gSrangeFromList(list, 1, 2)) == gSlist([6 , 7])))
console.log('siono array->'+([1,2]==[1,2]))
//gSassert((gSrangeFromList(list, 1, 2)) == gSlist([6 , 7]));

console.log('r(-1,3)->'+gSrange(-1,3))
console.log('r(5,1)->'+gSrange(5,1))
console.log('r(1,1)->'+gSrange(1,1))
console.log('r(1,0)->'+gSrange(1,0))
*/

/*
function gsCreateMyClass() {
    var object = inherit(gsClass)
    object.name = ''
    object.say = function () {console.log('Hey!')}
    object.say = function (something) {console.log('Hey!->'+something)}

    return object
}

var me = gsCreateMyClass();
me.name = 'Mac';
console.log('yo='+me.yo);
me.yo = 'Yo'
console.log('name='+me.name);
me.say();
me.say('Bob');
*/
/*
var lista = [1,2,3]
lista[4] = 4
console.log('List->'+lista)
var lista2 = gSlist([])
console.log('List2->'+lista2)
lista2[0]='hola'
lista2[1]=3
console.log('List2->'+lista2)
lista2.recorre()
*/

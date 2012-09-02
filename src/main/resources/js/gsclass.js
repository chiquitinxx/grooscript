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

function gSmap() {
    var object = inherit(gsClass)
    object.add = function(key,value) {
        this[key] = value;
        return this;
    }
    return object;
}

function gSlist(value) {
    var object = inherit(Array.prototype)
    object = value

    object.get = function(pos) {
        return this[pos]
    }

    object.size = function() {
        return this.length
    }

    object.add = function(element) {
        this[this.length]=element
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

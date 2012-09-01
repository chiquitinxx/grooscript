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
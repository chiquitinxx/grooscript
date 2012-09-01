var gSfails = false;

function gSassert(value) {
    if(value==false) {
          gSfails = true;
          console.log ('Assert Fails!')
    }
}

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

myClass = gsCreateMyClass();
gSassert(myClass.numberMessages == 0);
gSassert(!myClass.name);
myClass.name = "Fan";
console.log("name->"+myClass.name);
myClass.saySomething("GroovyRocks");
gSassert(myClass.numberMessages == 1);
gSassert(myClass.name == "Fan");

function gsCreateMyClass() {
  var object = inherit(gsClass);
  object.numberMessages = 0;
  object.MyClass1 = function(startingName) {
    this.name = startingName;
    }
  object.MyClass0 = function() {
    this.name = "";
    }
  object.saySomething = function(message) {
    console.log("" + this.name + ": " + message + "");
    this.numberMessages++;
    }
    return object;
  }

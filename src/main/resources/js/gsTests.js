var gSfails = false;

function gSassert(value) {
    if(value==false) {
          gSfails = true;
          gSprintln('Assert Fails!-'+value)
    }
}

var gSconsole = ""

function gSprintln(value) {
    if (gSconsole != "") {
        gSconsole = gSconsole + "\n"
    }
    gSconsole = gSconsole + value
}

function gSpassMapToObject(source,destination) {
    for (prop in source) {
        destination[prop] = source[prop]
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

function gSmap() {
    var object = inherit(gsClass)
    object.add = function(key,value) {
        this[key] = value;
        return this;
    }
    return object;
}
//////////////////////////////////////////////////////////////////////////// End of imports

sayHelloTo = function(it) {
  return "Hello " + it + "!";
  };

  //Quitado this.
gSassert(this.sayHelloTo("Groovy") == "Hello Groovy!");
var getCode = function(house) {
  var minValue = 5;
  console.log("Number->"+house.number)
  if (house.number > minValue) {
    return 1;
    } else {
    return 0;
    };
  };
var house = gsCreateHouse().House1(gSmap().add("address","Groovy Street").add("number",3));
//console.log ("Numero->"+house.number);
gSassert(house.number == 3);
gSassert(!house.code);
gSassert(house.code == null);
house.applyCode(getCode);
gSassert(house.code == 0);
function gsCreateHouse() {
  var object = inherit(gsClass);
  object.House1 = function(map) {
    /*console.log('Hola ->'+map);
    for (u in map) {
        console.log('Prop:'+u+" - "+map[u]);
    }*/
    gSpassMapToObject(map,this);
        for (x in this) {
            console.log('This:'+x+" - "+this[x]);
        }
    console.log('Adios');
    return this;
  };
  object.applyCode = function(closure) {
    this.code = closure(this);
    }
  return object;
}

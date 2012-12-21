var fs = require('fs');

// file is included here:
eval(fs.readFileSync('grooscript.js')+'');

var a = gSlist([]);

console.log('Ready to Test!');

/////////////////////////////////////////////////////////// Tests here

function A() {
  var gSobject = inherit(gsBaseClass);
  gSobject.value = null;
  gSobject.A1 = function(map) { gSpassMapToObject(map,this); return this;};
  if (arguments.length==1) {gSobject.A1(arguments[0]); }

  return gSobject;
};

function B() {
  var gSobject = inherit(gsBaseClass);
  gSobject.b = null;
  gSobject.number = null;
  gSobject.setB = function(value) {
    gSobject.b = value;
    return gSsetProperty(this,"number",(gSmultiply(5, value)));
  }
  gSobject.putValueToB = function(value) {
    return gSobject.b = value;
  }
  gSobject.B1 = function(value) {
    gSobject.number = value;
    return this;
  }
  if (arguments.length==1) {gSobject.B1(arguments[0]); }

  return gSobject;
};


var a = A();
gSsetProperty(a,"value",5);
gSassert(gSequals(gSgetProperty(a,"value"), 5), null);
gSsetMethod(a,"setValue",(6));
gSassert(gSequals(gSgetProperty(a,"value"), 6), null);
var number = 0;
gSsetProperty((a = gSmetaClass(a)),"setProperty",function(name, value) {
  return number++;
});
gSsetProperty(a,"value",7);
gSassert(gSequals(number, 1), null);
gSassert(gSequals(gSgetProperty(a,"value"), 6), null);
gSsetProperty((a = gSmetaClass(a)),"setProperty",function(name, value) {
  return a"" + (name) + "" = value;
});
gSsetProperty(a,"value",8);
gSassert(gSequals(number, 1), null);
gSassert(gSequals(gSgetProperty(a,"value"), 8), null);
b = B(5);
gSsetProperty(b,"b",5);
gSassert(gSequals(gSgetProperty(b,"b"), 5), null);
gSassert(gSequals(gSgetProperty(b,"number"), 25), null);
gSsetMethod(b,"setB",(6));
gSassert(gSequals(gSgetProperty(b,"b"), 6), null);
gSassert(gSequals(gSgetProperty(b,"number"), 30), null);
b.putValueToB(9);
gSassert(gSequals(gSgetProperty(b,"b"), 9), null);
gSassert(gSequals(gSgetProperty(b,"number"), 30), null);

////////////////////////////////////////--------------------End Test here -> Resume

console.log('\nConsole OutPut\n--------------\n'+gSconsole);
console.log('\nFails = '+gSfails);

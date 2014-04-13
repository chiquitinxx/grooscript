//This script needs grooscript.js and jQuery to run
function Binder() {
  var gSobject = gs.inherit(gs.baseClass,'Binder');
  gSobject.clazz = { name: 'org.grooscript.jquery.Binder', simpleName: 'Binder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.jQuery = null;
  gSobject['bindAllProperties'] = function(target, closure) {
    if (closure === undefined) closure = null;
    return gs.mc(gs.gp(target,"properties"),"each",gs.list([function(name, value) {
      if (gs.mc(gSobject.jQuery,"existsId",gs.list([name]))) {
        gs.mc(gSobject.jQuery,"bind",gs.list(["#" + (name) + "", target, name, closure]));
      };
      if (gs.mc(gSobject.jQuery,"existsName",gs.list([name]))) {
        gs.mc(gSobject.jQuery,"bind",gs.list(["[name='" + (name) + "']", target, name, closure]));
      };
      if (gs.mc(gSobject.jQuery,"existsGroup",gs.list([name]))) {
        return gs.mc(gSobject.jQuery,"bind",gs.list(["input:radio[name=" + (name) + "]", target, name, closure]));
      };
    }]));
  }
  gSobject['bindAllMethods'] = function(target) {
    return gs.mc(gs.gp((target = gs.metaClass(target)),"methods"),"each",gs.list([function(method) {
      if (gs.mc(gs.gp(method,"name"),"endsWith",gs.list(["Click"]))) {
        var shortName = gs.mc(gs.gp(method,"name"),"substring",gs.list([0, gs.minus(gs.mc(gs.gp(method,"name"),"length",gs.list([])), 5)]));
        if (gs.mc(gSobject.jQuery,"existsId",gs.list([shortName]))) {
          return gs.mc(gSobject.jQuery,"bindEvent",gs.list([shortName, "click", target["" + (gs.gp(method,"name")) + ""]]));
        };
      };
    }]));
  }
  gSobject['call'] = function(target, closure) {
    if (closure === undefined) closure = null;
    gs.mc(gSobject,"bindAllProperties",gs.list([target, closure]));
    return gs.mc(gSobject,"bindAllMethods",gs.list([target]));
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};

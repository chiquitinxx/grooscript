//This script needs grooscript.js and jQuery to run
function Binder() {
  var gSobject = gs.inherit(gs.baseClass,'Binder');
  gSobject.clazz = { name: 'org.grooscript.jquery.Binder', simpleName: 'Binder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.gQuery = GQueryImpl();
  gSobject['bindAllProperties'] = function(target, closure) {
    if (closure === undefined) closure = null;
    return gs.mc(gs.gp(target,"properties"),"each",[function(name, value) {
      if (gs.mc(gSobject.gQuery,"existsId",[name])) {
        gs.mc(gSobject.gQuery,"bind",["#" + (name) + "", target, name, closure]);
      };
      if (gs.mc(gSobject.gQuery,"existsName",[name])) {
        gs.mc(gSobject.gQuery,"bind",["[name='" + (name) + "']", target, name, closure]);
      };
      if (gs.mc(gSobject.gQuery,"existsGroup",[name])) {
        return gs.mc(gSobject.gQuery,"bind",["input:radio[name=" + (name) + "]", target, name, closure]);
      };
    }]);
  }
  gSobject['bindAllMethods'] = function(target) {
    return gs.mc(gs.gp((target = gs.metaClass(target)),"methods"),"each",[function(method) {
      if (gs.mc(gs.gp(method,"name"),"endsWith",["Click"])) {
        var shortName = gs.mc(gs.gp(method,"name"),"substring",[0, gs.minus(gs.mc(gs.gp(method,"name"),"length",[]), 5)]);
        if (gs.mc(gSobject.gQuery,"existsId",[shortName])) {
          return gs.mc(gSobject.gQuery,"bindEvent",[shortName, "click", target["" + (gs.gp(method,"name")) + ""]]);
        };
      };
    }]);
  }
  gSobject['call'] = function(target, closure) {
    if (closure === undefined) closure = null;
    gs.mc(gSobject,"bindAllProperties",[target, closure]);
    return gs.mc(gSobject,"bindAllMethods",[target]);
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};

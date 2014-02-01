//This script needs grooscript.js to run
function Builder() {
  var gSobject = gs.inherit(gs.baseClass,'Builder');
  gSobject.clazz = { name: 'org.grooscript.builder.Builder', simpleName: 'Builder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.html = null;
  gSobject.build = function(x0) { return Builder.build(x0); }
  gSobject['t'] = function(text) {
    return gSobject.html += text;
  }
  gSobject['methodMissing'] = function(name, args) {
    gSobject.html += "<" + (name) + "";
    if ((((gs.bool(args)) && (gs.mc(args,"size",gs.list([])) > 0)) && (!gs.instanceOf((args [ 0]), "String"))) && (!gs.instanceOf((args [ 0]), "Closure"))) {
      gs.mc(args [ 0],"each",gs.list([function(key, value) {
        return gSobject.html += " " + (key) + "='" + (value) + "'";
      }]));
    };
    gSobject.html += ">";
    if (gs.bool(args)) {
      if ((gs.equals(gs.mc(args,"size",gs.list([])), 1)) && (gs.instanceOf((args [ 0]), "String"))) {
        gSobject.html += (args [ 0]);
      } else {
        var lastArg = gs.mc(args,"last",gs.list([]));
        if (gs.instanceOf(lastArg, "Closure")) {
          gs.sp(lastArg,"delegate",this);
          (lastArg.delegate!=undefined?gs.applyDelegate(lastArg,lastArg.delegate,[]):lastArg());
        };
      };
    };
    return gSobject.html += "</" + (name) + ">";
  }
  gSobject['Builder0'] = function(it) {
    gSobject.html = "";
    return this;
  }
  if (arguments.length==0) {gSobject.Builder0(); }
  gSobject.Builder1 = function(map) { gs.passMapToObject(map,this); return this;};
  if (arguments.length==1) {gSobject.Builder1(arguments[0]); }
  
  return gSobject;
};
Builder.build = function(closure) {
  var builder = Builder();
  gs.sp(closure,"delegate",builder);
  (closure.delegate!=undefined?gs.applyDelegate(closure,closure.delegate,[]):closure());
  return gs.gp(builder,"html");
}

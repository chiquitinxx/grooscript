//This script needs grooscript.js to run
function HtmlBuilder() {
  var gSobject = gs.inherit(gs.baseClass,'HtmlBuilder');
  gSobject.clazz = { name: 'org.grooscript.builder.HtmlBuilder', simpleName: 'HtmlBuilder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.html = null;
  gSobject.build = function(x0) { return HtmlBuilder.build(x0); }
  gSobject['yield'] = function(text) {
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
        gs.mc(gSobject,"yield",gs.list([args [ 0]]));
      } else {
        var lastArg = gs.mc(args,"last",gs.list([]));
        if (gs.instanceOf(lastArg, "Closure")) {
          gs.sp(lastArg,"delegate",this);
          (lastArg.delegate!=undefined?gs.applyDelegate(lastArg,lastArg.delegate,[]):gs.executeCall(lastArg, gs.list([])));
        };
        if ((gs.instanceOf(lastArg, "String")) && (gs.mc(args,"size",gs.list([])) > 1)) {
          gs.mc(gSobject,"yield",gs.list([lastArg]));
        };
      };
    };
    return gSobject.html += "</" + (name) + ">";
  }
  gSobject['HtmlBuilder0'] = function(it) {
    gSobject.html = "";
    return this;
  }
  if (arguments.length==0) {gSobject.HtmlBuilder0(); }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};
HtmlBuilder.build = function(closure) {
  var builder = HtmlBuilder();
  gs.sp(closure,"delegate",builder);
  (closure.delegate!=undefined?gs.applyDelegate(closure,closure.delegate,[]):gs.executeCall(closure, gs.list([])));
  return gs.gp(builder,"html");
}

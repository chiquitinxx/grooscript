//This script needs grooscript.js to run
function HtmlBuilder() {
  var gSobject = gs.inherit(gs.baseClass,'HtmlBuilder');
  gSobject.clazz = { name: 'org.grooscript.builder.HtmlBuilder', simpleName: 'HtmlBuilder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.html = null;
  gSobject.tagSolver = function(name, args) {
    gSobject.html += "<" + (name) + "";
    if ((((gs.bool(args)) && (gs.mc(args,"size",[]) > 0)) && (!gs.instanceOf((args [ 0]), "String"))) && (!gs.instanceOf((args [ 0]), "Closure"))) {
      gs.mc(args [ 0],"each",[function(key, value) {
        return gSobject.html += " " + (key) + "='" + (value) + "'";
      }]);
    };
    gSobject.html += ">";
    if (gs.bool(args)) {
      if ((gs.equals(gs.mc(args,"size",[]), 1)) && (gs.instanceOf((args [ 0]), "String"))) {
        gs.mc(gSobject,"yield",[args [ 0]]);
      } else {
        var lastArg = gs.mc(args,"last",[]);
        if (gs.instanceOf(lastArg, "Closure")) {
          gs.sp(lastArg,"delegate",this);
          (lastArg.delegate!=undefined?gs.applyDelegate(lastArg,lastArg.delegate,[]):gs.executeCall(lastArg, []));
        };
        if ((gs.instanceOf(lastArg, "String")) && (gs.mc(args,"size",[]) > 1)) {
          gs.mc(gSobject,"yield",[lastArg]);
        };
      };
    };
    return gSobject.html += "</" + (name) + ">";
  };
  gSobject.build = function(x0) { return HtmlBuilder.build(x0); }
  gSobject['yield'] = function(text) {
    return gSobject.html += text;
  }
  gSobject['methodMissing'] = function(name, args) {
    gs.sp(this,"" + (name) + "",function(ars) {
      if (arguments.length == 1 && arguments[0] instanceof Array) { ars=gs.list(arguments[0]); } else 
      if (arguments.length == 1) { ars=gs.list([arguments[1 - 1]]); } else 
      if (arguments.length < 1) { ars=gs.list([]); } else 
      if (arguments.length > 1) {
        ars=gs.list([ars]);
        for (gScount=1;gScount < arguments.length; gScount++) {
          ars.add(arguments[gScount]);
        }
      }
      return gs.mc(gSobject,"tagSolver",[name, ars]);
    });
    return gs.mc(this,"invokeMethod",[name, args]);
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
  var mc = gs.expandoMetaClass(HtmlBuilder, false, true);
  gs.mc(mc,"initialize",[]);
  var builder = HtmlBuilder();
  gs.sp(builder,"metaClass",mc);
  gs.sp(closure,"delegate",builder);
  (closure.delegate!=undefined?gs.applyDelegate(closure,closure.delegate,[]):gs.executeCall(closure, []));
  return gs.gp(builder,"html");
}

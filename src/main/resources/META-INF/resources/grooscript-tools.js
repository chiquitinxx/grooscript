function HtmlBuilder() {
  var gSobject = gs.init('HtmlBuilder');
  gSobject.clazz = { name: 'org.grooscript.builder.HtmlBuilder', simpleName: 'HtmlBuilder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.tagSolver = function(name, args) {
    gs.mc(gSobject.htmCd,'leftShift', gs.list(["<" + (name) + ""]));
    if ((((gs.bool(args)) && (gs.mc(args,"size",[]) > 0)) && (!gs.bool(gs.instanceOf((args[0]), "String")))) && (!gs.bool(gs.instanceOf((args[0]), "Closure")))) {
      gs.mc(args[0],"each",[function(key, value) {
        return gs.mc(gSobject.htmCd,'leftShift', gs.list([" " + (key) + "='" + (value) + "'"]));
       }]);
     };
    gs.mc(gSobject.htmCd,'leftShift', gs.list([(!gs.bool(args) ? "/>" : ">")]));
    if (gs.bool(args)) {
      if ((gs.equals(gs.mc(args,"size",[]), 1)) && (gs.instanceOf((args[0]), "String"))) {
        gs.mc(gSobject,"yield",[args[0]]);
       } else {
        var lastArg = gs.mc(args,"last",[]);
        if (gs.instanceOf(lastArg, "Closure")) {
          gs.sp(lastArg,"delegate",this);
          gs.execCall(lastArg, this, []);
         };
        if ((gs.instanceOf(lastArg, "String")) && (gs.mc(args,"size",[]) > 1)) {
          gs.mc(gSobject,"yield",[lastArg]);
         };
       };
      return gs.mc(gSobject.htmCd,'leftShift', gs.list(["</" + (name) + ">"]));
     };
   };
  gSobject.htmCd = gs.stringBuffer();
  gSobject.build = function(x0) { return HtmlBuilder.build(x0); }
  gSobject['yield'] = function(text) {
    return gs.mc(text,"each",[function(ch) {
      var gSswitch0 = ch;
      if (gs.equals(gSswitch0, "&")) {
        gs.mc(gSobject.htmCd,'leftShift', gs.list(["&amp;"]));
        ;
       } else if (gs.equals(gSswitch0, "<")) {
        gs.mc(gSobject.htmCd,'leftShift', gs.list(["&lt;"]));
        ;
       } else if (gs.equals(gSswitch0, ">")) {
        gs.mc(gSobject.htmCd,'leftShift', gs.list(["&gt;"]));
        ;
       } else if (gs.equals(gSswitch0, "\"")) {
        gs.mc(gSobject.htmCd,'leftShift', gs.list(["&quot;"]));
        ;
       } else if (gs.equals(gSswitch0, "'")) {
        gs.mc(gSobject.htmCd,'leftShift', gs.list(["&apos;"]));
        ;
       } else {
        gs.mc(gSobject.htmCd,'leftShift', gs.list([ch]));
        ;
       };
     }]);
   }
  gSobject['yieldUnescaped'] = function(text) {
    return gs.mc(gSobject.htmCd,'leftShift', gs.list([text]));
   }
  gSobject['comment'] = function(text) {
    gs.mc(gSobject.htmCd,'leftShift', gs.list(["<!--"]));
    gs.mc(gSobject.htmCd,'leftShift', gs.list([text]));
    return gs.mc(gSobject.htmCd,'leftShift', gs.list(["-->"]));
   }
  gSobject['newLine'] = function(it) {
    return gs.mc(gSobject.htmCd,'leftShift', gs.list(["\n"]));
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
    return gs.mc(this,"invokeMethod",[name, args], gSobject);
   }
  gSobject['HtmlBuilder0'] = function(it) {
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
  gs.execCall(closure, this, []);
  return gs.mc(gs.gp(builder,"htmCd"),"toString",[]);
 }

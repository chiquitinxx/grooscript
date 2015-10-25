function HtmlBuilder() {
  var gSobject = gs.init('HtmlBuilder');
  gSobject.clazz = { name: 'org.grooscript.builder.HtmlBuilder', simpleName: 'HtmlBuilder'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.tagSolver = function(name, args) {
    gSobject.htmCd += "<" + (name) + "";
    if ((((gs.bool(args)) && (gs.mc(args,"size",[]) > 0)) && (!gs.bool(gs.instanceOf((args[0]), "String")))) && (!gs.bool(gs.instanceOf((args[0]), "Closure")))) {
      gs.mc(args[0],"each",[function(key, value) {
        return gSobject.htmCd += " " + (key) + "='" + (value) + "'";
      }]);
    };
    gSobject.htmCd += (!gs.bool(args) ? "/>" : ">");
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
      return gSobject.htmCd += "</" + (name) + ">";
    };
  };
  gSobject.htmCd = null;
  gSobject.build = function(x0) { return HtmlBuilder.build(x0); }
  gSobject['yield'] = function(text) {
    return gs.mc(text,"each",[function(ch) {
      var gSswitch0 = ch;
      if (gs.equals(gSswitch0, "&")) {
        gSobject.htmCd += "&amp;";
        ;
      } else if (gs.equals(gSswitch0, "<")) {
        gSobject.htmCd += "&lt;";
        ;
      } else if (gs.equals(gSswitch0, ">")) {
        gSobject.htmCd += "&gt;";
        ;
      } else if (gs.equals(gSswitch0, "\"")) {
        gSobject.htmCd += "&quot;";
        ;
      } else if (gs.equals(gSswitch0, "'")) {
        gSobject.htmCd += "&apos;";
        ;
      } else {
        gSobject.htmCd += ch;
        ;
      };
    }]);
  }
  gSobject['yieldUnescaped'] = function(text) {
    return gSobject.htmCd += text;
  }
  gSobject['comment'] = function(text) {
    return gSobject.htmCd += (gs.plus((gs.plus("<!--", text)), "-->"));
  }
  gSobject['newLine'] = function(it) {
    return gSobject.htmCd += "\n";
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
    gSobject.htmCd = "";
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
  return gs.gp(builder,"htmCd");
}

function Observable() {
  var gSobject = gs.init('Observable');
  gSobject.clazz = { name: 'org.grooscript.rx.Observable', simpleName: 'Observable'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.subscribers = gs.list([]);
  gSobject.sourceList = null;
  gSobject.chain = gs.list([]);
  gSobject.listen = function() { return Observable.listen(); }
  gSobject.from = function(x0) { return Observable.from(x0); }
  gSobject['produce'] = function(event) {
    return gs.mc(gSobject.subscribers,"each",[function(it) {
      return gs.mc(gSobject,"processFunction",[event, it]);
    }]);
  }
  gSobject['map'] = function(cl) {
    gs.mc(gSobject.chain,'leftShift', gs.list([cl]));
    return this;
  }
  gSobject['filter'] = function(cl) {
    gs.mc(gSobject.chain,'leftShift', gs.list([function(it) {
      if (gs.execCall(cl, this, [it])) {
        return it;
      } else {
        throw "Exception";
      };
    }]));
    return this;
  }
  gSobject['subscribe'] = function(cl) {
    while (gs.bool(gSobject.chain)) {
      cl = (gs.mc(cl,'leftShift', gs.list([gs.mc(gSobject.chain,"pop",[])])));
    };
    gs.mc(gSobject.subscribers,'leftShift', gs.list([cl]));
    if (gs.bool(gSobject.sourceList)) {
      return gs.mc(gSobject.sourceList,"each",[function(it) {
        return gs.mc(gSobject,"processFunction",[it, cl]);
      }]);
    };
  }
  gSobject['removeSubscribers'] = function(it) {
    return gSobject.subscribers = gs.list([]);
  }
  gSobject['processFunction'] = function(data, cl) {
    try {
      gs.execCall(cl, this, [data]);
    }
    catch (e) {
    }
    ;
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};
Observable.listen = function(it) {
  return Observable();
}
Observable.from = function(list) {
  return Observable(gs.map().add("sourceList",list));
}

function GQueryImpl() {
  var gSobject = gs.init('GQueryImpl');
  gSobject.clazz = { name: 'org.grooscript.jquery.GQueryImpl', simpleName: 'GQueryImpl'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.clazz.interfaces = [{ name: 'org.grooscript.jquery.GQuery', simpleName: 'GQuery'}];
  gSobject['bind'] = function(selector, target, nameProperty, closure) {
    if (closure === undefined) closure = null;
    return gs.mc(gs.execStatic(GQueryList,'of', this,[selector]),"bind",[target, nameProperty, closure]);
  }
  gSobject['existsId'] = function(id) {
    return gs.mc(gs.execStatic(GQueryList,'of', this,["#" + (id) + ""]),"hasResults",[]);
  }
  gSobject['existsName'] = function(name) {
    return gs.mc(gs.execStatic(GQueryList,'of', this,["[name='" + (name) + "']"]),"hasResults",[]);
  }
  gSobject['existsGroup'] = function(name) {
    return gs.mc(gs.execStatic(GQueryList,'of', this,["input:radio[name='" + (name) + "']"]),"hasResults",[]);
  }
  gSobject['onEvent'] = function(selector, nameEvent, func) {
    return gs.mc(gs.execStatic(GQueryList,'of', this,[selector]),"onEvent",[nameEvent, func]);
  }
  gSobject.doRemoteCall = function(url, type, params, onSuccess, onFailure, objectResult) {
    if (objectResult === undefined) objectResult = null;
    $.ajax({
            type: type, //GET or POST
            data: gs.toJavascript(params),
            url: url,
            dataType: 'text'
        }).done(function(newData) {
            if (onSuccess) {
                onSuccess(gs.toGroovy(jQuery.parseJSON(newData), objectResult));
            }
        })
        .fail(function(error) {
            if (onFailure) {
                onFailure(error);
            }
        });
  }
  gSobject.onReady = function(func) {
    $(document).ready(func);
  }
  gSobject['attachMethodsToDomEvents'] = function(obj) {
    return gs.mc(gs.gp((obj = gs.metaClass(obj)),"methods"),"each",[function(method) {
      if (gs.mc(gs.gp(method,"name"),"endsWith",["Click"])) {
        var shortName = gs.mc(gs.gp(method,"name"),"substring",[0, gs.minus(gs.mc(gs.gp(method,"name"),"length",[]), 5)]);
        if (gs.mc(gSobject,"existsId",[shortName])) {
          gs.mc(gSobject,"onEvent",[gs.plus("#", shortName), "click", obj["" + (gs.gp(method,"name")) + ""]]);
        };
      };
      if (gs.mc(gs.gp(method,"name"),"endsWith",["Submit"])) {
        var shortName = gs.mc(gs.gp(method,"name"),"substring",[0, gs.minus(gs.mc(gs.gp(method,"name"),"length",[]), 6)]);
        if (gs.mc(gSobject,"existsId",[shortName])) {
          gs.mc(gSobject,"onEvent",[gs.plus("#", shortName), "submit", gs.mc(obj["" + (gs.gp(method,"name")) + ""],'leftShift', gs.list([function(it) {
            return gs.mc(it,"preventDefault",[]);
          }]))]);
        };
      };
      if (gs.mc(gs.gp(method,"name"),"endsWith",["Change"])) {
        var shortName = gs.mc(gs.gp(method,"name"),"substring",[0, gs.minus(gs.mc(gs.gp(method,"name"),"length",[]), 6)]);
        if (gs.mc(gSobject,"existsId",[shortName])) {
          return gs.mc(gSobject,"onChange",[gs.plus("#", shortName), obj["" + (gs.gp(method,"name")) + ""]]);
        };
      };
    }]);
  }
  gSobject['onChange'] = function(selector, closure) {
    return gs.mc(gs.execStatic(GQueryList,'of', this,[selector]),"onChange",[closure]);
  }
  gSobject['focusEnd'] = function(selector) {
    return gs.mc(gs.execStatic(GQueryList,'of', this,[selector]),"focusEnd",[]);
  }
  gSobject['bindAllProperties'] = function(target) {
    return gs.mc(gs.gp(target,"properties"),"each",[function(name, value) {
      if (gs.mc(gSobject,"existsId",[name])) {
        gs.mc(gSobject,"bind",["#" + (name) + "", target, name]);
      };
      if (gs.mc(gSobject,"existsName",[name])) {
        gs.mc(gSobject,"bind",["[name='" + (name) + "']", target, name]);
      };
      if (gs.mc(gSobject,"existsGroup",[name])) {
        return gs.mc(gSobject,"bind",["input:radio[name='" + (name) + "']", target, name]);
      };
    }]);
  }
  gSobject['bindAll'] = function(target) {
    gs.mc(gSobject,"bindAllProperties",[target]);
    return gs.mc(gSobject,"attachMethodsToDomEvents",[target]);
  }
  gSobject['observeEvent'] = function(selector, nameEvent, data) {
    if (data === undefined) data = gs.map();
    var observable = gs.execStatic(Observable,'listen', this,[]);
    gs.mc(gs.execCall(this, this, [selector]),"on",[nameEvent, data, function(event) {
      return gs.mc(observable,"produce",[event]);
    }]);
    return observable;
  }
  gSobject['call'] = function(selector) {
    return gs.execStatic(GQueryList,'of', this,[selector]);
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};

function GQueryList() {
  var gSobject = gs.init('GQueryList');
  gSobject.clazz = { name: 'org.grooscript.jquery.GQueryList', simpleName: 'GQueryList'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.list = null;
  gSobject.selec = null;
  gSobject.of = function(x0) { return GQueryList.of(x0); }
  gSobject.methodMissing = function(name, args) {
    return gSobject.list[name].apply(gSobject.list, args);
  }
  gSobject.withResultList = function(cl) {
    if (gSobject.list.length) {
            cl(gSobject.list.toArray());
        }
        return gSobject;
  }
  gSobject.hasResults = function() {
    return gSobject.list.length > 0;
  }
  gSobject.onEvent = function(nameEvent, cl) {
    gSobject.list.on(nameEvent, cl);
        return gSobject;
  }
  gSobject.onChange = function(cl) {
    var jq = gSobject.list;

        if (jq.is(":text")) {
            jq.bind('input', function() {
                cl($(this).val());
            });
        } else if (jq.is('textarea')) {
            jq.bind('input propertychange', function() {
                cl($(this).val());
            });
        } else if (jq.is(":checkbox")) {
            jq.change(function() {
                cl($(this).is(':checked'));
            });
        } else if (jq.is(":radio")) {
            jq.change(function() {
                cl($(this).val());
            });
        } else if (jq.is("select")) {
            jq.bind('change', function() {
                cl($(this).val());
            });
        } else {
            console.log('Not supporting onChange for selector: ' + gSobject.selec);
        }
        return gSobject;
  }
  gSobject.focusEnd = function() {
    var jq = gSobject.list;

        if (jq.length) {
            if (jq.is(":text") || jq.is('textarea')) {
                var originalValue = jq.val();
                jq.val('');
                jq.blur().focus().val(originalValue);
            } else {
                jq.focus();
            }
        }
        return gSobject;
  }
  gSobject.bind = function(target, nameProperty, closure) {
    if (closure === undefined) closure = null;
    var jq = gSobject.list;
        //Create set method
        var nameSetMethod = 'set'+nameProperty.capitalize();

        if (jq.is(":text")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                jq.val(newValue);
                if (closure) { closure(newValue); };
            };
            jq.bind('input', function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (jq.is('textarea')) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                jq.val(newValue);
                if (closure) { closure(newValue); };
            };
            jq.bind('input propertychange', function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (jq.is(":checkbox")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                jq.prop('checked', newValue);
                if (closure) { closure(newValue); };
            };
            jq.change(function() {
                var currentVal = $(this).is(':checked');
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (jq.is(":radio")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                $(gSobject.selec +'[value="' + newValue + '"]').prop('checked', true);
                if (closure) { closure(newValue); };
            };
            jq.change(function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (jq.is("select")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                jq.val(newValue);
                if (closure) { closure(newValue); };
            };
            jq.bind('change', function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else {
            console.log('Not supporting bind for selector ' + gSobject.selec);
        }
        return gSobject;
  }
  gSobject.jqueryList = function(selec) {
    return $(selec);
  }
  gSobject['GQueryList1'] = function(selector) {
    gSobject.selec = selector;
    gSobject.list = gs.mc(gSobject,"jqueryList",[selector]);
    return this;
  }
  if (arguments.length==1) {gSobject.GQueryList1(arguments[0]); }
  
  return gSobject;
};
GQueryList.of = function(selector) {
  return GQueryList(selector);
}


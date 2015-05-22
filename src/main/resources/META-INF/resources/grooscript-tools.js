function HtmlBuilder() {
  var gSobject = gs.inherit(gs.baseClass,'HtmlBuilder');
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
  var gSobject = gs.inherit(gs.baseClass,'Observable');
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
      cl = (gs.mc(cl,'leftShift', gs.list([gs.mc(gSobject.chain,"remove",[gs.minus(gs.mc(gSobject.chain,"size",[]), 1)])])));
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
  var gSobject = gs.inherit(gs.baseClass,'GQueryImpl');
  gSobject.clazz = { name: 'org.grooscript.jquery.GQueryImpl', simpleName: 'GQueryImpl'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.clazz.interfaces = [{ name: 'org.grooscript.jquery.GQuery', simpleName: 'GQuery'}];
  gSobject.bind = function(selector, target, nameProperty, closure) {
    if (closure === undefined) closure = null;
    var sourceDom = $(selector);
        //Create set method
        var nameSetMethod = 'set'+nameProperty.capitalize();

        if (sourceDom.is(":text")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                sourceDom.val(newValue);
                if (closure) { closure(newValue); };
            };
            sourceDom.bind('input', function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (sourceDom.is('textarea')) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                sourceDom.val(newValue);
                if (closure) { closure(newValue); };
            };
            sourceDom.bind('input propertychange', function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (sourceDom.is(":checkbox")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                sourceDom.prop('checked', newValue);
                if (closure) { closure(newValue); };
            };
            sourceDom.change(function() {
                var currentVal = $(this).is(':checked');
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (sourceDom.is(":radio")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                $(selector +'[value="' + newValue + '"]').prop('checked', true);
                if (closure) { closure(newValue); };
            };
            sourceDom.change(function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else if (sourceDom.is("select")) {
            target[nameSetMethod] = function(newValue) {
                this[nameProperty] = newValue;
                sourceDom.val(newValue);
                if (closure) { closure(newValue); };
            };
            sourceDom.bind('change', function() {
                var currentVal = $(this).val();
                target[nameProperty] = currentVal;
                if (closure) { closure(currentVal); };
            });
        } else {
            console.log('Not supporting bind for selector ' + selector);
        }
  }
  gSobject.existsId = function(id) {
    return $("#" + id).length > 0
  }
  gSobject.existsName = function(name) {
    return $("[name='" + name + "']").length > 0
  }
  gSobject.existsGroup = function(name) {
    return $("input:radio[name='" + name + "']").length > 0
  }
  gSobject.onEvent = function(selector, nameEvent, func) {
    $(selector).on(nameEvent, func);
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
  gSobject.onChange = function(selector, closure) {
    var sourceDom = $(selector);

        if (sourceDom.is(":text")) {
            sourceDom.bind('input', function() {
                closure($(this).val());
            });
        } else if (sourceDom.is('textarea')) {
            sourceDom.bind('input propertychange', function() {
                closure($(this).val());
            });
        } else if (sourceDom.is(":checkbox")) {
            sourceDom.change(function() {
                closure($(this).is(':checked'));
            });
        } else if (sourceDom.is(":radio")) {
            sourceDom.change(function() {
                closure($(this).val());
            });
        } else if (sourceDom.is("select")) {
            sourceDom.bind('change', function() {
                closure($(this).val());
            });
        } else {
            console.log('Not supporting onChange for selector: ' + selector);
        }
  }
  gSobject.focusEnd = function(selector) {
    var sourceDom = $(selector);

        if (sourceDom) {
            if (sourceDom.is(":text") || sourceDom.is('textarea')) {
                var originalValue = sourceDom.val();
                sourceDom.val('');
                sourceDom.blur().focus().val(originalValue);
            } else {
                sourceDom.focus();
            }
        }
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
        return gs.mc(gSobject,"bind",["input:radio[name=" + (name) + "]", target, name]);
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
    return GQueryList(selector);
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};

function GQueryList() {
  var gSobject = gs.inherit(gs.baseClass,'GQueryList');
  gSobject.clazz = { name: 'org.grooscript.jquery.GQueryList', simpleName: 'GQueryList'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.list = null;
  gSobject.jqueryList = function(selector) {
    return $(selector);
  }
  gSobject.methodMissing = function(name, args) {
    return gSobject.list[name].apply(gSobject.list, args);
  }
  gSobject['GQueryList1'] = function(selector) {
    gSobject.list = gs.mc(gSobject,"jqueryList",[selector]);
    return this;
  }
  if (arguments.length==1) {gSobject.GQueryList1(arguments[0]); }
  
  return gSobject;
};


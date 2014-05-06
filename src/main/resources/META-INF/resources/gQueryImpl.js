//This script needs grooscript.js and jQuery to run
function GQueryImpl() {
  var gSobject = gs.inherit(gs.baseClass,'GQueryImpl');
  gSobject.clazz = { name: 'org.grooscript.jquery.GQueryImpl', simpleName: 'GQueryImpl'};
  gSobject.clazz.superclass = { name: 'java.lang.Object', simpleName: 'Object'};
  gSobject.clazz.interfaces = [{ name: 'org.grooscript.jquery.GQuery', simpleName: 'GQuery'}, ];
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
  gSobject.bindEvent = function(id, name, func) {
    $('#'+id).on(name, func);
  }
  gSobject.doRemoteCall = function(url, type, params, onSuccess, onFailure, objectResult) {
    if (objectResult === undefined) objectResult = null;
    $.ajax({
            type: type, //GET or POST
            data: gs.toJavascript(params),
            url: url,
            dataType: 'text'
        }).done(function(newData) {
            onSuccess(gs.toGroovy(jQuery.parseJSON(newData), objectResult));
        })
        .fail(function(error) {
            onFailure(error);
        });
  }
  gSobject.onReady = function(func) {
    $(document).ready(func);
  }
  gSobject.html = function(selector, text) {
    $(selector).text(text);
  }
  if (arguments.length == 1) {gs.passMapToObject(arguments[0],gSobject);};
  
  return gSobject;
};

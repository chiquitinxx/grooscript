package org.grooscript.jquery

import org.grooscript.asts.GsNative
import org.grooscript.rx.Observable

/**
 * Created by jorge on 15/02/14.
 */
class GQueryImpl implements GQuery {
    @GsNative
    def bind(String selector, target, String nameProperty, Closure closure = null) { /*

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
    */}

    @GsNative
    boolean existsId(String id) {/*
        return $("#" + id).length > 0
    */}

    @GsNative
    boolean existsName(String name) {/*
        return $("[name='" + name + "']").length > 0
    */}

    @GsNative
    boolean existsGroup(String name) {/*
        return $("input:radio[name='" + name + "']").length > 0
    */}

    @GsNative
    void onEvent(String selector, String nameEvent, Closure func) {/*
        $(selector).on(nameEvent, func);
    */}

    @GsNative
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure, objectResult = null) {/*
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
    */}

    @GsNative
    void onReady(Closure func) {/*
        $(document).ready(func);
    */}

    void attachMethodsToDomEvents(obj) {
        obj.metaClass.methods.each { method ->
            if (method.name.endsWith('Click')) {
                def shortName = method.name.substring(0, method.name.length() - 5)
                if (existsId(shortName)) {
                    onEvent('#'+shortName, 'click', obj.&"${method.name}")
                }
            }
            if (method.name.endsWith('Submit')) {
                def shortName = method.name.substring(0, method.name.length() - 6)
                if (existsId(shortName)) {
                    onEvent('#'+shortName, 'submit', obj.&"${method.name}" << { it.preventDefault() })
                }
            }
            if (method.name.endsWith('Change')) {
                def shortName = method.name.substring(0, method.name.length() - 6)
                if (existsId(shortName)) {
                    onChange('#'+shortName, obj.&"${method.name}")
                }
            }
        }
    }

    @GsNative
    void onChange(String selector, Closure closure) {/*
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
    */}

    @GsNative
    void focusEnd(String selector) {/*
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
    */}

    void bindAllProperties(target) {
        target.properties.each { name, value ->
            if (existsId(name)) {
                bind("#$name", target, name)
            }
            if (existsName(name)) {
                bind("[name='$name']", target, name)
            }
            if (existsGroup(name)) {
                bind("input:radio[name=${name}]", target, name)
            }
        }
    }

    void bindAll(target) {
        bindAllProperties(target)
        attachMethodsToDomEvents(target)
    }

    Observable observeEvent(String selector, String nameEvent, Map data = [:]) {
        def observable = Observable.listen()
        call(selector).on(nameEvent, data, { event ->
            observable.produce(event)
        })
        observable
    }

    GQueryList call(String selector) {
        new GQueryList(selector)
    }
}

class GQueryList {
    def list
    GQueryList(String selector) {
        list = jqueryList(selector)
    }

    @GsNative
    def jqueryList(String selector) {/*
        return $(selector);
    */}

    @GsNative
    def methodMissing(String name, args) {/*
        return gSobject.list[name].apply(gSobject.list, args);
    */}
}

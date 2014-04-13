package org.grooscript.jquery

import org.grooscript.asts.GsNative

/**
 * Created by jorge on 15/02/14.
 */
class JQueryImpl implements JQuery {
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
        return $('input:radio[name=' + name + ']').length > 0
    */}

    @GsNative
    void bindEvent(String id, String name, Closure func) {/*
        $('#'+id).on(name, func);
    */}
}

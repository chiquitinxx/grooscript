/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.jquery

import org.grooscript.asts.GsNative
import org.grooscript.rx.Observable

class GQueryImpl implements GQuery {

    GQueryList bind(String selector, target, String nameProperty, Closure closure = null) {
        GQueryList.of(selector).bind(target, nameProperty, closure)
    }

    GQueryList bindProperty(String selector, target, String nameProperty, GQueryList parent = null) {
        resolveSelector(selector, parent).bind(target, nameProperty)
    }

    boolean existsSelector(String selector, GQueryList parent = null) {
        resolveSelector(selector, parent).hasResults()
    }

    boolean existsId(String id, GQueryList parent = null) {
        resolveSelector("#${id}", parent).hasResults()
    }

    boolean existsName(String name, GQueryList parent = null) {
        resolveSelector("[name='${name}']", parent).hasResults()
    }

    boolean existsGroup(String name, GQueryList parent = null) {
        resolveSelector("input:radio[name='${name}']", parent).hasResults()
    }

    GQueryList onEvent(String selector, String nameEvent, Closure func, GQueryList parent = null) {
        resolveSelector(selector, parent).onEvent(nameEvent, func)
    }

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

    void attachMethodsToDomEvents(obj, GQueryList parent = null) {
        obj.metaClass.methods.each { method ->
            if (method.name.endsWith('Click')) {
                def shortName = method.name.substring(0, method.name.length() - 5)
                if (existsId(shortName, parent)) {
                    onEvent('#'+shortName, 'click', obj.&"${method.name}", parent)
                }
            }
            if (method.name.endsWith('Submit')) {
                def shortName = method.name.substring(0, method.name.length() - 6)
                if (existsId(shortName, parent)) {
                    onEvent('#'+shortName, 'submit', obj.&"${method.name}" << { it.preventDefault() }, parent)
                }
            }
            if (method.name.endsWith('Change')) {
                def shortName = method.name.substring(0, method.name.length() - 6)
                if (existsId(shortName, parent)) {
                    onChange('#'+shortName, obj.&"${method.name}", parent)
                }
            }
        }
    }

    GQueryList onChange(String selector, Closure closure, GQueryList parent = null) {
        resolveSelector(selector, parent).onChange closure
    }

    GQueryList focusEnd(String selector, GQueryList parent = null) {
        resolveSelector(selector, parent).focusEnd()
    }

    void bindAllProperties(target, GQueryList parent = null) {
        target.properties.each { String name, value ->
            if (existsId(name, parent)) {
                bindProperty("#$name", target, name, parent)
            }
            if (existsName(name, parent)) {
                bindProperty("[name='$name']", target, name, parent)
            }
            if (existsGroup(name, parent)) {
                bindProperty("input:radio[name='${name}']", target, name, parent)
            }
        }
    }

    void bindAll(target, GQueryList parent = null) {
        bindAllProperties(target, parent)
        attachMethodsToDomEvents(target, parent)
    }

    Observable observeEvent(String selector, String nameEvent, Map data = [:]) {
        def observable = Observable.listen()
        call(selector).on(nameEvent, data, { event ->
            observable.produce(event)
        })
        observable
    }

    GQueryList call(String selector) {
        GQueryList.of(selector)
    }

    private GQueryList resolveSelector(String selector, GQueryList parent) {
        parent != null ? parent.find(selector) : GQueryList.of(selector)
    }
}

class GQueryList {

    def list
    String selec

    static GQueryList of(String selector) {
        new GQueryList(selector)
    }

    GQueryList(String selector) {
        selec = selector
        list = jqueryList(selector)
    }

    @GsNative
    def methodMissing(String name, args) {/*
        return gSobject.list[name].apply(gSobject.list, args);
    */}

    @GsNative
    GQueryList withResultList(Closure cl) {/*
        if (gSobject.list.length) {
            cl(gSobject.list.toArray());
        }
        return gSobject;
    */}

    @GsNative
    boolean hasResults() {/*
        return gSobject.list.length > 0;
    */}

    @GsNative
    GQueryList onEvent(String nameEvent, Closure cl) {/*
        gSobject.list.on(nameEvent, cl);
        return gSobject;
    */}

    @GsNative
    GQueryList onChange(Closure cl) {/*
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
    */}

    @GsNative
    GQueryList focusEnd() {/*
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
    */}

    @GsNative
    GQueryList bind(target, String nameProperty, Closure closure = null) { /*
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
    */}

    @GsNative
    private jqueryList(String selec) {/*
        return $(selec);
    */}
}

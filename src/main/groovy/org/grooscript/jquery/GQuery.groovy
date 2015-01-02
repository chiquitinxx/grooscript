package org.grooscript.jquery

import org.grooscript.rx.Observable

interface GQuery {
    //Bind a selector to a property of an object
    def bind(String selector, target, String nameProperty)
    def bind(String selector, target, String nameProperty, Closure closure)
    //Exist dom element with id
    boolean existsId(String id)
    //Exist dom element with name
    boolean existsName(String name)
    //Exist dom radio group with that name
    boolean existsGroup(String name)
    //Launch closure on dom event, closure not receive params
    void onEvent(String selector, String nameEvent, Closure func)
    //Remote call to server to receive JSON
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure)
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure, objectResult)
    //Launch closure when page loaded
    void onReady(Closure func)
    //Atach methods of an object to dom elements with that id (Click, Submit, Change)
    void attachMethodsToDomEvents(target)
    //Launch closure on input change event, closure receive a param with the new value
    void onChange(String id, Closure closure)
    //Focus dom object, on input put cursor at the end
    void focusEnd(String selector)
    //Bind all properties of an object to dom elements, find dom elements by id or name
    void bindAllProperties(target)
    //Bind all
    void bindAll(target)
    //Returns an Observable from a event
    Observable observeEvent(String selector, String nameEvent)
    Observable observeEvent(String selector, String nameEvent, Map data)
    //Call
    GQueryList call(String selector)
}

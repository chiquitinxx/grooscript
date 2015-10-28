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
//tag::gquery[]
package org.grooscript.jquery

import org.grooscript.rx.Observable

interface GQuery {
    //Bind a selector to a property of an object
    GQueryList bind(String selector, target, String nameProperty)
    GQueryList bind(String selector, target, String nameProperty, Closure closure)
    GQueryList bindProperty(String selector, target, String nameProperty)
    GQueryList bindProperty(String selector, target, String nameProperty, GQueryList parent)
    //Exist selector
    boolean existsSelector(String selector)
    boolean existsSelector(String selector, GQueryList parent)
    //Exist dom element with id
    boolean existsId(String id)
    boolean existsId(String id, GQueryList parent)
    //Exist dom element with name
    boolean existsName(String name)
    boolean existsName(String name, GQueryList parent)
    //Exist dom radio group with that name
    boolean existsGroup(String name)
    boolean existsGroup(String name, GQueryList parent)
    //Launch closure on dom event, closure not receive params
    GQueryList onEvent(String selector, String nameEvent, Closure func)
    GQueryList onEvent(String selector, String nameEvent, Closure func, GQueryList parent)
    //Remote call to server to receive JSON
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure)
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure, objectResult)
    //Launch closure when page loaded
    void onReady(Closure func)
    //Atach methods of an object to dom elements with that id (Click, Submit, Change)
    void attachMethodsToDomEvents(target)
    void attachMethodsToDomEvents(target, GQueryList parent)
    //Launch closure on input change event, closure receive a param with the new value
    GQueryList onChange(String selector, Closure closure)
    GQueryList onChange(String selector, Closure closure, GQueryList parent)
    //Focus dom object, on input put cursor at the end
    GQueryList focusEnd(String selector)
    GQueryList focusEnd(String selector, GQueryList parent)
    //Bind all properties of an object to dom elements, find dom elements by id or name
    void bindAllProperties(target)
    void bindAllProperties(target, GQueryList parent)
    //Bind all
    void bindAll(target)
    void bindAll(target, GQueryList parent)
    //Returns an Observable from a event
    Observable observeEvent(String selector, String nameEvent)
    Observable observeEvent(String selector, String nameEvent, Map data)
    //Call
    GQueryList call(String selector)
}
//end::gquery[]

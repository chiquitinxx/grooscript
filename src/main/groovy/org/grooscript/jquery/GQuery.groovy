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
    //Exist selector
    boolean existsSelector(String selector)
    //Exist dom element with id
    boolean existsId(String id)
    boolean existsId(String id, String prefix)
    //Exist dom element with name
    boolean existsName(String name)
    boolean existsName(String name, String prefix)
    //Exist dom radio group with that name
    boolean existsGroup(String name)
    boolean existsGroup(String name, String prefix)
    //Launch closure on dom event, closure not receive params
    GQueryList onEvent(String selector, String nameEvent, Closure func)
    //Remote call to server to receive JSON
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure)
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure, objectResult)
    //Launch closure when page loaded
    void onReady(Closure func)
    //Atach methods of an object to dom elements with that id (Click, Submit, Change)
    void attachMethodsToDomEvents(target)
    void attachMethodsToDomEvents(target, String prefix)
    //Launch closure on input change event, closure receive a param with the new value
    GQueryList onChange(String selector, Closure closure)
    //Focus dom object, on input put cursor at the end
    GQueryList focusEnd(String selector)
    //Bind all properties of an object to dom elements, find dom elements by id or name
    void bindAllProperties(target)
    void bindAllProperties(target, String prefix)
    //Bind all
    void bindAll(target)
    void bindAll(target, String prefix)
    //Returns an Observable from a event
    Observable observeEvent(String selector, String nameEvent)
    Observable observeEvent(String selector, String nameEvent, Map data)
    //Call
    GQueryList call(String selector)
}
//end::gquery[]

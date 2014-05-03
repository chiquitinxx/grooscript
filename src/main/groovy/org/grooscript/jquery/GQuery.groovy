package org.grooscript.jquery

/**
 * Created by jorge on 13/04/14.
 */
interface GQuery {
    def bind(String selector, target, String nameProperty)
    def bind(String selector, target, String nameProperty, Closure closure)
    boolean existsId(String id)
    boolean existsName(String name)
    boolean existsGroup(String name)
    void bindEvent(String id, String name, Closure func)
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure)
    void doRemoteCall(String url, String type, params, Closure onSuccess, Closure onFailure, objectResult)
}

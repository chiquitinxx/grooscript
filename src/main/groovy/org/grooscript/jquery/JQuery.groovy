package org.grooscript.jquery

/**
 * Created by jorge on 13/04/14.
 */
interface JQuery {
    def bind(String selector, target, String nameProperty)
    def bind(String selector, target, String nameProperty, Closure closure)
    boolean existsId(String id)
    boolean existsName(String name)
    boolean existsGroup(String name)
    void bindEvent(String id, String name, Closure func)
}

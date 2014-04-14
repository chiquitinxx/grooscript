package org.grooscript.jquery

/**
 * User: jorgefrancoleza
 * Date: 06/02/14
 */
class Binder {

    GQuery gQuery

    def bindAllProperties(target, closure = null) {
        target.properties.each { name, value ->
            if (gQuery.existsId(name)) {
                gQuery.bind("#$name", target, name, closure)
            }
            if (gQuery.existsName(name)) {
                gQuery.bind("[name='$name']", target, name, closure)
            }
            if (gQuery.existsGroup(name)) {
                gQuery.bind("input:radio[name=${name}]", target, name, closure)
            }
        }
    }

    def bindAllMethods(target) {
        target.metaClass.methods.each { method ->
            if (method.name.endsWith('Click')) {
                def shortName = method.name.substring(0, method.name.length() - 5)
                if (gQuery.existsId(shortName)) {
                    gQuery.bindEvent(shortName, 'click', target.&"${method.name}")
                }
            }
        }
    }

    def call(target, closure = null) {
        bindAllProperties(target, closure)
        bindAllMethods(target)
    }
}

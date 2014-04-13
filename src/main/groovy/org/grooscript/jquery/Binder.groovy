package org.grooscript.jquery

/**
 * User: jorgefrancoleza
 * Date: 06/02/14
 */
class Binder {

    JQuery jQuery

    def bindAllProperties(target, closure = null) {
        target.properties.each { name, value ->
            if (jQuery.existsId(name)) {
                jQuery.bind("#$name", target, name, closure)
            }
            if (jQuery.existsName(name)) {
                jQuery.bind("[name='$name']", target, name, closure)
            }
            if (jQuery.existsGroup(name)) {
                jQuery.bind("input:radio[name=${name}]", target, name, closure)
            }
        }
    }

    def bindAllMethods(target) {
        target.metaClass.methods.each { method ->
            if (method.name.endsWith('Click')) {
                def shortName = method.name.substring(0, method.name.length() - 5)
                if (jQuery.existsId(shortName)) {
                    jQuery.bindEvent(shortName, 'click', target.&"${method.name}")
                }
            }
        }
    }

    def call(target, closure = null) {
        bindAllProperties(target, closure)
        bindAllMethods(target)
    }
}

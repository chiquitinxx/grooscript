package org.grooscript.templates

class Templates {

    static Map<String, Closure> templates

    static String applyTemplate(String name, model = [:]) {
        Closure cl = templates[name]
        cl.delegate = model
        cl(model)
    }
}

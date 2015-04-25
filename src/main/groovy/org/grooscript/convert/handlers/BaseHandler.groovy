package org.grooscript.convert.handlers

import org.grooscript.convert.Context
import org.grooscript.convert.ConversionFactory
import org.grooscript.convert.Functions
import org.grooscript.convert.Out
import org.grooscript.convert.Traits

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class BaseHandler {

    static final List<Map> SPECIAL_STATIC_METHOD_CALLS = [
            [type: 'org.grooscript.GrooScript', method: 'toJavascript', function: GS_TO_JAVASCRIPT],
            [type: 'org.grooscript.GrooScript', method: 'toGroovy', function: GS_TO_GROOVY],
            [type: 'org.grooscript.GrooScript', method: 'toJsObj', function: GS_TO_JS_OBJECT],
            [type: 'java.lang.Integer', method: 'parseInt', function: 'parseInt'],
            [type: 'java.lang.Float', method: 'parseFloat', function: 'parseFloat'],
    ]

    Context context
    Out out
    ConversionFactory conversionFactory
    Functions functions
    Traits traits
}

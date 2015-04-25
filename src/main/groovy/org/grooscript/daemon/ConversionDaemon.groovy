package org.grooscript.daemon

import org.grooscript.GrooScript
import org.grooscript.util.GsConsole
import org.grooscript.util.Util

/**
 * User: jorgefrancoleza
 * Date: 21/02/13
 */
class ConversionDaemon {

    static numberConversions = 0

    static Closure conversionClosure = { List<String> source, String destination,
                                         Map conversionOptions, List<String> filesChanged ->
        List<String> filesToConvert = filesChanged.findAll {
            it.endsWith(Util.GROOVY_EXTENSION) || it.endsWith(Util.JAVA_EXTENSION)
        }
        if (filesToConvert) {
            GrooScript.clearAllOptions()
            conversionOptions.each { String key, value ->
                GrooScript.setConversionProperty(key, value)
            }
            if (destination.endsWith(Util.JS_EXTENSION)) {
                GrooScript.convert(source, destination)
            } else {
                GrooScript.convert(filesToConvert, destination)
            }
            GsConsole.info "[${numberConversions++}] Conversion daemon has converted files."
        }
    }

    static FilesDaemon start(List<String> source, String destination, Map conversionOptions) {
        def filesDaemon = new FilesDaemon(source,
                newAction(source, destination, conversionOptions),
                [actionOnStartup: true, recursive: conversionOptions.recursive ?: false])
        filesDaemon.start()
        filesDaemon
    }

    private static Closure newAction(List<String> source, String destination, Map conversionOptions) {
        conversionClosure.curry(source, destination, conversionOptions)
    }
}

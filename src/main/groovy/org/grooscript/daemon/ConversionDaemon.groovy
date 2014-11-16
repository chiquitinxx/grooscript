package org.grooscript.daemon

import org.grooscript.GrooScript
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 21/02/13
 */
class ConversionDaemon {

    static numberConversions = 0

    static conversionClosure = { source, String destination, conversionOptions, filesChanged ->
        if (filesChanged) {
            GrooScript.clearAllOptions()
            conversionOptions.each { key, value ->
                GrooScript.setConversionProperty(key, value)
            }
            if (destination.endsWith('.js')) {
                GrooScript.convert(source, destination)
            } else {
                GrooScript.convert(filesChanged, destination)
            }
            GsConsole.info "[${numberConversions++}] Conversion daemon has converted files."
        }
    }

    static Closure newAction(List<String> source, String destination, Map conversionOptions) {
        conversionClosure.curry(source, destination, conversionOptions)
    }

    static FilesDaemon start(List<String> source, String destination, Map conversionOptions) {
        def filesDaemon = new FilesDaemon(source,
                newAction(source, destination, conversionOptions),
                [actionOnStartup: true, recursive: conversionOptions.recursive ?: false])
        filesDaemon.start()
        filesDaemon
    }
}

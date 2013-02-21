package features

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 22/02/13
 */
//For test conversion daemon
//destination folder (js) must exist

GrooScript.startConversionDaemon(['.'],'js',null,{ list -> list.each { println 'Converted file -> '+it}})


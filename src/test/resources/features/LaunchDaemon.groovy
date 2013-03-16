package features

import org.grooscript.GrooScript

/**
 * User: jorgefrancoleza
 * Date: 22/02/13
 */
//For test conversion daemon

//Create a directory to save js files
final DIR = 'js'
new File(DIR).mkdir()

//Start the conversion daemon
GrooScript.startConversionDaemon(['ZeroOne.groovy'],DIR,null,{ list -> list.each { println 'Converted file -> '+it}})

sleep(60000)

//Stop conversion daemon
GrooScript.stopConversionDaemon()

//Remove directory
new File(DIR).deleteDir()
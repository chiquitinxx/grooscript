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
def daemon = GrooScript.startConversionDaemon(['ZeroOne.groovy'],DIR,null,{ list -> list.each { println 'Converted file -> '+it}})

sleep(3000)

//Stop conversion daemon
daemon.stop()

//Remove directory
new File(DIR).deleteDir()
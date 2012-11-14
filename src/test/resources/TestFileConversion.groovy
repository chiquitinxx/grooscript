import org.grooscript.GrooScript
/**
 * JFL 14/11/12
 */
new File('newDir').mkdir()

GrooScript.convert('staticRealm.groovy','newDir')

GrooScript.convert('.','newDir')

new File('newDir').deleteDir()
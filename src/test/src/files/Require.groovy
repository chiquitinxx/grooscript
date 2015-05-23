package files

import org.grooscript.asts.RequireJsModule

/**
 * User: jorgefrancoleza
 * Date: 23/05/15
 */
class Require {
    @RequireJsModule(path = 'lib/data')
    def data
    def nombre
}

package files

import org.grooscript.asts.RequireJsModule

class Require {
    @RequireJsModule(path = 'lib/data')
    def data
}

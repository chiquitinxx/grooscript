package features

import org.grooscript.asts.GsNotConvert
import org.grooscript.asts.GsNative

/**
 * JFL 19/11/12
 */

@GsNotConvert
class Hide {
    def secret
}

class Master {
    @GsNotConvert
    def secretAction() {
        println 'Super Secret stuff'
    }

    @GsNative
    def alert(message) {/*
        alert(message);
    */}
}





package depends

import org.grooscript.asts.GsNative

/**
 * User: jorgefrancoleza
 * Date: 26/04/14
 */
class WithGsNative {
    @GsNative
    def alert(String message) {/*
        alert(message);
    */}

    def callAlert() {
        alert('ALERT!')
    }

    def newAlert() {
        alert('NEW!')
    }
}

package files

import depends.WithGsNative
/**
 * User: jorgefrancoleza
 * Date: 26/04/14
 */
class UseGsNative {
    WithGsNative withGsNative

    def start() {
        withGsNative.callAlert()
    }
}

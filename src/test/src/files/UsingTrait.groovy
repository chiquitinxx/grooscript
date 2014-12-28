package files

/**
 * User: jorgefrancoleza
 * Date: 26/04/14
 */
class UsingTrait implements MyTrait {
    def value
    UsingTrait() {
        value = 0
        name = 'UsingTrait'
    }
    def bye() { 'Bye!' }
}

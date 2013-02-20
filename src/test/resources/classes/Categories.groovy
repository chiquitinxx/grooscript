package classes

/**
 * User: jorgefrancoleza
 * Date: 04/02/13
 */

class Speak {
    static String shout(String text) {  // Method argument is String, so we can add shout() to String object.
        text.toUpperCase() + '!'
    }

    static String salute(String text,String who) {  // Method argument is String, so we can add shout() to String object.
       "${who}: ${text}"
    }

}

use (Speak) {
    //println '->'+"Pay attention".shout()
    assert 'PAY ATTENTION!' == "Pay attention".shout()
    assert 'Mario: Hello' == "Hello".salute('Mario')
}

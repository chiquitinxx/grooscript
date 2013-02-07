package classes

/**
 * User: jorgefrancoleza
 * Date: 04/02/13
 */

class Speak {
    static String shout(String text) {  // Method argument is String, so we can add shout() to String object.
        text.toUpperCase() + '!'
    }

}

use (Speak) {
    //println '->'+"Pay attention".shout()
    assert 'PAY ATTENTION!' == "Pay attention".shout()
}

/*
// Or we can use the @Category annotation.
@Category(String)
class StreetTalk {
    String hiphop() {
        "Yo, yo, here we go. ${this}"
    }
    String xTimes(number) {
        this * number
    }
}


use(StreetTalk) {
    assert 'Yo, yo, here we go. Groovy is fun!' == 'Groovy is fun!'.hiphop()
    assert 'Groovy is fun!Groovy is fun!' == 'Groovy is fun!'.xTimes(2)
}
*/
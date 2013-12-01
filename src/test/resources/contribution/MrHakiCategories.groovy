package contribution

/**
 * User: jorgefrancoleza
 * Date: 01/12/13
 */

@Category(String)
class StreetTalk {
    String hiphop() {
        "Yo, yo, here we go. ${this}"
    }
}

use(StreetTalk) {
    assert 'Yo, yo, here we go. Groovy is fun!' == 'Groovy is fun!'.hiphop()
}

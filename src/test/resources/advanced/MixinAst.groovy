package advanced

/**
 * User: jorgefrancoleza
 * Date: 30/08/13
 */

class Pirate {
    def yarrr() {
        return "$name: YARRRRR!"
    }
}

@Mixin([Pirate])
class Human {
    def name
}

class Boy {
    def name
}

Boy.mixin(Pirate)

assert new Boy(name: 'Jorge').yarrr() == 'Jorge: YARRRRR!'
assert new Human(name: 'Jorge').yarrr() == 'Jorge: YARRRRR!'

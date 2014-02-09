package classes

/**
 * User: jorgefrancoleza
 * Date: 09/02/14
 */

def contacts = [] as Set
contacts << 'hello' << 'hello'

assert contacts.size() == 1

public interface Game {
    boolean start()
}

def game = [
        start : { -> println 'Game started.'; true }
] as Game

def beginGame = { Game newGame ->
    newGame.start()
}

assert beginGame(game) == true

String[] s = ["a", "b"] as String[]
assert s == ['a', 'b']

assert "${s[0]}${s[1]}c" as String == 'abc'




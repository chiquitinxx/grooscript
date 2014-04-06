package traits

/**
 * User: jorgefrancoleza
 * Date: 06/04/14
 */

trait GoogleAbility {
    abstract String toSearch()
    String google() { "Searching ${toSearch()}..." }
}

class Searcher implements GoogleAbility {
    String toSearch() { 'groovy' }
}

def searcher = new Searcher()
assert searcher.google() == 'Searching groovy...'

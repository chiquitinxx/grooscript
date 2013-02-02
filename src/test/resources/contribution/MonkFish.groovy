package contribution

import org.grooscript.asts.GsNotConvert

/**
 * User: jorgefrancoleza
 * Date: 02/02/13
 */

def map = [one:1,two:2]
map.remove('one')

assert map.size() == 1

class Mf1 {
    def value = 0
    @GsNotConvert
    def two() {
        value = 2
    }
    def twoTwo() {
        two() * two()
    }
}
//In the test we add code to js file as if two() exist
assert new Mf1().twoTwo() == 4

class Mf2 {
    def one = 1
    def two = 2
    def list = ['one','two']

    def giveMe(name) {
        def result
        def item = name
        list.each { it ->
            if (!result) {
                result = this."${item}"
            }
        }
        return result
    }
}

assert new Mf2().giveMe('two') == 2

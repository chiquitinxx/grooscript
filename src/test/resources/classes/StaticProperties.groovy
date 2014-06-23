package classes

/**
 * User: jorgefrancoleza
 * Date: 23/06/14
 */

class StaticData {
    static data = 'grooscript'

    static sayData() {
        data
    }

    def getData() {
        data + sayData()
    }
}

assert StaticData.data == 'grooscript'
assert StaticData.sayData() == 'grooscript'
assert new StaticData().data == 'grooscriptgrooscript'
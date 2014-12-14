package contribution

/**
 * Created by jorgefrancoleza on 14/12/14.
 */
def done = false
def check = { map ->
    [
            tastes: { like ->
                assert map.that == 'wine'
                assert like == 'good'
                done = true
            }
    ]
}

def wine = 'wine'
def good = 'good'

check that: wine tastes good

assert done
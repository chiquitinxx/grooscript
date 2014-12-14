package contribution

/**
 * Created by jorgefrancoleza on 14/12/14.
 */

void unless(boolean b, Closure c) {
    if (!b) c()
}

def (speed, limit) = [80, 90]
def tooFast = false

unless (speed > limit) {
    tooFast = true
}

assert tooFast

import java.util.ArrayList as L

def list = new L()
assert list == []

import static java.lang.Integer.parseInt as parse
import static java.lang.Float.parseFloat as parseF

assert 5 == parse('5')
assert 5.45 as float == parseF('5.45')
package baseScript

import groovy.transform.BaseScript

/**
 * Created by jorge on 27/07/14.
 */

def hello = {
    'hello'
}

class CustomScript extends Script {

    def value = 42

    int getTheMeaningOfLife() { value }

    def run() {
        value = 12
    }
}

@BaseScript
CustomScript baseScript

assert baseScript == this
assert theMeaningOfLife == 42
assert theMeaningOfLife == baseScript.theMeaningOfLife
assert hello() == 'hello'
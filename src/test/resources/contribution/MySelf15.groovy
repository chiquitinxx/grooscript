package contribution

/**
 * Created by jorge on 17/2/15.
 */

def list = [
        [number: 1, name: 'one'],
        [number: 2, name: 'two'],
        [number: 3, name: 'three'],
        [number: 4, name: 'four'],
        [number: 5, name: 'five'],
]

assert ['one', 'two', 'three', 'four', 'five'] == list*.name
assert ['four', 'five'] == list.findAll { it.number > 3 }*.name
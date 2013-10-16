package advanced

/**
 * User: jorgefrancoleza
 * Date: 16/10/13
 */

def range = 'a'..'p'

assert range.size() == 16
assert ('a'..'p').contains('d')
assert ('P'..'A').size() == 16
assert ('P'..'A').contains('D')
assert !('P'..'A').contains('d')

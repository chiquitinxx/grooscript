package contribution

/**
 * Created by jorge on 15/11/14.
 */
def list = ['one', 'two', 'three', 'four']
assert list.remove(1) == 'two'
assert list.size() == 3

assert list.remove('four') == true
assert list.remove('five') == false

/**
 * JFL 27/10/12
 */

enum Days {
    monday, tuesday, wednesday, thursday, friday, saturday, sunday

    static tellMeOne() {
        'one'
    }
}

def a = Days.monday

assert a == Days.monday
assert 'one' == Days.tellMeOne()

/**
 * JFL 27/10/12
 */

enum Days {
    monday, tuesday, wednesday, thursday, friday, saturday, sunday

    static tellMeOne() {
        'one'
    }
}

Days a = Days.monday

assert a == Days.monday
assert 'one' == Days.tellMeOne()
assert Days.monday.name() == 'monday'
assert a.name() == 'monday'
assert a.ordinal() == 0
assert Days.sunday.ordinal() == 6
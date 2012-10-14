package contribution

/**
 * MrHaki Code
 */

// Traditional "sum of the values in a list" sample.
// First with each() and side effect, because we have
// to declare a variable to hold the result:
def total = 0
(1..4).each { total += it }
assert 10  == total

// With the inject method we 'inject' the
// first value of the result, and then for
// each item the result is increased and
// returned for the next iteration.
def sum = (1..4).inject(0) { result, i -> result + i }
assert 10 == sum

// We add a println statement to see what happens.
(1..4).inject(0) { result, i ->
    println "$result + $i = ${result + i}"
    result + i
}
// Output:
// 0 + 1 = 1
// 1 + 2 = 3
// 3 + 3 = 6
// 6 + 4 = 10


class Person {
    String username
    String email
}
def persons = [
        new Person(username:'mrhaki', email: 'email@host.com'),
        new Person(username:'hubert', email: 'other@host.com')
]

// Convert list to a map where the key is the value of
// username property of Person and the value is the email
// property of Person. We inject an empty map as the starting
// point for the result.
def map = persons.inject([:]) { result, person ->
    result[person.username] = person.email
    result
}
assert [mrhaki: 'email@host.com', hubert: 'other@host.com'].toString() == map.toString()
package contribution

/**
 * Created by jorge on 04/12/14.
 */

def groups =  [1,23,10].groupBy{(it % 2) == 0}
assert groups[true] == [10]
assert groups[false] == [1, 23]

assert [].groupBy { true } == [:]
def langs =  ['Groovy', 'Grooscript', 'Java'].groupBy {
    it.toLowerCase().contains('groo') ? 'grooviers': 'non-grooviers'
}
assert langs['grooviers'] == ['Groovy', 'Grooscript']
assert langs['non-grooviers'] == ['Java']

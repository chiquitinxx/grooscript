package contribution

/**
 * Created by jorgefrancoleza on 5/8/15.
 */

def list = ['Gr8Conf2015US', 'Groovy', 'Grails', 'Gradle']

assert list.first() == 'Gr8Conf2015US'
assert list.last() == 'Gradle'
assert list.head() == 'Gr8Conf2015US'
assert list.tail() == ['Groovy', 'Grails', 'Gradle']

assert list.init() == ['Gr8Conf2015US', 'Groovy', 'Grails']

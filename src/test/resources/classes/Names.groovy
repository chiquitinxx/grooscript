package classes

/**
 * JFL 25/11/12
 */

class One {

}

class Two extends One {

}

assert new One().class.name == 'classes.One'

assert new One().class.simpleName == 'One'
assert new One().class.superclass.name == 'java.lang.Object'
assert new One().class.superclass.simpleName == 'Object'

assert new Two().class.superclass.name == 'classes.One'

/*
assert "hello".class.name == 'java.lang.String'
assert 3.class.name == 'java.lang.Integer'
assert (3.02).class.name == 'java.math.BigDecimal'

assert [].class.name == 'java.util.ArrayList'
def map = [hello:5]
assert map.hello.class.name == 'java.lang.Integer'
*/

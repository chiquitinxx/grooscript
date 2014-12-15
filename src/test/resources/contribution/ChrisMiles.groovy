package contribution

/**
 * Created by jorgefrancoleza on 10/12/14.
 */
// tag::class_info[]
class A { def a }
class B extends A {}
def a = new A()
def b = new B()

assert a.class == a.getClass()
assert a.metaClass == a.getMetaClass()
assert a.getClass().getName() == 'contribution.A'

assert a.getClass().getSimpleName() == 'A'
assert b instanceof A
assert b instanceof contribution.B
assert Class.forName('contribution.B') == B
assert A.newInstance()
// end::class_info[]
assert a.class.name == a.class.getName()
assert a.class.simpleName == a.class.getSimpleName()
assert new B() instanceof A
assert b instanceof B
assert b instanceof contribution.B
assert A.newInstance()
assert Class.forName('contribution.A') == A

//NOT SUPPORTED
//assert b.class.superclass == A
//assert b.class.getSuperclass() == A
//assert a.class.newInstance()
//assert a.getClass().newInstance()

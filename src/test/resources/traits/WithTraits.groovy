package traits

/**
 * User: jorgefrancoleza
 * Date: 28/04/14
 */

trait A { String methodFromA() { 'A' } }
trait B { String methodFromB() { 'B' } }

class C {}
def c = new C()

def d = c.withTraits A, B
assert d.methodFromA() == 'A'
assert d.methodFromB() == 'B'

def e = new C().withTraits A
assert e.methodFromA() == 'A'
try {
    e.methodFromB()
    assert false, 'Must fail!'
} catch (ex) {
    assert true
}
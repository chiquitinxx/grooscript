package traits

/**
 * Created by jorge on 16/04/14.
 */
trait NewNamed {
    String name
    int number = 5
    void setName(newName) {
        println 'new name:'+newName
        name = newName
    }
    int getNumber() {
        println 'Getting number with: '+number
        number
    }
}
class NewPerson implements NewNamed {}
def p = new NewPerson(name: 'Bob')
assert p.name == 'Bob'
assert p.getName() == 'Bob'

def p2 = new NewPerson(name: 'Foo')
assert p2.name == 'Foo'
assert p.name == 'Bob'
assert p.number == 5
p.number = 7
assert p.number == 7
assert p2.number == 5

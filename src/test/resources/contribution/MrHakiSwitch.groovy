package contribution

/**
 * Created by jorgefrancoleza on 4/8/15.
 */

class MyUser {
    String name

    def 'switch'() {
        name.reverse()
    }
}

def u = new MyUser(name: 'mrhaki')

assert u.switch() == 'ikahrm'

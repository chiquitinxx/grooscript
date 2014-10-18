package contribution

/**
 * User: jorgefrancoleza
 * Date: 18/10/14
 */

trait Name {
    String salutation, firstName, lastName

    String getFullName() {
        [salutation, firstName, lastName].join(' ')
    }
}

class User {
    String username, password
}

def user = new User() as Name
user.with {
    salutation = 'Mr.'
    firstName = 'Hubert'
    lastName = 'Klein Ikkink'
    username = 'mrhaki'
    password = '*****'
}

assert user.fullName == 'Mr. Hubert Klein Ikkink'
assert user.username == 'mrhaki'

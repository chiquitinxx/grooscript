package contribution

/**
 * Created by jorgefrancoleza on 14/12/14.
 */
// tag::customize[]
class Account {
    String name
    boolean disabled = false

    boolean asBoolean() { !disabled }
}

assert new Account(name: 'current')
assert !new Account(name: 'old', disabled: true)
// end::customize[]
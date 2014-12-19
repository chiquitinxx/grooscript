package contribution

/**
 * User: jorgefrancoleza
 * Date: 18/10/14
 */

trait Id {
    Long id
}

trait Version {
    Long version = 0L
}

trait Active {
    Date from = new Date()
    Date to = null

    boolean isActive() {
        final Date now = new Date()
        from < now && (!to || to > now)
    }
}

class MPerson {
    String username
}

def person = new MPerson(username: 'mrhaki')
def domainPerson = person.withTraits Id, Version, Active

domainPerson.id = 1

assert domainPerson.username == 'mrhaki'
assert domainPerson.id == 1
assert domainPerson.version == 0
assert domainPerson.active

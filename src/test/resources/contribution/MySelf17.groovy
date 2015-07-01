package contribution

/**
 * Created by jorgefrancoleza on 1/7/15.
 */
class Validator {
    boolean completed(String ... data) {
        data.every {
            it?.trim()
        }
    }
}

class Candidate {

    String name
    String email

    Validator validator

    boolean isValid() {
        validator.completed(name, email)
    }
}

def candidate = new Candidate(validator: new Validator())
assert !candidate.valid
assert !candidate.isValid()
candidate = new Candidate(validator: new Validator(), name: 'name')
assert !candidate.valid
assert !candidate.isValid()

assert new Candidate(validator: new Validator(), name: 'name', email: 'email').valid
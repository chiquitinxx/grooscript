/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package contribution

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
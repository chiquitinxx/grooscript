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

// Simple Java Bean.
public class Simple {
    private String text;

    public Simple() {
        super();
    }

    public String getMessage() {
        return "GET " + text;
    }

    public void setMessage(final String text) {
        this.text = "SET " + text
    }
}

def s1 = new Simple()
s1.setMessage('Old style')
assert 'GET SET Old style' == s1.getMessage()
s1.setMessage 'A bit more Groovy'  // No parenthesis.
assert 'GET SET A bit more Groovy' == s1.getMessage()

def s2 = new Simple(message: 'Groovy constructor')  // Named parameter in constructor.
assert 'GET SET Groovy constructor' == s2.getMessage()

def s3 = new Simple()
s3.message = 'Groovy style'  // = assigment for property.
assert 'GET SET Groovy style' == s3.message  // get value with . notation.
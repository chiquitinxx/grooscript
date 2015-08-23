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
package asts

import org.grooscript.asts.GsNative

class Foo {
    def a = 0
    public void methodA(){
        println 'methodA'
        a++
    }

    // tag::mixNative[]
    @GsNative
    def methodB(){/*
            gs.println('methodB');
            this.a++;
            this.methodA();*/
        println 'inMethodB'; a = a + 1; this.methodA()
    }
    // end::mixNative[]

    @GsNative
    static bar() {/*
        return 'bar';
    */ println 'inStatic'; 'bar'}
}

def foo = new Foo()
foo.methodB()

assert foo.a == 2
assert Foo.bar() == 'bar'
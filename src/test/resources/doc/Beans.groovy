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
package doc

// tag::mark[]
class MyBean {
    def a
    def b

    def getA() { 5 }

    def getC() { 6 }
}

def myBean = new MyBean(a: 3, b: 4)
assert myBean.a == 5
assert myBean.@a == 3
assert myBean.b == 4
assert myBean.c == 6
// end::mark[]
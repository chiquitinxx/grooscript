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
package advanced

// tag::mark[]
clever = new Expando()

clever.name = 'Groovy'
clever."$clever.name" = { 'YES'}

assert clever.Groovy() == 'YES'
clever."name" = 'Groovy 2.0'
assert clever.name ==  'Groovy 2.0'

def anotherExpando = new Expando(one:1,two:2)

assert anotherExpando.one == 1
assert anotherExpando.two == 2
// end::mark[]

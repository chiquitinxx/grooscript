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

a = 5
def f = {
    return a
}
assert f() == 5

class HtlmDsl {

    def text

    def build(Closure closure) {
        text = ''
        closure.delegate = this
        closure()
        return text
    }

    def methodMissing(String name,args) {
        text+="<${name}"
        if (args[0] instanceof HashMap) {
            args[0].each {
                text+=" ${it.key}='${it.value}'"
            }
        }
        text+=">"
        if (args[args.size()-1] instanceof Closure) {
            def clo = args[args.size()-1]
            clo.delegate = this
            clo()
        }
        text+="</${name}>"
    }
}

def builder = new HtlmDsl()

def putDiv = false

def result = builder.build {
    def five = 5
    html {
        if (putDiv) {
            div (id:'hola') {

            }
        }
        span (id:'adios',number:five)
    }
}

assert result == '<html><span id=\'adios\' number=\'5\'></span></html>'

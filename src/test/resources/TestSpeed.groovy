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

class MultiMethods {

    def hits = 0

    MultiMethods() {
        def mc = new ExpandoMetaClass(MultiMethods, false, true)
        mc.initialize()
        this.metaClass = mc
    }

    def methodMissing(String name, args) {
        hits++
        this.metaClass."$name" = { -> 1 }
        this.invokeMethod(name, args)
    }
}

def takeTime(text, closure) {
    //def timesList = [500, 2000, 10000]
    def timesList = [500]
    timesList.each { it->
        def date = new Date()
        closure(it)
        println "TakeTime (${text}) Number:${it} Time: ${new Date().time - date.time}"
    }
}

takeTime('methodMissingSpeed') { value ->
    def multi = new MultiMethods()
    Random random = new Random()
    value.times {
        multi."${random.nextInt(100000)}"()
    }
}

assert true, 'Finish Ok.'

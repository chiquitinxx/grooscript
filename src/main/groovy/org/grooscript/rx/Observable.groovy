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
package org.grooscript.rx

class Observable {

    List subscribers = []
    List sourceList = null
    List chain = []

    static Observable listen() {
        new Observable()
    }

    static Observable from(List list) {
        new Observable(sourceList: list)
    }

    void produce(event) {
        subscribers.each {
            processFunction(event, it)
        }
    }

    Observable map(Closure cl) {
        chain << cl
        this
    }

    Observable filter(Closure cl) {
        chain << { it ->
            if (cl(it)) {
                it
            } else {
                throw new Exception()
            }
        }
        this
    }

    void subscribe(Closure cl) {
        while (chain) {
            cl = cl << chain.pop()
        }
        subscribers << cl
        if (sourceList) {
            sourceList.each {
                processFunction(it, cl)
            }
        }
    }

    void removeSubscribers() {
        subscribers = []
    }

    private processFunction(data, cl) {
        try {
            cl(data)
        } catch (e) {
            //println "Exception: ${e.message}"
        }
    }
}


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

class Action {

    def execute(closure) {
        closure()
    }
}

class Run {
    def action = new Action()

    def run(param) {
        return 'Running ' + param
    }

    def doAction() {
        action.execute(this.&run)
    }
}

class Doing {
    def start() {
        def run = new Run()
        run.doAction()
    }
}

def doing = new Doing()
assert doing.start() == 'Running null'

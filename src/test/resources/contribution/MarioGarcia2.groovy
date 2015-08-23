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

class Model {
    def name
    def age
}

class Application extends App {

    /**
     * Bootstraping the application
     **/
    def init(){
        println "Bootstraping the application"
        def model = new Model(name:"John",age:21)
        //def controller = new Controller()
        //controller.initFormWith(model)
        println "Application started successfully"
    }
}

class App {
    String name
}
/*

class Controller {
    def name
}*/

new Application().init()
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

interface Car {
    public Car start()
    public void stop()
    public Car move()
    public int getSpeed()
}

class MyCar implements Car {

    int speed = 0
    boolean started = false

    public Car start() {
        started = true
        this
    }

    public void stop() {
        started = false
        speed = 0
    }

    public Car move() {
        if (started) {
            speed++
        }
        this
    }

    public int getSpeed() {
        speed
    }
}

assert new MyCar().start().move().speed == 1
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
class MotorizedVehicle extends Vehicle {

    def company
    def backVehicles

    MotorizedVehicle(name,seats,wheels) {
        super(name)
        this.seats = seats
        this.wheels = wheels
        this.backVehicles = []
    }

    def putVehicleBack(vehicle) {
        backVehicles << vehicle
    }

    def String toString() {
        return "MotorizedVehicle(${name}).wheels(${wheels}).seats(${seats}).company($company).back(${backVehicles.size()})"
    }
}

class Vehicle {

    def wheels
    def seats
    def name

    Vehicle(name) {
        this.name = name
        wheels = 0
        seats = 0
    }

    def String toString() {
        return "Vehicle(${name}).wheels(${wheels}).seats(${seats})"
    }
}

def bike = new Vehicle('Bike')
bike.seats = 1
bike.wheels = 2

//We add a try test
def error = false
try {
    bike.company.gol
} catch (e) {
    error = true
}
assert error

def car = new MotorizedVehicle('Car',4,4)
car.company = 'Opel'
assert car.toString() == 'MotorizedVehicle(Car).wheels(4).seats(4).company(Opel).back(0)'

car.putVehicleBack(bike)
assert car.toString() == 'MotorizedVehicle(Car).wheels(4).seats(4).company(Opel).back(1)'
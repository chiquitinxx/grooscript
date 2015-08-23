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

class Bank {
    def money = 20
    def moneys = []

    def propertyMissing(String name) {
        if (name.startsWith('money')) {
            return name.substring(5)
        }
    }

    def propertyMissing(String name, value) {
        if (name.startsWith('money')) {
            moneys << name.substring(5)
        }
    }
}

def bank = new Bank()
assert bank.money == 20
bank.money = 10
assert bank.money == 10
assert bank.moneyYeah == 'Yeah'

bank.moneySave = 10
assert bank.moneys == ['Save']
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

class Product {
    String name
    BigDecimal price

    Product plus(Product other) {
        new Product(price: this.price + other.price)
    }
}

def products = [
                new Product(name: 'laptop', price: 999),
                new Product(name: 'netbook', price: 395),
                new Product(name: 'iPad', price: 200)
]

assert 1594 == products.sum().price

assert products.sum { it.price } == 1594
//Js cant do this atm
//assert products.price.sum() == 1594
//assert products*.price.sum() == 1594

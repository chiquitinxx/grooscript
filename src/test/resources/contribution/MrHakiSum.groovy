package contribution

/**
 * MrHaki Code
 */

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

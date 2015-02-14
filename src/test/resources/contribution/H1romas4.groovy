package contribution

/**
 * Created by jorgefrancoleza on 14/2/15.
 */

class Test {
    def field1 = 0
    def field2 = 0
    Test() {
        // OK
        (1..10).each {
            field1 += it
        }
        println field1
        // ReferenceError: "field2" is not defined.
        for(def i = 1; i <= 10; i++) {
            field2 += i
            // OK
            // this.field2 += i
        }
        println field2
    }
}

new Test()
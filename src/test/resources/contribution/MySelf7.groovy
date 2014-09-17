package contribution

/**
 * Created by jorge on 17/09/14.
 */
class Counting {
    private value = 0
    void inc() {
        value++
    }
}

class Class1 {
    Class1(counting) {
        counting.inc()
        doubleInc(counting)
    }
    def doubleInc(counting) {
        counting.inc()
        counting.inc()
    }
}

class Class2 extends Class1 {
    Class2(counting) {
        super(counting)
        counting.inc()
        doubleInc(counting)
    }
}

def counter = new Counting()
new Class2(counter)
assert counter.value == 6
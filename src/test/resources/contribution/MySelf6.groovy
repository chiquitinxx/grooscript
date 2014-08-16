package contribution

/**
 * Created by jorge on 16/08/14.
 */
class MyDsl {
    static run(Closure cl) {
        cl.delegate = new MyDsl()
        cl()
    }

    int four() {
        4
    }
}

def map = [a: 3, b: 1]

map.with {
    def number = 5
    def six = { ->
        6
    }
    MyDsl.run {
        assert four() == a + b
        assert number == 5
        assert six() == 6
    }
}
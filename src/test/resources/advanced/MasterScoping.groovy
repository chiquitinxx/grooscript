package advanced

/**
 * JFL 10/12/12
 */

class Data {

    def static COUNT = 0

    def a
    def b

    def boolean equals(o) {
        o && o.a == this.a && o.b == this.b
    }
}

class Functions {

    def static number = 0

    def process(items) {
        def result = []
        items.each { item ->
            def data = new Data()
            data.a = number++
            data.b = Data.COUNT++ % 2
            //TODO leftShift
            result << data
        }

        result
    }
}

func = new Functions()

result = func.process([1,2,3,4,5])

assert result && result.size() == 5
assert result[0] == new Data(a: 0, b: 0)
assert result[1] == new Data(a: 1, b: 1)
assert result[2] == new Data(a: 2, b: 0)

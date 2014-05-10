package web

trait RandomAbility {
    def methodMissing(String name, args) {
        if (name.startsWith('random')) {
            def number = name.substring(6) as int
            new Random().nextInt(number)
        } else {
            throw new RuntimeException()
        }
    }
}

class MyNumbers implements RandomAbility {
}

def myNumbers = new MyNumbers()

100.times {
    assert myNumbers.random10() < 10
}
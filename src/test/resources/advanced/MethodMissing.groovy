package advanced

/**
 * JFL 22/12/12
 */

class Missing {

    def a

    def countMissing = 0

    def methodMissing(String name,args) {
        countMissing++
        this.metaClass."$name" = { ->
            a++
        }
    }

    Missing() {
        def mc = new ExpandoMetaClass(Missing, false, true)
        mc.initialize()
        this.metaClass = mc
    }


}

def missing = new Missing()

missing.a = 5

assert missing.getA() == 5
assert missing.countMissing == 0


missing.doSomething()

assert missing.countMissing == 1


missing.doSomething()
assert missing.countMissing == 1
assert missing.a == 6

def expandoMissing = new Expando()
count = 0
expandoMissing.metaClass.methodMissing = { String name,args ->
    count++
}

expandoMissing.doSomething()
assert count == 1
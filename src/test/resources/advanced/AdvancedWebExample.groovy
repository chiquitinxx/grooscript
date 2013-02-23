package advanced

/**
 * User: jorgefrancoleza
 * Date: 22/02/13
 */

class Tests {
    def 'failTest'() {
        def a = 2

        then:
        check(a == 1)
    }

    def 'okTest'() {
        check(1 == 1)
    }
}

Tests.metaClass.static.doTest = {
    def result = []
    def tests = new Tests()
    tests.metaClass.check = { exp ->
        if (!exp) {
            throw new Exception()
        }
    }

    tests.metaClass.methods.findAll { !['setProperty','setMetaClass','invokeMethod','getProperty','getMetaClass','__$swapInit',
       'wait','toString','notifyAll','notify','hashCode','getClass','equals','doTest','check'].contains(it.name) }
    .each { MetaMethod method ->

        def test = new Expando(name:method.name,fail:false)
        try {
            tests."${method.name}"()
        } catch (e) {
            test.fail = true
        }
        result << test
    }

    return result
}

def result = Tests.doTest()
assert result.size() == 2
assert result.findAll { it.fail == true}.size() == 1

class Components {
    def static salute(String who) {
        return "Hello ${who}!"
    }
}


class LittleDsl {

    def StringBuffer text = new StringBuffer('')

    def static build(Closure closure) {
        def builder = new LittleDsl()
        closure.delegate = builder
        closure()
        return builder.text.toString()
    }

    def add(textToAdd) {
        text << textToAdd
    }

    def methodMissing(String name,args) {
        text << "<${name}>"
        if (args && args.last() instanceof Closure) {
            def clo = args.last()
            //clo.delegate = this
            clo()
        }
        text << "</${name}>"
    }
}

def text = LittleDsl.build {
    if (result.size()<=0) {
        p { add 'No results!' }
    } else {
        ul {
            def i = 0
            result.each { item ->
                li { add "(${i++}) Name: ${item.name} Fail:${item.fail}"}
            }
        }
        use (Components) {
            p { add "Jorge".salute()}
        }
    }
}

assert text == '<ul><li>(0) Name: failTest Fail:true</li><li>(1) Name: okTest Fail:false</li></ul><p>Hello Jorge!</p>'

def name = "Groovy"
name.metaClass.mixin Components
assert name.salute() == "Hello Groovy!"



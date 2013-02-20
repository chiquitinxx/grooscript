package advanced

/**
 * User: jorgefrancoleza
 * Date: 20/02/13
 */
a = 5
def f = {
    return a
}
assert f() == 5

class HtlmDsl {

    def text

    def build(Closure closure) {
        text = ''
        closure.delegate = this
        closure()
        return text
    }

    def methodMissing(String name,args) {
        text+="<${name}"
        if (args[0] instanceof HashMap) {
            args[0].each {
                text+=" ${it.key}='${it.value}'"
            }
        }
        text+=">"
        if (args[args.size()-1] instanceof Closure) {
            def clo = args[args.size()-1]
            clo.delegate = this
            clo()
        }
        text+="</${name}>"
    }
}

def builder = new HtlmDsl()

def putDiv = false

def result = builder.build {
    def five = 5
    html {
        if (putDiv) {
            div (id:'hola') {

            }
        }
        span (id:'adios',number:five)
    }
}

assert result == '<html><span id=\'adios\' number=\'5\'></span></html>'

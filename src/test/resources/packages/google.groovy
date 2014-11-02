package packages

class MM {
    def propertyMissing(String name) {
        new MM()
    }
    def methodMissing(String name, args) {
        println "Calling $name with: $args"
        return '1'
    }
}

def use = { String pack ->
    new MM()
}

//Code to convert

//use can be used with any ui framework
def goog = use('goog.crypt.base64')

def input = "Lorem ipsum dolor sit amet"
def output = goog.crypt.base64.encodeString input
println "Original string: $input"
println "Encoded base64 string: $output"

assert output == '1'

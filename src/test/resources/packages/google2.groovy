package packages

/**
 * User: jorgefrancoleza
 * Date: 2/11/14
 */
//Not needed
//import static goog.crypt.base64.encodeString

def input = "Lorem ipsum dolor sit amet"
def output = goog.crypt.base64.encodeString(input)
println "Original string: $input"
println "Encoded base64 string: $output"

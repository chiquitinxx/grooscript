/**
 * JFL 08/09/12
 */
class Greet {
    def origin
    def destination

    def salute() {
        destination.each { it -> println "$origin: Hello $it!"}
    }
}

g = new Greet([origin:'GScript',destination:['Groovy','JavaScript']])
g.salute()
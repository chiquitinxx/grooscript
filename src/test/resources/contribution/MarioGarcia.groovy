package contribution

/**
 * JFL 03/11/12
 */

class SomeClass {

    public fieldWithModifier
    String typedField

    def untypedField
    protected field1, field2, field3
    private assignedField = new Date()
    static classField

    public static final String CONSTA = 'a', CONSTB = 'b'
    def someMethod(){
        def localUntypedMethodVar = 1

        int localTypedMethodVar = 1

        def localVarWithoutAssignment, andAnotherOne
    }
}

def localvar = 1

boundvar1 = 1

def someMethod(){

    localMethodVar = 1

    boundvar2 = 1

}

class Counter {

    public count = 0

}
def counter = new Counter()
counter.count = 1

assert counter.count == 1

def fieldName = 'count'

counter[fieldName] = 2

assert counter['count'] == 2

package advanced

/**
 * JFL 16/09/12
 */
// tag::mark[]
clever = new Expando()

clever.name = 'Groovy'
clever."$clever.name" = { 'YES'}

assert clever.Groovy() == 'YES'
clever."name" = 'Groovy 2.0'
assert clever.name ==  'Groovy 2.0'

def anotherExpando = new Expando(one:1,two:2)

assert anotherExpando.one == 1
assert anotherExpando.two == 2
// end::mark[]

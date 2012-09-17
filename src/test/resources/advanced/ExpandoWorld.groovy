package advanced

/**
 * JFL 16/09/12
 */

clever = new Expando()

clever.name = 'Groovy'
clever."$clever.name" = { 'YES'}

assert clever.Groovy() == 'YES'
clever."name" = 'Groovy 2.0'
assert clever.name ==  'Groovy 2.0'


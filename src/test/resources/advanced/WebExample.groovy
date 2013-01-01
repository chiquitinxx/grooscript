package advanced

/**
 * User: jorgefrancoleza
 * Date: 01/01/13
 */

def sayHello = { println "Hello ${it}!" }
['Groovy','JavaScript','GrooScript'].each sayHello

assert [1,2,3].size() == 3
class Amazing {}

amazing = new Amazing()
amazing.metaClass.who = []
amazing.metaClass.add = { who << it}

assert amazing.who.size()==0
amazing.add 'Rafa Nadal'
assert amazing.who.size()==1

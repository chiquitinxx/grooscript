package org.grooscript.packages

/**
 * Created by jorgefrancoleza on 19/11/14.
 */
class DojoPackage {
    Dojo dojo = new Dojo()

    static require(String pack, @DelegatesTo(DojoPackage) Closure cl) {
        def dojoPackage = new DojoPackage()
        cl.delegate = dojoPackage
        cl()
    }
}

class Dojo {
    Cldr cldr = new Cldr()
}

class Cldr {
    Monetary monetary = new Monetary()
}

class Monetary {
    def getData(String currency) {
        [ places: currency + new Random().nextInt(50), round: currency + new Random().nextInt(50) ]
    }
}
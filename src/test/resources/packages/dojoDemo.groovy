package packages

/**
 * Created by jorgefrancoleza on 18/11/14.
 */
import static org.grooscript.packages.DojoPackage.require

require("dojo/cldr/monetary") {
    ["EUR", "USD", "GBP", "JPY"].each {
        def cldrMonetaryData = dojo.cldr.monetary.getData(it)
        println "Places: $cldrMonetaryData.places"
        println "Round: $cldrMonetaryData.round"
    }
}
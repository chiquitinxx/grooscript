package doc

/**
 * Created by jorgefrancoleza on 20/12/14.
 */
// tag::features[]
class Language {
    def name
    def features

    def twoNiceFeatures() {
        def result = []
        2.times {
            result << features[randomFeature()]
        }
        result.collect this.&nice
    }

    def randomFeature() {
        new Random().nextInt(features.size())
    }

    def nice(value) {
        "* ${value.toUpperCase()} *"
    }
}

def groovy = new Language(name: 'Groovy', features: [
        'Flat learning curve', 'Smooth Java integration', 'Vibrant and rich ecosystem',
        'Powerful features', 'Domain-Specific Languages', 'Scripting and testing glue'
])

def (feature1, feature2) = groovy.twoNiceFeatures()
//* DOMAIN-SPECIFIC LANGUAGES *
//* POWERFUL FEATURES *
// end::features[]
println feature1
println feature2
assert feature1
assert feature2

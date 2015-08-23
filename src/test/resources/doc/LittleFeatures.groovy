/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package doc

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

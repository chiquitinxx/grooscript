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
package advanced

// ~ creates a Pattern from String
def pattern = ~/foo/
//assert pattern instanceof Pattern
assert pattern.matcher("foo").matches()    // returns TRUE
assert !pattern.matcher("foobar").matches() // returns FALSE, because matches() must match whole String

// =~ creates a Matcher, and in a boolean context, it's "true" if it has at least one match, "false" otherwise.
assert "cheesecheese" =~ "cheese"
assert "cheesecheese" =~ /cheese/
assert "cheese" == /cheese/   /*they are both string syntaxes*/
assert ! ("cheese" =~ /ham/)

// ==~ tests, if String matches the pattern
assert "2009" ==~ /\d+/  // returns TRUE
assert !("holla" ==~ /\d+/) // returns FALSE

// lets create a Matcher
def matcher = "cheesecheese" =~ /cheese/
//assert matcher instanceof Matcher

// lets do some replacement
def cheese = ("cheesecheese" =~ /cheese/).replaceFirst("nice")
assert cheese == "nicecheese"
assert "color" == "colour".replaceFirst(/ou/, "o")

cheese = ("cheesecheese" =~ /cheese/).replaceAll("nice")
assert cheese == "nicenice"

// simple group demo
// You can also match a pattern that includes groups.  First create a matcher object,
// either using the Java API, or more simply with the =~ operator.  Then, you can index
// the matcher object to find the matches.  matcher[0] returns a List representing the
// first match of the regular expression in the string.  The first element is the string
// that matches the entire regular expression, and the remaining elements are the strings
// that match each group.
// Here's how it works:
def m = "foobarfoo" =~ /o(b.*r)f/
assert m[0] == ["obarf", "bar"]
assert m[0][1] == "bar"

// Although a Matcher isn't a list, it can be indexed like a list.  In Groovy 1.6
// this includes using a collection as an index:

matcher = "eat green cheese" =~ "e+"

assert "ee" == matcher[2]
assert ["ee", "e"] == matcher[2..3]
/*
assert ["e", "ee"] == matcher[0, 2]
assert ["e", "ee", "ee"] == matcher[0, 1..2]
*/

matcher = "cheese please" =~ /([^e]+)e+/
assert ["se", "s"] == matcher[1]

/*
assert [["se", "s"], [" ple", " pl"]] == matcher[1, 2]
*/
assert [["se", "s"], [" ple", " pl"]] == matcher[1 .. 2]
/*
assert [["chee", "ch"], [" ple", " pl"], ["ase", "as"]] == matcher[0, 2..3]
*/

// Matcher defines an iterator() method, so it can be used, for example,
// with collect() and each():
matcher = "cheese please" =~ /([^e]+)e+/
matcher.each { println it }
matcher.reset()
assert matcher.collect { it } ==
[["chee", "ch"], ["se", "s"], [" ple", " pl"], ["ase", "as"]]
// The semantics of the iterator were changed by Groovy 1.6.
// In 1.5, each iteration would always return a string of the entire match, ignoring groups.
// In 1.6, if the regex has any groups, it returns a list of Strings as shown above.

// there is also regular expression aware iterator grep()
assert ["foo", "moo"] == ["foo", "bar", "moo"].grep(~/.*oo$/)
// which can be written also with findAll() method
assert ["foo", "moo"] == ["foo", "bar", "moo"].findAll { it ==~ /.*oo/ }
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
package classes

class Add1 {
    static String addThis(item,text) {
        return item.text + text
    }
    static String whoAmI(item) {
        return item.class.name
    }
}

class Add2 {
    static seven(item) {
        return 7
    }
}

class Add3 {
    static four(item) {
        return 4
    }
}


class ItemToAdd {
    def text = ''
}

// Runtime mixin on String class.
// mixin() is a GDK extension to Class.
ItemToAdd.mixin Add1

assert new ItemToAdd().whoAmI() == 'classes.ItemToAdd'
assert new ItemToAdd().addThis('!!!') == '!!!'
def errors = false
try {
    assert new ItemToAdd().seven() == 7
} catch (e) {
    errors = true
}
assert errors

ItemToAdd.mixin Add2
assert new ItemToAdd().seven() == 7
assert new ItemToAdd().whoAmI() == 'classes.ItemToAdd'

def item = new ItemToAdd()
item.metaClass.mixin Add3

assert item.seven() == 7
assert item.four() == 4

errors = false
try {
    new ItemToAdd().four()
} catch (e) {
    errors = true
}
assert errors

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
class GTree {

    def root
    def curText

    def private insert(leaf,object) {

        if (leaf==object) {
            //Nothing to do, we dont add repeated values
        } else if (leaf > object) {
            if (leaf.left) {
                insert leaf.left,object
            } else {
                leaf.left = object
            }
        } else if (leaf < object) {
            if (leaf?.right) {
                insert leaf.right,object
            } else {
                leaf.right = object
            }
        }
    }

    def private addText(text) {
        if (!curText) {
            curText = text
        } else {
            curText += " - ${text}"
        }
    }

    def private followPrint(leaf) {
        if (leaf.left) {
            followPrint leaf.left
        }
        addText(leaf)
        if (leaf.right) {
            followPrint leaf.right
        }
    }

    def String toString() {
        def result = '[empty]'
        if (root) {
            followPrint root
            result = curText
        }

        return result
    }

    def add(object) {
        object.metaClass.left = null
        object.metaClass.right = null
        if (!root) {
            root = object
        } else {
            insert root,object
        }
        this
    }
}

def tree = new GTree()
tree.add('5').add('7').add('3').add('9').add('2').add('8').add('6').add('4').add('1').add('10')

assert tree.toString() == '1 - 10 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9'

def treeNumbers = new GTree()
treeNumbers.add(5).add(7).add(3).add(9).add(2).add(8).add(6).add(4).add(1).add(10)

assert treeNumbers.toString() == '1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9 - 10'

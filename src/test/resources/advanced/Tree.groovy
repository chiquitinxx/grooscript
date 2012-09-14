/**
 * JFL 13/09/12
 */
class GTree {

    def root
    def curText

    def private insert(leaf,object) {

        if (leaf==object) {
            //Nothing to do, we dont add repeated values
        } else if (leaf>object) {
            if (leaf.left) {
                insert leaf.left,object
            } else {
                leaf.left = object
            }
        } else if (leaf<object) {
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

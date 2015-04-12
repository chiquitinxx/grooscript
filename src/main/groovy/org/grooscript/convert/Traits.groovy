package org.grooscript.convert

import org.codehaus.groovy.ast.ClassNode
import org.grooscript.util.Util

/**
 * User: jorgefrancoleza
 * Date: 28/04/14
 */
class Traits {

    boolean isTrait(ClassNode classNode) {
        if (Util.groovyVersionAtLeast('2.3')) {
            return org.codehaus.groovy.transform.trait.Traits.isTrait(classNode)
        } else {
            return false
        }
    }

    boolean isTraitHelper(ClassNode classNode) {
        if (Util.groovyVersionAtLeast('2.3')) {
            return classNode.nameWithoutPackage.endsWith(org.codehaus.groovy.transform.trait.Traits.TRAIT_HELPER)
        } else {
            return false
        }
    }
}

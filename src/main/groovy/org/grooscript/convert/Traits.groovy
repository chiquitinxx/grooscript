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
package org.grooscript.convert

import org.codehaus.groovy.ast.ClassNode
import org.grooscript.util.Util

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

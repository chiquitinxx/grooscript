package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.InnerClassNode

/**
 * User: jorgefrancoleza
 * Date: 28/12/14
 */
class TraitBaseHandler extends BaseHandler {

    protected boolean isAccessorOfStaticField(String nameMethod, InnerClassNode classNode) {
        if (nameMethod.startsWith('get') || nameMethod.startsWith('set')) {
            def nameField = nameMethod.substring(3)
            def otherNameField = nameField[0].toLowerCase() + nameField.substring(1)
            return traitHasStaticField(classNode.outerClass, nameField) ||
                    traitHasStaticField(classNode.outerClass, otherNameField)
        }
        false
    }

    protected boolean isAccessorOfStaticField(String nameMethod, ClassNode classNode) {
        if (nameMethod.startsWith('get') || nameMethod.startsWith('set')) {
            def nameField = nameMethod.substring(3)
            def otherNameField = nameField[0].toLowerCase() + nameField.substring(1)
            if (context.traitFieldScopeContains(nameField)) {
                return traitHasStaticField(classNode, nameField)
            }
            if (context.traitFieldScopeContains(otherNameField)) {
                return traitHasStaticField(classNode, otherNameField)
            }
        }
        return false
    }


    private boolean traitHasStaticField(ClassNode classNode, String nameField) {
        ClassNode helperFieldsNode = org.codehaus.groovy.transform.trait.Traits.findHelpers(classNode).fieldHelper
        boolean found = false
        if (helperFieldsNode) {
            found = helperFieldsNode.fields?.findAll{ it.name.startsWith('$static') }.any { FieldNode fieldNode ->
                def name = fieldNode.name.substring(fieldNode.name.lastIndexOf('__') + 2)
                name == nameField
            }
        }
        found
    }

    protected List listStaticFields(ClassNode classNode) {
        def list
        ClassNode helperFieldsNode = org.codehaus.groovy.transform.trait.Traits.findHelpers(classNode).fieldHelper
        if (helperFieldsNode) {
            list = helperFieldsNode.fields?.findAll{ it.name.startsWith('$static') }
        }
        list
    }
}

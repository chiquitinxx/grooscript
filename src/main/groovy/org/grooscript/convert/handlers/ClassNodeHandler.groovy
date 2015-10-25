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
package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.grooscript.convert.ConversionOptions

import static org.grooscript.JsNames.*

class ClassNodeHandler extends TraitBaseHandler {

    void handle(ClassNode node) {
        //Exit if it doesn't have to convert
        if (haveAnnotationNonConvert(node.annotations)) {
            return
        }

        out.addLine()

        //Push name in stack
        context.classNameStack.push(node.nameWithoutPackage)
        context.variableScoping.push([])
        context.variableStaticScoping.push([])
        populateTraitFieldsScoping(node)

        out.block ("function ${node.nameWithoutPackage}() ") {

            //Limited allowed inheritance
            context.superNameStack.push(node.superClass.name)
            if (node.superClass.name != 'java.lang.Object' && node.superClass.name != 'groovy.lang.Script') {
                //println 'Allowed!'+ node.superClass.class.name
                out.addScript("var ${GS_OBJECT} = ${node.superClass.nameWithoutPackage}();", true)

                //We add to this class scope variables of fathers
                addSuperClassesToScope(node.superClass)
            } else {
                out.addScript("var ${GS_OBJECT} = ${GS_INIT_CLASS}('${node.nameWithoutPackage}');", true)
            }

            //Class names and interfaces
            putClassNamesAndInterfaces(node)

            //Add variable names to scope
            addClassVariableNamesToScope(node)

            //Traits
            checkTraits(node)

            //Adding initial values of properties
            node?.properties?.each { PropertyNode property ->
                if (!property.isStatic()) {
                    addPropertyToClass(property, false)
                    //We add variable names of the class
                } else {
                    addPropertyStaticToClass(property.name)
                }
            }

            //Add fields not added as properties
            node.fields.each { FieldNode field ->
                checkDelegateAnnotation(field, node.nameWithoutPackage)
                if (isOnlyFieldOfClassNode(field, node)) {
                    if (!field.isStatic()) {
                        addPropertyToClass(field, false)
                    } else {
                        addPropertyStaticToClass(field.name)
                    }
                }
            }

            processClassMethods(node?.methods, node)

            //Constructors
            checkConstructors(node)

            //@Mixin
            checkAddMixin(node.nameWithoutPackage, node.annotations)

            out.addLine()
            out.addScript("return ${GS_OBJECT};")
        }

        context.staticProcessNode = node
        //Static methods
        node?.methods?.each { MethodNode method ->
            if (!haveAnnotationNonConvert(method.annotations)) {
                if (method.isStatic()) {
                    if (functions.haveAnnotationNative(method.annotations)) {
                        functions.putGsNativeMethod("${node.nameWithoutPackage}.${method.name}", node, method)
                    } else {
                        functions.processBasicFunction("${node.nameWithoutPackage}.${method.name}", method, false)
                    }
                }
            }
        }

        //Static properties
        node?.properties?.each { it->
            if (it.isStatic()) {
                out.addScript(node.nameWithoutPackage)
                addPropertyToClass(it, true)
            }
        }
        //Static fields
        node?.fields?.each { it->
            if (it.isStatic() && isOnlyFieldOfClassNode(it, node)) {
                out.addScript(node.nameWithoutPackage)
                addPropertyToClass(it, true)
            }
        }
        //Static fields of traits
        initStaticFieldsInTraits(node)

        //Static methods
        initStaticMethodsInTraits(node)

        context.staticProcessNode = null

        //Remove variable class names from the list
        context.variableScoping.pop()
        context.variableStaticScoping.pop()

        //Pop name in stack
        context.classNameStack.pop()
        context.superNameStack.pop()

        context.clearTraitFieldsScoping()

        //@Category
        checkAddCategory(node.nameWithoutPackage, node.annotations)
    }

    private processClassMethods(List<MethodNode> methods, ClassNode classNode) {

        def wasProcessingClassMethods = context.processingClassMethods
        context.processingClassMethods = true
        methods?.each { MethodNode methodNode ->
            context.currentClassMethodConverting = methodNode.name
            if (!haveAnnotationNonConvert(methodNode.annotations) && !methodNode.isAbstract()) {
                //Process the methods
                if (functions.haveAnnotationNative(methodNode.annotations) && !methodNode.isStatic()) {
                    functions.putGsNativeMethod("${GS_OBJECT}.${methodNode.name}", classNode, methodNode)
                } else if (!methodNode.isStatic()) {
                    if (methodNode.name == 'propertyMissing' && methodNode.parameters.length == 2) {
                        functions.processBasicFunction("${GS_OBJECT}['setPropertyMissing']", methodNode, false)
                    } else {
                        conversionFactory.visitNode(methodNode, false)
                    }
                } else {
                    staticMethod(methodNode, GS_OBJECT, classNode.nameWithoutPackage)
                }
            }
            context.currentClassMethodConverting = null
        }
        context.processingClassMethods = wasProcessingClassMethods
    }

    private staticMethod(MethodNode methodNode, String objectName, String nodeName, boolean withSelf = false) {
        //We put the number of params as x? name variables
        def numberParams = 0
        if (methodNode.parameters && methodNode.parameters.size()>0) {
            numberParams = methodNode.parameters.size()
        }
        def params = []
        def paramsCall = []
        numberParams.times { number ->
            params << 'x'+number
            paramsCall << 'x'+number
        }
        if (withSelf && params) {
            params.remove(0)
            paramsCall[0] = objectName
        }

        out.addScript("${objectName}.${methodNode.name} = function(${params.join(',')}) { return ${nodeName}.${methodNode.name}(")
        out.addScript(paramsCall.join(','))
        out.addScript("); }", true)
    }

    private haveAnnotationNonConvert(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            //If it doesn't have to convert, then exit
            if (it.getClassNode().nameWithoutPackage=='GsNotConvert') {
                exit = true
            }
        }
        return exit
    }

    private haveAnnotationGroovyImmutable(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            if (it.getClassNode().name == 'groovy.transform.Immutable') {
                exit = true
            }
        }
        exit
    }

    private checkConstructors(ClassNode node) {

        boolean has1parameterConstructor = false
        node?.declaredConstructors?.each { MethodNode it->
            def numberArguments = it.parameters?.size()
            if (numberArguments == 1) {
                has1parameterConstructor = true
            }
            conversionFactory.visitNode(it, true)

            addConditionConstructorExecution(numberArguments, it.parameters)
        }

        if (haveAnnotationGroovyImmutable(node.annotations)) {
            //Add a constructor with params
            def paramSize = node.properties.size()
            def paramNames = node.properties.collect { it.name }.join(', ')
            def nameFunction = "${GS_OBJECT}.${node.nameWithoutPackage}${paramSize}"
            out.addScript("${nameFunction} = function(${paramNames}) {")
            node.properties.collect { it.name }.each {
                out.addScript("  ${GS_OBJECT}.${it} = ${it}; ")
            }
            out.addScript("  return this; ")
            out.addScript("};", true)
            out.addScript("if (arguments.length==${paramSize}) {" +
                    "${nameFunction}.apply(${GS_OBJECT}, arguments); }", true)
            if (paramSize == 1) {
                has1parameterConstructor = true
            }
        }

        //If no constructor with 1 parameter, we create 1 that get a map, for put value on properties
        if (!has1parameterConstructor) {
            out.addScript("if (arguments.length == 1) {" +
                    "${GS_PASS_MAP_TO_OBJECT}(arguments[0],${GS_OBJECT});};", true)
        }
    }

    private addPropertyToClass(fieldOrProperty, isStatic) {

        def previous = GS_OBJECT
        if (isStatic) {
            previous = ''
        }

        if (isRequireJsModuleAnnotated(fieldOrProperty) &&
                conversionFactory.converter.conversionOptions[ConversionOptions.REQUIRE_JS_MODULE.text] == true) {
            out.addScript("${previous}.${fieldOrProperty.name} = ${fieldOrProperty.name};", true)
            addRequireJsDependency(fieldOrProperty)
        } else if (fieldOrProperty.initialExpression) {
            out.addScript("${previous}.${fieldOrProperty.name} = ")
            conversionFactory.visitNode(fieldOrProperty.initialExpression)
            out.addScript(';', true)
        } else {
            out.addScript("${previous}.${fieldOrProperty.name} = null;", true)
        }
    }

    private boolean isRequireJsModuleAnnotated(fieldOrProperty) {
        def annotations = (fieldOrProperty instanceof PropertyNode ?
                fieldOrProperty.field.annotations : fieldOrProperty.annotations)
        annotations.any { AnnotationNode annotationNode ->
            annotationNode.getClassNode().name == 'org.grooscript.asts.RequireJsModule'
        }
    }

    private addRequireJsDependency(fieldOrProperty) {
        def annotations = (fieldOrProperty instanceof PropertyNode ?
                fieldOrProperty.field.annotations : fieldOrProperty.annotations)
        AnnotationNode annotationNode = annotations.find {
            it.getClassNode().name == 'org.grooscript.asts.RequireJsModule'
        }
        conversionFactory.converter.addRequireJsDependency(annotationNode.getMember('path').value, fieldOrProperty.name)
    }

    private addPropertyStaticToClass(String name) {

        out.addScript("Object.defineProperty(${GS_OBJECT}, '${name}', { " +
                "get: function() { return ${context.classNameStack.peek()}.${name}; }, " +
                "set: function(${VALUE}) { ${context.classNameStack.peek()}.${name} = ${VALUE}; }, " +
                "enumerable: true });", true)
    }

    private checkAddMixin(className, annotations) {

        annotations.each { AnnotationNode annotationNode ->
            if (annotationNode.getClassNode().name == 'groovy.lang.Mixin') {
                def list = []
                annotationNode.members.values().each { value ->
                    if (value instanceof ListExpression) {
                        value.expressions.each { it ->
                            list << it.type.nameWithoutPackage
                        }
                    } else {
                        list << value.type.nameWithoutPackage
                    }
                }
                addMixinToClass(className, list)
            }
        }
    }

    private addMixinToClass(className, List<String> listMixins) {
        out.addScript("${GS_MIXIN_CLASS}('${className}',")
        out.addScript('[')
        out.addScript listMixins.join(',')
        out.addScript(']);', true)
    }

    private checkAddCategory(className, annotations) {
        annotations.each { AnnotationNode annotationNode ->
            if (annotationNode.getClassNode().name == 'groovy.lang.Category') {
                annotationNode.members.values().each { value ->
                    if (value instanceof ListExpression) {
                        value.expressions.each { it ->
                            addCategoryToClass(className, it.type.nameWithoutPackage)
                        }
                    } else {
                        addCategoryToClass(className, value.type.nameWithoutPackage)
                    }
                }
                out.addScript("${GS_MY_CATEGORIES}['${className}'] = ${className};", true)
            }
        }
    }

    private addCategoryToClass(categoryName, className) {
        out.addScript("${GS_ADD_CATEGORY_ANNOTATION}('${categoryName}','${className}');", true)
    }

    /**
     * Create code the js class definition, for execute constructor
     * @param numberArguments
     * @param paramList
     * @return
     */
    private addConditionConstructorExecution(numberArguments, paramList) {

        out.addScript("if (arguments.length==${numberArguments}) {")
        out.addScript("${GS_OBJECT}.${context.classNameStack.peek()}${numberArguments}")

        out.addScript '('
        def count = 0
        paramList?.each { param ->
            if (count>0) out.addScript ', '
            out.addScript("arguments[${count}]")
            count++
        }
        out.addScript ')'

        out.addScript('; }', true)
    }

    private addSuperClassesToScope(ClassNode node) {
        addClassVariableNamesToScope(node)
        if (node.superClass.isPrimaryClassNode() && node.superClass.name != 'java.lang.Object') {
            addSuperClassesToScope(node.superClass)
        }
    }

    private addClassVariableNamesToScope(ClassNode node) {
        node?.properties?.each { PropertyNode property ->
            if (!property.isStatic()) {
                context.variableScoping.peek().add(property.name)
            } else {
                context.variableStaticScoping.peek().add(property.name);
            }
        }
        node.fields.each { FieldNode field ->
            if (field.owner.name == node.name && (field.isPublic()|| !node.properties.any { it.name == field.name})) {
                if (!field.isStatic()) {
                    context.variableScoping.peek().add(field.name)
                } else {
                    context.variableStaticScoping.peek().add(field.name)
                }
            }
        }
        node.methods?.each { MethodNode it ->
            //Add method names to variable scoping
            if (!it.isStatic() && !it.isAbstract()) {
                context.variableScoping.peek().add(it.name)
            }
        }
    }

    private checkDelegateAnnotation(FieldNode fieldNode, String nameClass) {
        if (fieldHasDelegateAnnotation(fieldNode)) {
            out.addScript("$GS_AST_DELEGATE('${nameClass}', '${fieldNode.name}');", true)
        }
    }

    private boolean fieldHasDelegateAnnotation(FieldNode fieldNode) {
        fieldNode.annotations.any { annotationNode ->
            annotationNode.getClassNode().name == 'groovy.lang.Delegate'
        }
    }

    private checkTraits(ClassNode classNode) {
        classNode.interfaces.findAll {
            traits.isTrait(it)
        }.each {
            handleTrait(classNode, it, ['getMetaClass', 'setMetaClass', 'invokeMethod'])
        }
    }

    private handleTrait(ClassNode actualClassNode, ClassNode traitClassNode, notAddThisMethods = []) {
        addClassVariableNamesToScope(traitClassNode)
        ClassNode helperClassNode = org.codehaus.groovy.transform.trait.Traits.findHelpers(traitClassNode).helper
        helperClassNode.outerClass?.interfaces?.findAll{ traits.isTrait(it) }.each { ClassNode cn ->
            handleTrait(actualClassNode, cn, notAddThisMethods + helperClassNode.methods*.name)
        }
        addTraitMethods(actualClassNode, traitClassNode, helperClassNode, notAddThisMethods)
    }

    private static final METHODS_THAT_MAYBE_NOT_DEFINED_IN_TRAIT = ['getProperty', 'setProperty']

    private addTraitMethods(ClassNode actualClassNode, ClassNode traitClassNode, ClassNode helperClassNode, notAddThisMethods) {
        helperClassNode.methods.findAll { it.name != '$static$init$'} .each {
            if (it.name == '$init$') {
                if (!it.code.isEmpty()) {
                    out.addScript("${traitClassNode.nameWithoutPackage}.\$init\$(${GS_OBJECT});", true)
                }
            } else if (isAccessorOfStaticField(it.name, traitClassNode)) {
                def fieldName = context.findTraitScopeByName(it.name.substring(3))
                if (it.name.startsWith('get'))
                    out.addScript("${GS_OBJECT}.${it.name} = function() { " +
                            " return ${actualClassNode.nameWithoutPackage}.${fieldName} };", true)
                if (it.name.startsWith('set'))
                    out.addScript("${GS_OBJECT}.${it.name} = function(x0) { " +
                            " ${actualClassNode.nameWithoutPackage}.${fieldName} = x0 };", true)
            } else {
                if (!(it.name in notAddThisMethods) && !it.synthetic) {
                    if (it.name in METHODS_THAT_MAYBE_NOT_DEFINED_IN_TRAIT) {
                        out.addScript("if (${traitClassNode.nameWithoutPackage}['${it.name}']) {", true)
                        out.addTab()
                    }
                    staticMethod(it, GS_OBJECT, traitClassNode.nameWithoutPackage, true)
                    if (it.name in METHODS_THAT_MAYBE_NOT_DEFINED_IN_TRAIT) {
                        out.addScript('}', true)
                    }
                }
            }
        }
    }

    private putClassNamesAndInterfaces(ClassNode node) {
        out.addScript("${GS_OBJECT}.${CLASS} = ${jsObjectNames(node)};", true)
        if (node.superClass) {
            out.addScript("${GS_OBJECT}.${CLASS}.superclass = ${jsObjectNames(node.superClass)};", true)
        }
        if (node.interfaces) {
            out.addScript("${GS_OBJECT}.${CLASS}.interfaces = [")
            def allInterfaces = getAllInterfaces(node, [])
            out.addScript(allInterfaces.collect { jsObjectNames(it) }.join(', '))
            out.addScript('];', true)
        }
    }

    private jsObjectNames(ClassNode node) {
        "{ name: '${node.name}', simpleName: '${node.nameWithoutPackage}'}"
    }

    private getAllInterfaces(ClassNode classNode, list) {
        classNode.interfaces.each {
            list << it
            getAllInterfaces(it, list)
        }
        list
    }

    private isOnlyFieldOfClassNode(FieldNode fieldNode, ClassNode classNode) {
        fieldNode.owner.name == classNode.name && !classNode.properties.any { it.name == fieldNode.name}
    }

    private populateTraitFieldsScoping(ClassNode classNode) {
        classNode?.interfaces.findAll {
            traits.isTrait(it)
        }.each {
            addTraitFieldsToContext(it)
        }
    }

    private addTraitFieldsToContext(ClassNode classNode) {
        ClassNode helperFieldsNode = org.codehaus.groovy.transform.trait.Traits.findHelpers(classNode).fieldHelper
        if (helperFieldsNode) {
            populateTraitFieldsScoping(helperFieldsNode.outerClass)
            helperFieldsNode.fields?.each { FieldNode fieldNode ->
                def name = fieldNode.name.substring(fieldNode.name.lastIndexOf('__') + 2)
                context.addToTraitFieldsScoping(name)
            }
        }
    }

    private initStaticFieldsInTraits(ClassNode classNode) {
        classNode?.interfaces.findAll {
            traits.isTrait(it)
        }.each {
            checkStaticPropertiesInTrait(classNode, it)
        }
    }

    private checkStaticPropertiesInTrait(ClassNode actualClassNode, ClassNode traitClassNode) {
        addClassVariableNamesToScope(traitClassNode)
        ClassNode helperClassNode = org.codehaus.groovy.transform.trait.Traits.findHelpers(traitClassNode).helper
        helperClassNode.outerClass?.interfaces?.findAll{ traits.isTrait(it) }.each { ClassNode cn ->
            checkStaticPropertiesInTrait(actualClassNode, cn)
        }
        def list = listStaticFields(traitClassNode)
        if (list)
            out.addScript("${traitClassNode.nameWithoutPackage}\$static\$init\$(${actualClassNode.nameWithoutPackage});", true)
    }

    private initStaticMethodsInTraits(ClassNode classNode) {
        classNode?.interfaces.findAll {
            traits.isTrait(it)
        }.each {
            checkStaticMethodsInTrait(classNode, it)
        }
    }

    private checkStaticMethodsInTrait(ClassNode actualClassNode, ClassNode traitClassNode) {
        ClassNode helperClassNode = org.codehaus.groovy.transform.trait.Traits.findHelpers(traitClassNode).helper
        helperClassNode.outerClass?.interfaces?.findAll{ traits.isTrait(it) }.each { ClassNode cn ->
            checkStaticMethodsInTrait(actualClassNode, cn)
        }
        def icn = helperClassNode.outerClass
        icn?.@methods?.map.findAll { !it.value && !(isAccessorOfStaticField(it.key, traitClassNode)) }.
                each { key, value ->
            out.addScript("${actualClassNode.nameWithoutPackage}.${key} = function() {" +
                    " return ${icn.nameWithoutPackage}.${key}.apply(${actualClassNode.nameWithoutPackage}, " +
                    "[${actualClassNode.nameWithoutPackage}].concat(Array.prototype.slice.call(arguments)));" +
                    "}", true)
        }
    }
}
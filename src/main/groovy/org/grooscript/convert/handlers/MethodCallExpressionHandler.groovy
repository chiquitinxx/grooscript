package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 26/01/14
 */
class MethodCallExpressionHandler extends BaseHandler {

    static final String SUPER_METHOD_BEGIN = 'super_'

    void handle(MethodCallExpression expression) {
        //println "MCE ${expression.objectExpression} - ${expression.methodAsString}"
        def addParameters = true
        String methodName = expression.methodAsString

        //Change println for javascript function
        if (methodName == 'println' || methodName == 'print') {
            out.addScript(GS_PRINTLN)
            //Remove call method call from closures
        } else if (methodName == 'call') {
            //println 'Calling!->'+expression.objectExpression
            addParameters = false
            if (expression.objectExpression instanceof VariableExpression) {
                def nameFunc = expression.objectExpression.text
                out.addScript("(${nameFunc}.delegate!=undefined?${GS_APPLY_DELEGATE}(${nameFunc},${nameFunc}.delegate,[")
                factory.visitNode(expression.arguments, false)
                out.addScript("]):${GS_EXECUTE_CALL}(${nameFunc}, ${GS_LIST}([")
                factory.visitNode(expression.arguments, false)
                out.addScript("])))")
            } else {
                out.addScript("${GS_EXECUTE_CALL}(")
                factory.visitNode(expression.objectExpression)
                out.addScript(", ${GS_LIST}([")
                factory.visitNode(expression.arguments, false)
                out.addScript(']))')
            }
            //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'super') {
            out.addScript("${SUPER_METHOD_BEGIN}${methodName}")
            //Function times, with a number, have to put (number) in javascript
        } else if (['times', 'upto', 'step'].contains(methodName) && expression.objectExpression instanceof ConstantExpression) {
            out.addScript('(')
            factory.visitNode(expression.objectExpression)
            out.addScript(')')
            out.addScript(".${methodName}")
            //With
        } else if (methodName == 'with' && expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpression(0) instanceof ClosureExpression) {
            factory.visitNode(expression.objectExpression)
            out.addScript(".${WITH}")
            //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Math') {
            out.addScript("Math.${methodName}")
            //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Class' &&
                methodName == 'forName') {
            out.addScript("${GS_CLASS_FOR_NAME}(")
            factory.visitNode(expression.arguments, false)
            if (expression.arguments[0] instanceof ConstantExpression) {
                out.addScript(", ${factory.reduceClassName(expression.arguments[0].text)}")
            }
            out.addScript(')')
            addParameters = false
            //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'this' && methodName == 'use') {
            ArgumentListExpression args = expression.arguments
            addParameters = false
            def nameCategory = args.expressions[0].type.nameWithoutPackage
            out.addScript("${GS_CATEGORY_USE}(\"${nameCategory}\",${nameCategory},")
            factory.visitNode(args.expressions[1])
            out.addScript(')')
            //Mixin Classes
        } else if (expression.objectExpression instanceof ClassExpression && methodName == 'mixin') {
            //println 'Mixin!'
            addParameters = false
            out.addScript("${GS_MIXIN_CLASS}('${expression.objectExpression.type.nameWithoutPackage}',")
            out.addScript('[')
            ArgumentListExpression args = expression.arguments
            out.addScript args.expressions.inject ([]) { item,expr->
                item << expr.type.nameWithoutPackage
            }.join(',')
            out.addScript('])')
            //Mixin Objects
        } else if (expression.objectExpression instanceof PropertyExpression &&
                expression.objectExpression.property instanceof ConstantExpression &&
                expression.objectExpression.property.text == 'metaClass' &&
                methodName == 'mixin') {
            addParameters = false
            out.addScript("${GS_MIXIN_OBJECT}(${expression.objectExpression.objectExpression.text},")
            out.addScript('[')
            ArgumentListExpression args = expression.arguments
            out.addScript args.expressions.inject ([]) { item,expr->
                item << expr.type.nameWithoutPackage
            }.join(',')
            out.addScript('])')
            //Spread method call [1,2,3]*.toString()
        } else if (expression.isSpreadSafe()) {
            //println 'spreadsafe!'
            addParameters = false
            factory.visitNode(expression.objectExpression)
            out.addScript(".collect(function(it) { return ${GS_METHOD_CALL}(it,'${methodName}',${GS_LIST}([")
            factory.visitNode(expression.arguments, false)
            out.addScript(']));})')
            //Call a method in this, method exist in main context
        } else if (factory.isThis(expression.objectExpression) &&
                context.firstVariableScopingHasMethod(methodName)) {
            out.addScript(methodName)
        //is function
        } else if (methodName == 'is') {
            out.addScript("${GS_IS}(")
            factory.visitNode(expression.objectExpression)
            out.addScript(',')
            factory.visitNode(expression.arguments, false)
            out.addScript(')')
            addParameters = false
            //Trait method
        } else if(factory.isTraitClass(expression.objectExpression.type.name) &&
                methodName.endsWith('$set')) {
            String nameProperty = methodName.substring(methodName.indexOf('__') + 2, methodName.lastIndexOf('$'))
            out.addScript("\$self.${nameProperty} = ")
            factory.visitNode(expression.arguments, false)
            addParameters = false
        } else if(factory.isTraitClass(expression.objectExpression.type.name) &&
                methodName.endsWith('$get')) {
            String nameProperty = methodName.substring(methodName.indexOf('__') + 2, methodName.lastIndexOf('$'))
            out.addScript("\$self.${nameProperty}")
            addParameters = false
        } else {
            //println 'Method->'+methodName+' - '+expression.arguments.class.simpleName + ' - ' + variableScoping
            addParameters = false

            out.addScript("${GS_METHOD_CALL}(")
            //Object
            if (factory.isThis(expression.objectExpression) &&
                    context.currentVariableScopingHasMethod(methodName)) {
                out.addScript(GS_OBJECT)
            } else {
                factory.visitNode(expression.objectExpression)
            }

            out.addScript(',')
            //MethodName
            putMethodName(expression)

            //Parameters
            out.addScript(",${GS_LIST}([")
            factory.visitNode(expression.arguments, false)
            out.addScript(']))')
        }

        if (addParameters) {
            factory.visitNode(expression.arguments)
        }
    }

    private putMethodName(MethodCallExpression expression) {
        factory.visitNode(expression.method)
    }
}

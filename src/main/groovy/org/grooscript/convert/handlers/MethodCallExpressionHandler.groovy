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

        //Change println for javascript function
        if (expression.methodAsString == 'println' || expression.methodAsString == 'print') {
            out.addScript(GS_PRINTLN)
            //Remove call method call from closures
        } else if (expression.methodAsString == 'call') {
            //println 'Calling!->'+expression.objectExpression

            if (expression.objectExpression instanceof VariableExpression) {
                addParameters = false
                def nameFunc = expression.objectExpression.text
                out.addScript("(${nameFunc}.delegate!=undefined?${GS_APPLY_DELEGATE}(${nameFunc},${nameFunc}.delegate,[")
                factory.visitNode(expression.arguments, false)
                out.addScript("]):${nameFunc}")
                factory.visitNode(expression.arguments)
                out.addScript(")")
            } else {
                factory.visitNode(expression.objectExpression)
            }
            //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'super') {
            out.addScript("${SUPER_METHOD_BEGIN}${expression.methodAsString}")
            //Function times, with a number, have to put (number) in javascript
        } else if (['times', 'upto', 'step'].contains(expression.methodAsString) && expression.objectExpression instanceof ConstantExpression) {
            out.addScript('(')
            factory.visitNode(expression.objectExpression)
            out.addScript(')')
            out.addScript(".${expression.methodAsString}")
            //With
        } else if (expression.methodAsString == 'with' && expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpression(0) instanceof ClosureExpression) {
            factory.visitNode(expression.objectExpression)
            out.addScript(".${WITH}")
            //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Math') {
            out.addScript("Math.${expression.methodAsString}")
            //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Class' &&
                expression.methodAsString=='forName') {
            out.addScript("${GS_CLASS_FOR_NAME}(")
            factory.visitNode(expression.arguments, false)
            out.addScript(')')
            addParameters = false
            //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'this' && expression.methodAsString == 'use') {
            ArgumentListExpression args = expression.arguments
            addParameters = false
            out.addScript("${GS_CATEGORY_USE}(\"")
            out.addScript(args.expressions[0].type.nameWithoutPackage)
            out.addScript('",')
            factory.visitNode(args.expressions[1])
            out.addScript(')')
            //Mixin Classes
        } else if (expression.objectExpression instanceof ClassExpression && expression.methodAsString == 'mixin') {
            //println 'Mixin!'
            addParameters = false
            out.addScript("${GS_MIXIN_CLASS}('${expression.objectExpression.type.nameWithoutPackage}',")
            out.addScript('[')
            ArgumentListExpression args = expression.arguments
            out.addScript args.expressions.inject ([]) { item,expr->
                item << '"'+expr.type.nameWithoutPackage+'"'
            }.join(',')
            out.addScript('])')
            //Mixin Objects
        } else if (expression.objectExpression instanceof PropertyExpression &&
                expression.objectExpression.property instanceof ConstantExpression &&
                expression.objectExpression.property.text == 'metaClass' &&
                expression.methodAsString == 'mixin') {
            addParameters = false
            out.addScript("${GS_MIXIN_OBJECT}(${expression.objectExpression.objectExpression.text},")
            out.addScript('[')
            ArgumentListExpression args = expression.arguments
            out.addScript args.expressions.inject ([]) { item,expr->
                item << '"'+expr.type.nameWithoutPackage+'"'
            }.join(',')
            out.addScript('])')
            //Spread method call [1,2,3]*.toString()
        } else if (expression.isSpreadSafe()) {
            //println 'spreadsafe!'
            addParameters = false
            factory.visitNode(expression.objectExpression)
            out.addScript(".collect(function(it) { return ${GS_METHOD_CALL}(it,'${expression.methodAsString}',${GS_LIST}([")
            factory.visitNode(expression.arguments, false)
            out.addScript(']));})')
            //Call a method in this, method exist in main context
        } else if (isThis(expression.objectExpression) &&
                context.firstVariableScopingHasMethod(expression.methodAsString)) {
            out.addScript(expression.methodAsString)
        } else {

            //println 'Method->'+expression.methodAsString+' - '+expression.arguments.class.simpleName + ' - ' + variableScoping
            addParameters = false

            out.addScript("${GS_METHOD_CALL}(")
            //Object
            if (isThis(expression.objectExpression) &&
                    context.variableScoping.peek()?.contains(expression.methodAsString)) {
                out.addScript(GS_OBJECT)
            } else {
                factory.visitNode(expression.objectExpression)
            }

            out.addScript(',')
            //MethodName
            factory.visitNode(expression.method)

            //Parameters
            out.addScript(",${GS_LIST}([")
            factory.visitNode(expression.arguments, false)
            out.addScript(']))')
        }

        if (addParameters) {
            factory.visitNode(expression.arguments)
        }
    }

    private isThis(expression) {
        expression instanceof VariableExpression && expression.name == 'this'
    }
}

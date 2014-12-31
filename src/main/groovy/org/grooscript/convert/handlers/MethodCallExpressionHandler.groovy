package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.SpreadExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
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
        String methodName = expression.methodAsString

        //Change println for javascript function
        if (methodName == 'println' || methodName == 'print') {
            out.addScript(GS_PRINTLN)
            addParameters(expression)
        //Remove call method call from closures
        } else if (methodName == 'call') {
            //println 'Calling!->'+expression.objectExpression
            if (expression.objectExpression instanceof VariableExpression) {
                def nameFunc = expression.objectExpression.text
                out.addScript("(${nameFunc}.delegate!=undefined?${GS_APPLY_DELEGATE}(${nameFunc},${nameFunc}.delegate,[")
                conversionFactory.visitNode(expression.arguments, false)
                out.addScript("]):${GS_EXECUTE_CALL}(${nameFunc}, this, [")
                conversionFactory.visitNode(expression.arguments, false)
                out.addScript("]))")
            } else {
                out.addScript("${GS_EXECUTE_CALL}(")
                conversionFactory.visitNode(expression.objectExpression)
                out.addScript(", this, [")
                conversionFactory.visitNode(expression.arguments, false)
                out.addScript('])')
            }
        //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'super') {
            out.addScript("${SUPER_METHOD_BEGIN}${methodName}")
            addParameters(expression)
        //Function times, with a number, have to put (number) in javascript
        } else if (['times', 'upto', 'step'].contains(methodName) && expression.objectExpression instanceof ConstantExpression) {
            out.addScript('(')
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(')')
            out.addScript(".${methodName}")
            addParameters(expression)
        //With
        } else if (methodName == 'with' && expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpression(0) instanceof ClosureExpression) {
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".${WITH}")
            context.insideWith = true
            addParameters(expression)
            context.insideWith = false
        //Equals
        } else if (methodName == 'equals') {
            out.addScript("${GS_EQUALS}(")
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(',')
            conversionFactory.visitNode(expression.arguments.getExpression(0))
            out.addScript(')')
        //WithTraits
        } else if (methodName == 'withTraits' && expression.arguments instanceof ArgumentListExpression) {
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".${WITH_TRAITS}")
            addParameters(expression)
        //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Math') {
            out.addScript("Math.${methodName}")
            addParameters(expression)
        //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression &&
                expression.objectExpression.type.name == 'java.lang.Class' &&
                methodName == 'forName') {
            out.addScript("${GS_CLASS_FOR_NAME}(")
            conversionFactory.visitNode(expression.arguments, false)
            if (expression.arguments[0] instanceof ConstantExpression) {
                out.addScript(", ${conversionFactory.reduceClassName(expression.arguments[0].text)}")
            }
            out.addScript(')')
        //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name == 'this' && methodName == 'use') {
            ArgumentListExpression args = expression.arguments
            def nameCategory = args.expressions[0].type.nameWithoutPackage
            out.addScript("${GS_CATEGORY_USE}(\"${nameCategory}\",${nameCategory},")
            conversionFactory.visitNode(args.expressions[1])
            out.addScript(')')
        //Mixin Classes
        } else if (expression.objectExpression instanceof ClassExpression && methodName == 'mixin') {
            //println 'Mixin!'
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
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(".collect(function(it) { return ${GS_METHOD_CALL}(it,'${methodName}',[")
            conversionFactory.visitNode(expression.arguments, false)
            out.addScript(']);})')
        //Call a method in this, method exist in main context
        } else if (conversionFactory.isThis(expression.objectExpression) &&
                context.firstVariableScopingHasMethod(methodName)) {
            out.addScript(methodName)
            addParameters(expression)
        //is function
        } else if (methodName == 'is') {
            out.addScript("${GS_IS}(")
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(',')
            conversionFactory.visitNode(expression.arguments, false)
            out.addScript(')')
        //Trait set method
        } else if(conversionFactory.isTraitClass(expression.objectExpression.type.name) &&
                methodName.endsWith('$set')) {
            out.addScript("\$self.${getNameTraitProperty(methodName)} = ")
            conversionFactory.visitNode(expression.arguments, false)
        //Trait get method
        } else if(conversionFactory.isTraitClass(expression.objectExpression.type.name) &&
                methodName.endsWith('$get')) {
            out.addScript("\$self.${getNameTraitProperty(methodName)}")
        //Trait get static property
        } else if(expression.objectExpression instanceof TernaryExpression &&
                expression.objectExpression.booleanExpression.expression instanceof BinaryExpression &&
                methodName?.endsWith('$get') &&
                expression.objectExpression.booleanExpression.expression.leftExpression instanceof VariableExpression &&
                expression.objectExpression.booleanExpression.expression.leftExpression.variable == '$static$self'
        ) {
                //traits_Methods__ONE$get
                out.addScript("${GS_GET_PROPERTY}(\$static\$self,'")
                out.addScript(methodName.substring(methodName.lastIndexOf('__') + 2, methodName.size() - 4))
                out.addScript('\')')
        //Trait set static property
        } else if(methodName?.endsWith('$set') && expression.objectExpression instanceof VariableExpression &&
            expression.objectExpression.variable == '$static$self') {
            //traits_Methods__ONE$set
            out.addScript("${GS_SET_PROPERTY}(\$static\$self,'")
            out.addScript(methodName.substring(methodName.lastIndexOf('__') + 2, methodName.size() - 4))
            out.addScript('\',')
            conversionFactory.visitNode(expression.arguments, false)
            out.addScript(')')
        //Static method
        } else if(isStaticMethodCall(expression)) {
            out.addScript("$GS_EXEC_STATIC(")
            conversionFactory.visitNode(expression.objectExpression)
            out.addScript(",'$methodName', this,[")
            conversionFactory.visitNode(expression.arguments, false)
            out.addScript('])')
        } else {
            //println 'Method->'+methodName+' - '+expression.arguments.class.simpleName
            doFullMethodCall(methodName, expression)
        }
    }

    private addParameters(expression) {
        conversionFactory.visitNode(expression.arguments)
    }

    private putMethodName(MethodCallExpression expression) {
        conversionFactory.visitNode(expression.method)
    }

    private getNameTraitProperty(methodName) {
        methodName.substring(0, methodName.lastIndexOf('$'))
    }

    private boolean isStaticMethodCall(MethodCallExpression expression) {
        boolean staticMethod = false
        if (expression.objectExpression instanceof ClassExpression) {
            ClassNode sourceObject = expression.objectExpression.type
            def method = sourceObject.methods.find { it.name == expression.methodAsString}
            if (method && method.isStatic()) {
                staticMethod = true
            }
        }
        staticMethod
    }

    private doFullMethodCall(methodName, expression) {
        out.addScript("${GS_METHOD_CALL}(")
        //Object
        if (conversionFactory.isThis(expression.objectExpression) &&
                context.currentVariableScopingHasMethod(methodName)) {
            out.addScript(GS_OBJECT)
        } else {
            if (conversionFactory.isThis(expression.objectExpression) && context.staticProcessNode) {
                out.addScript(context.staticProcessNode.nameWithoutPackage)
            } else {
                conversionFactory.visitNode(expression.objectExpression)
            }
        }

        out.addScript(',')
        //MethodName
        putMethodName(expression)

        //Parameters
        out.addScript(",")
        if (expression.arguments.expressions.size() == 1 &&
                expression.arguments.expressions.first() instanceof SpreadExpression) {
            conversionFactory.visitNode(expression.arguments.expressions.first().expression)
        } else {
            out.addScript("[")
            conversionFactory.visitNode(expression.arguments, false)
            out.addScript("]")
        }
        if (conversionFactory.isThis(expression.objectExpression) && !context.mainContext &&
                context.insideClass && !context.currentVariableScopingHasMethod(methodName) &&
                !context.staticProcessNode) {
            out.addScript(", ${GS_OBJECT}")
        }
        out.addScript(')')
    }
}

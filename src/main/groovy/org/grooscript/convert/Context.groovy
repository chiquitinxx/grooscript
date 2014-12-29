package org.grooscript.convert

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.VariableExpression
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class Context {

    //Class names stacks
    Stack<String> classNameStack = new Stack<String>()
    Stack<String> superNameStack = new Stack<String>()
    //Use for variable scoping, for class variable names and function names mainly
    Stack variableScoping = new Stack()
    Stack variableStaticScoping = new Stack()
    Stack returnScoping = new Stack()
    //Use por function variable names
    Stack actualScope = new Stack()
    boolean processingClosure = false
    boolean processingClassMethods = false
    boolean processingBaseScript = false
    boolean lookingForReturnStatementInIf = false
    ClassNode staticProcessNode

    private Stack traitFieldsScoping = new Stack()

    //Control switch inside switch
    def switchCount = 0
    def addClosureSwitchInitialization = false

    def insideWith = false

    //Prefix and postfix for variables without clear scope
    def prefixOperator = '', postfixOperator = ''

    //Where code of native functions stored, as a map. Used for GsNative annotation
    List<NativeFunction> nativeFunctions

    String currentClassMethodConverting = null

    Context() {
        variableScoping.clear()
        variableScoping.push([])
        variableStaticScoping.clear()
        variableStaticScoping.push([])
        actualScope.clear()
        actualScope.push([])
        clearTraitFieldsScoping()
    }

    def addToActualScope(variableName) {
        if (!actualScope.isEmpty()) {
            actualScope.peek().add(variableName)
        }
    }

    def variableScopingContains(variableName) {
        tourStack(variableScoping, variableName)
    }

    def allActualScopeContains(variableName) {
        tourStack(actualScope, variableName)
    }

    boolean firstVariableScopingHasMethod(String methodName) {
        (variableScoping && variableScoping.peek() == variableScoping.firstElement() &&
                variableScoping.peek().contains(methodName)) ||
            (actualScope && actualScope.firstElement().contains(methodName))
    }

    boolean currentVariableScopingHasMethod(String methodName) {
        variableScoping && variableScoping.peek()?.contains(methodName)
    }

    boolean isVariableWithMissingScope(VariableExpression expression) {
        !expression.isThisExpression() && !allActualScopeContains(expression.name) &&
                !variableScopingContains(expression.name) &&
                (processingClosure || processingClassMethods || processingBaseScript)
    }

    String getNativeFunction(ClassNode classNode, String methodName) {
        def nativeFunctionsWithClassName = nativeFunctions.findAll {
            it.className == classNode.nameWithoutPackage && it.methodName == methodName}
        if (nativeFunctionsWithClassName.size() == 1) {
            return nativeFunctionsWithClassName.first().code
        } else {
            def natives = nativeFunctions.findAll {
                it.methodName == methodName
            }
            if (natives.size() == 1) {
                return natives.first().code
            } else if (natives.size() > 1) {
                return natives.first().code
            } else {
                GsConsole.error("Don't find unique native code for method: ${methodName} in class: ${classNode.name}")
                return ''
            }
        }
    }

    boolean isMainContext() {
        variableScoping.peek().is(variableScoping.firstElement())
    }

    boolean isInsideClass() {
        classNameStack.peek().is(classNameStack.firstElement())
    }

    void clearTraitFieldsScoping() {
        traitFieldsScoping.clear()
    }

    void addToTraitFieldsScoping(String name) {
        if (traitFieldsScoping.isEmpty()) {
            traitFieldsScoping.push([])
        }
        traitFieldsScoping.peek() << name
    }

    boolean traitFieldScopeContains(String name) {
        !traitFieldsScoping.isEmpty() && traitFieldsScoping.peek().contains(name)
    }

    String findTraitScopeByName(String name) {
        traitFieldsScoping.peek().find { String nameField ->
            nameField == name || (nameField[0].toLowerCase() + nameField.substring(1)) == name
        }
    }

    private tourStack(Stack stack,variableName) {
        if (stack.isEmpty()) {
            return false
        } else if (stack.peek()?.contains(variableName)) {
            return true
        } else {
            //println 'going stack->'+stack.peek()
            def keep = stack.pop()
            def result = tourStack(stack, variableName)
            stack.push(keep)
            return result
        }
    }
}

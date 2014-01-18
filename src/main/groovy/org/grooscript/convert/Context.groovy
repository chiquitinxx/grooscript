package org.grooscript.convert

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
    boolean lookingForReturnStatementInIf = false

    def inheritedVariables = [:]

    //Control switch inside switch
    def switchCount = 0
    def addClosureSwitchInitialization = false

    def addToActualScope(variableName) {
        if (!actualScope.isEmpty()) {
            actualScope.peek().add(variableName)
        }
    }

    def actualScopeContains(variableName) {
        if (!actualScope.isEmpty()) {
            return actualScope.peek().contains(variableName)
        } else {
            return false
        }
    }

    private variableScopingContains(variableName) {
        tourStack(variableScoping, variableName)
    }

    private allActualScopeContains(variableName) {
        tourStack(actualScope, variableName)
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

    private firstVariableScopingHasMethod(methodName) {
        variableScoping && variableScoping.peek() == variableScoping.firstElement() &&
                variableScoping.peek().contains(methodName)
    }
}

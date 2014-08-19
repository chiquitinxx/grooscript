package org.grooscript.convert

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.grooscript.convert.handlers.*
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static org.grooscript.JsNames.*

/**
 * User: jorgefrancoleza
 * Date: 16/01/14
 */
class ConversionFactory {

    def conversionClasses = [:]
    Context context
    Out out
    Functions functions
    def converter
    Traits traits

    Map converters = [
        'VariableExpression': VariableExpressionHandler,
        'ClassNode': ClassNodeHandler,
        'BinaryExpression': BinaryExpressionHandler,
        'MethodCallExpression': MethodCallExpressionHandler,
        'PropertyExpression': PropertyExpressionHandler,
        'BlockStatement': BlockStatementHandler,
        'MethodNode': MethodNodeHandler,
        'ConstructorCallExpression': ConstructorCallExpressionHandler,
        'CastExpression': CastExpressionHandler,
        'ArrayExpression': ArrayExpressionHandler,
        'MethodPointerExpression': MethodPointerExpressionHandler,
        'InnerClassNode': InnerClassNodeHandler,
        'DeclarationExpression': DeclarationExpressionHandler,
        'ForStatement': ForStatementHandler
    ]

    ConversionFactory() {
        context = new Context()
        out = new Out()
        traits = new Traits()
        functions = new Functions(conversionFactory: this)
    }

    void convert(ASTNode node, otherParam = null) {
        if (!context || !out) {
            throw new GrooScriptException('Need to define context and out in ConversionFactory.')
        }
        visitNode(node, otherParam)
    }

    BaseHandler getConverter(String className) {
        if (!conversionClasses[className]) {
            conversionClasses[className] =
                    improvedConversionHandler(className)
        }
        conversionClasses[className]
    }

    void visitNode(node, otherParam = null) {
        String className = node.class.simpleName
        if (!converters[className]) {
            if (otherParam != null) {
                converter."process${className}"(node, otherParam)
            } else {
                converter."process${className}"(node)
            }
        } else {
            if (otherParam) {
                getConverter(className).handle(node, otherParam)
            } else {
                getConverter(className).handle(node)
            }
        }
    }

    void handExpressionInBoolean(expression) {
        if (expression instanceof VariableExpression || expression instanceof PropertyExpression ||
                (expression instanceof NotExpression && expression.expression &&
                    (expression.expression instanceof VariableExpression || expression.expression instanceof PropertyExpression))) {
            if (expression instanceof NotExpression) {
                out.addScript("!${GS_BOOL}(")
                visitNode(expression.expression)
            } else {
                out.addScript("${GS_BOOL}(")
                visitNode(expression)
            }
            out.addScript(')')
        } else {
            visitNode(expression)
        }
    }

    void processObjectExpressionFromProperty(PropertyExpression expression) {
        if (expression.objectExpression instanceof ClassExpression) {
            out.addScript(expression.objectExpression.type.nameWithoutPackage)
        } else {
            visitNode(expression.objectExpression)
        }
    }

    void processPropertyExpressionFromProperty(PropertyExpression expression) {
        if (expression.property instanceof GStringExpression) {
            visitNode(expression.property)
        } else {
            out.addScript('"')
            visitNode(expression.property, false)
            out.addScript('"')
        }
    }

    boolean statementThatCanReturn(statement) {
        return !(statement instanceof ReturnStatement) &&
                !(statement instanceof IfStatement) && !(statement instanceof WhileStatement) &&
                !(statement instanceof AssertStatement) && !(statement instanceof BreakStatement) &&
                !(statement instanceof CaseStatement) && !(statement instanceof CatchStatement) &&
                !(statement instanceof ContinueStatement) && !(statement instanceof DoWhileStatement) &&
                !(statement instanceof ForStatement) && !(statement instanceof SwitchStatement) &&
                !(statement instanceof ThrowStatement) && !(statement instanceof TryCatchStatement) &&
                !(statement.metaClass.expression && statement.expression instanceof DeclarationExpression)
    }

    boolean isThis(expression) {
        expression instanceof VariableExpression && expression.name == 'this'
    }

    private Object improvedConversionHandler(String className) {
        BaseHandler instanceHandler = converters[className].newInstance()
        instanceHandler.out = out
        instanceHandler.context = context
        instanceHandler.traits = traits
        instanceHandler.functions = functions
        instanceHandler.factory = this
        instanceHandler
    }

    boolean isTraitClass(String name) {
        name.contains('$Trait$')
    }

    String reduceClassName(String name) {
        def result = name
        def i = result.lastIndexOf('.')
        if (i > 0) {
            result = result.substring(i + 1)
        }
        result
    }
}

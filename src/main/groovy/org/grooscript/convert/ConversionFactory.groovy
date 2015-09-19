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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.grooscript.convert.handlers.*
import org.grooscript.util.GrooScriptException

import static org.grooscript.JsNames.*

class ConversionFactory {

    def conversionClasses = [:]
    Context context
    Out out
    Functions functions
    GsConverter converter
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
        'ForStatement': ForStatementHandler,
        'ConstructorNode': ConstructorNodeHandler,
        'AssertStatement': AssertStatementHandler,
        'BooleanExpression': BooleanExpressionHandler,
        'ConstantExpression': ConstantExpressionHandler,
        'ExpressionStatement': ExpressionStatementHandler,
        'UnaryMinusExpression': UnaryMinusExpressionHandler,
        'UnaryPlusExpression': UnaryPlusExpressionHandler,
        'TryCatchStatement': TryCatchStatementHandler,
        'StaticMethodCallExpression': StaticMethodCallExpressionHandler,
        'SwitchStatement': SwitchStatementHandler,
        'EmptyStatement': EmptyStatementHandler,
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
        //println 'Visiting:' + node
        if (!converters[className]) {
            if (otherParam != null) {
                converter."process${className}"(node, otherParam)
            } else {
                converter."process${className}"(node)
            }
        } else {
            if (otherParam != null) {
                getConverter(className).handle(node, otherParam)
            } else {
                getConverter(className).handle(node)
            }
        }
    }

    void handExpressionInBoolean(expression) {
        if (expression instanceof VariableExpression || expression instanceof PropertyExpression ||
                expression instanceof ConstructorCallExpression || expression instanceof NotExpression) {
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

    void processKnownPropertyExpression(PropertyExpression expression) {
        processObjectExpressionFromProperty(expression)
        out.addScript('[')
        processPropertyExpressionFromProperty(expression)
        out.addScript(']')
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
        instanceHandler.conversionFactory = this
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

    void outFirstArgument(expression) {
        out.addScript(expression.arguments.expressions.first().value)
    }
}

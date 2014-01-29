package org.grooscript.convert.handlers

import org.codehaus.groovy.ast.expr.ConstructorCallExpression

import static org.grooscript.JsNames.*
/**
 * User: jorgefrancoleza
 * Date: 29/01/14
 */
class ConstructorCallExpressionHandler extends BaseHandler {

    static final NUMBERS = ['java.math.BigDecimal', 'java.lang.Float', 'java.lang.Double', 'java.lang.Integer']

    void handle(ConstructorCallExpression expression) {
        //Super expression in constructor is allowed
        if (expression?.isSuperCall()) {
            def name = context.superNameStack.peek()
            //println 'processNotExpression name->'+name
            if (name == 'java.lang.Object') {
                out.addScript("this.${CONSTRUCTOR}")
            } else {
                out.addScript("this.${name}${expression.arguments.expressions.size()}")
            }
        } else if (expression.type.name=='java.lang.String') {
            if (expression.arguments.getExpressions().size() > 0) {
                factory.visitNode(expression.arguments)
            } else {
                out.addScript("''")
            }
            return
        } else if (expression.type.name=='java.util.Date') {
            out.addScript(GS_DATE)
        } else if (expression.type.name=='groovy.util.Expando') {
            out.addScript(GS_EXPANDO)
        } else if (expression.type.name=='java.util.Random') {
            out.addScript(GS_RANDOM)
        } else if (expression.type.name=='java.util.HashSet') {
            out.addScript(GS_SET)
        } else if (expression.type.name=='java.util.HashMap') {
            out.addScript(GS_MAP)
        } else if (expression.type.name=='java.util.ArrayList') {
            out.addScript(GS_LIST)
        } else if (expression.type.name in NUMBERS) {
            out.addScript(GS_TO_NUMBER)
        } else if (expression.type.name=='groovy.lang.ExpandoMetaClass') {
            out.addScript(GS_EXPANDO_META_CLASS)
        } else if (expression.type.name=='java.lang.StringBuffer') {
            out.addScript(GS_STRING_BUFFER)
        } else {
            if (expression.type.name.startsWith('java.') || expression.type.name.startsWith('groovy.util.')) {
                throw new Exception('Not support type '+expression.type.name)
            }
            //Constructor have name with number of params on it
            def name = expression.type.nameWithoutPackage
            out.addScript(name)
        }
        factory.visitNode(expression.arguments)
    }
}

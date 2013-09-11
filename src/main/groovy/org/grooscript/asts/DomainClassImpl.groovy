package org.grooscript.asts

import org.codehaus.groovy.ast.*

/**
 * User: jorgefrancoleza
 * Date: 28/01/13
 */
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import java.lang.reflect.Modifier

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class DomainClassImpl implements ASTTransformation {

    static final NOT_PROPERTY_NAMES = ['transients', 'constraints', 'mapping', 'hasMany', 'belongsTo']

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        //TODO
        if (!nodes[0] instanceof AnnotationNode ||
            !nodes[1] instanceof ClassNode) {
            throw new RuntimeException('Fail!')
        }

        ClassNode theClass = (ClassNode) nodes[1]
        //If 'id' property exist we do nothing
        if (theClass.hasProperty('id')) {
            return
        }

        //Get properties for the class and load into listColumns
        ListExpression list = new ListExpression([])
        theClass.properties.each { PropertyNode pn ->
            if (!(pn.name in NOT_PROPERTY_NAMES)) {
                List<MapEntryExpression> listColumnProperties = new ArrayList<MapEntryExpression>()
                listColumnProperties << new MapEntryExpression(new ConstantExpression('name'),new ConstantExpression(pn.name))
                listColumnProperties << new MapEntryExpression(new ConstantExpression('type'),new ConstantExpression(pn.type.name))
                //Let's find constraints
                def constraints = []
                if (theClass.properties.find {it.name=='constraints'}) {
                    try {
                        ClosureExpression clo = theClass.properties.find {it.name=='constraints'}.initialExpression
                        BlockStatement blo = clo.code
                        blo.statements.each { Statement st ->
                            if (st instanceof ExpressionStatement && st.expression instanceof MethodCallExpression) {
                                MethodCallExpression mce = st.expression
                                if (mce.methodAsString==pn.name) {
                                    //We got constraints of this property
                                    NamedArgumentListExpression nameds = mce.arguments.expressions[0]
                                    nameds.mapEntryExpressions.each { MapEntryExpression mpe ->
                                        constraints << mpe
                                    }
                                }
                            }
                        }
                    } catch (e) {
                        constraints = [fail:'fail']
                    }
                }

                listColumnProperties << new MapEntryExpression(new ConstantExpression('constraints'), new MapExpression(constraints))

                def map = new NamedArgumentListExpression(listColumnProperties)
                list.addExpression(new ConstructorCallExpression(new ClassNode(Expando), new TupleExpression(map)))
            }
        }

        //Remove properties not allowed
        theClass.properties.removeAll { it.name in NOT_PROPERTY_NAMES }
        theClass.fields.removeAll { it.name in NOT_PROPERTY_NAMES }

        theClass.addProperty('listColumns', Modifier.STATIC , new ClassNode(ArrayList), list, null, null)
        theClass.addProperty('listItems', Modifier.STATIC , new ClassNode(ArrayList),
                new ListExpression([]), null, null)
        theClass.addProperty('mapTransactions', Modifier.STATIC , new ClassNode(HashMap),
                new MapExpression([]), null, null)
        theClass.addProperty('dataHandler', Modifier.STATIC , new ClassNode(Object),null,null,null)
        theClass.addProperty('lastId', Modifier.STATIC, ClassHelper.Long_TYPE,new ConstantExpression(0),null,null)

        //Instance variables
        theClass.addProperty('id', Modifier.PUBLIC, ClassHelper.Long_TYPE,null,null,null)
        theClass.addProperty('errors', Modifier.PUBLIC, new ClassNode(HashMap), new MapExpression([]), null, null)
        theClass.addProperty('version', Modifier.PUBLIC, ClassHelper.Long_TYPE,new ConstantExpression(0),null,null)

        theClass.addMethod('clientValidations', Modifier.PUBLIC, ClassHelper.Boolean_TYPE, Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            def result = true
            def item = this
            errors = [:]
            listColumns.each { field ->
                if (field.constraints) {
                    if (field.constraints['blank']==false && !item."${field.name}") {
                        errors.put(field.name,'blank validation on value '+item."${field.name}")
                        result = false
                    }
                }
            }
            return result
        }[0])

        theClass.addMethod('validate',Modifier.PUBLIC,ClassHelper.Boolean_TYPE,Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode {
            return clientValidations()
        }[0])

        //hasErrors()
        theClass.addMethod('hasErrors',Modifier.PUBLIC, ClassHelper.boolean_TYPE,Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromSpec {
            block {
                returnStatement {
                    variable 'errors'
                }
            }
        }[0])

        //list()
        theClass.addMethod('list',Modifier.STATIC,new ClassNode(ArrayList),Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromSpec {
            block {
                returnStatement {
                    variable 'listItems'
                }
            }
        }[0])

        //count()
        theClass.addMethod('count',Modifier.STATIC,ClassHelper.int_TYPE,Parameter.EMPTY_ARRAY,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromSpec {
            block {
                returnStatement {
                    methodCall {
                        variable 'listItems'
                        constant 'size'
                        argumentList ()
                    }
                }
            }
        }[0])

        //processDataHandlerError(data)
        Parameter[] params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap),'data')
        theClass.addMethod('processDataHandlerError',Modifier.STATIC,null, params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            if (mapTransactions[data.number]) {
                if (mapTransactions[data.number].onError) {
                    mapTransactions[data.number].onError.call()
                }
                mapTransactions.remove(data.number)
            }
        }[0])

        //processDataHandlerSuccess(data)
        params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap),'data')
        theClass.addMethod('processDataHandlerSuccess',Modifier.STATIC,null, params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            if (mapTransactions[data.number]) {
                def item = mapTransactions[data.number].item
                data.item.each { key,value ->
                    item."${key}" = value
                }
                if (data.action=='insert') {
                    listItems << item
                }
                if (data.action=='delete') {
                    listItems = listItems - item
                }
                if (mapTransactions[data.number].onOk) {
                    mapTransactions[data.number].onOk.call(data)
                }
                mapTransactions.remove(data.number)
                processChanges(data)
            }
        }[0])

        //processPublishMessage(data)
        params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap),'data')
        theClass.addMethod('processPublishMessage',Modifier.STATIC,null, params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            if (data.action == 'list') {
                processOnServerList(data)
            } else {
                def item
                if (data.action == 'insert') {
                    item = Class.forName(data.model).newInstance()
                } else {
                    item = get(data.item.id)
                }

                if (data.item) {
                    data.item.each { key, value ->
                        item."${key}" = value
                    }
                }

                if (data.action=='insert') {
                    listItems << item
                }
            }
            processChanges(data)
        }[0])

        //processOnServerList(data)
        params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap),'data')
        theClass.addMethod('processOnServerList',Modifier.STATIC,null, params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            listItems = []
            def dataArrived = data
            if (data.items) {
                data.items.each { row ->
                    def item = Class.forName(dataArrived.model).newInstance()
                    row.each { key, value ->
                        item."${key}" = value
                    }
                    listItems << item
                }
            }
            processChanges(data)
        }[0])


        //get(id)
        params = new Parameter[1]
        params[0] = new Parameter(ClassHelper.long_TYPE,'value')
        theClass.addMethod('get',Modifier.STATIC,null, params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            def number = value
            def item = listItems.find { it.id == number}
            return item
        }[0])

        //Save method
        params = new Parameter[2]
        params[0] = new Parameter(new ClassNode(Closure),'onOk',new ConstantExpression(null))
        params[1] = new Parameter(new ClassNode(Closure),'onError',new ConstantExpression(null))
        theClass.addMethod('save', Modifier.PUBLIC, ClassHelper.Long_TYPE, params,
                ClassNode.EMPTY_ARRAY, new AstBuilder().buildFromCode { //onOk = null, onError = null ->

            if (!this.clientValidations()) {
                return 0
            } else {
                //If we have dataHandler
                if (dataHandler) {
                    def numberTransaction
                    if (!this.id) {
                        numberTransaction = dataHandler.insert(this.class.name, this)
                    } else { //An update
                        numberTransaction = dataHandler.update(this.class.name, this)
                    }
                    def transaction = [item:this,onOk:onOk,onError:onError]
                    mapTransactions.put(numberTransaction,transaction)
                    return numberTransaction

                } else {
                    //Without dataHandler
                    //Insert
                    if (!this.id) {
                        //numberTransaction = item.class.getDataHandler().insert(cl.name, item)
                        this.id = ++lastId
                        listItems << this
                        processChanges([action:'insert',item:this])
                    } else { //An update
                        //Nothing to do?? :o
                        processChanges([action:'update',item:this])
                    }
                    return this.id
                }
            }
        }[0])

        //Delete method
        params = new Parameter[2]
        params[0] = new Parameter(new ClassNode(Closure),'onOk',new ConstantExpression(null))
        params[1] = new Parameter(new ClassNode(Closure),'onError',new ConstantExpression(null))
        theClass.addMethod('delete', Modifier.PUBLIC, ClassHelper.Long_TYPE, params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {

            if (this.id) {
                //If we have dataHandler
                if (dataHandler) {
                    def numberTransaction = dataHandler.delete(this.class.name, this)

                    def transaction = [item:this,onOk:onOk,onError:onError]
                    mapTransactions.put(numberTransaction,transaction)
                    return numberTransaction

                } else {
                    listItems = listItems - this
                    processChanges([action:'delete',item:this])
                    return this.id
                }
            } else {
                throw new Exception('Deleting not saved object')
            }
        }[0])

        //Change Listeners
        theClass.addProperty('changeListeners', Modifier.STATIC , new ClassNode(ArrayList),
                new ListExpression([]), null, null)

        params = new Parameter[1]
        params[0] = new Parameter(new ClassNode(HashMap),'data')
        theClass.addMethod('processChanges',Modifier.STATIC,null,params,
                ClassNode.EMPTY_ARRAY,new AstBuilder().buildFromCode {
            def actionData = data

            if (changeListeners) {
                changeListeners.each {
                    it.call(actionData)
                }
            }
        }[0])

    }
}
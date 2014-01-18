package org.grooscript.convert

import static org.grooscript.JsNames.*

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.*
import org.grooscript.util.GsConsole
import org.grooscript.util.Util

/**
 * JFL 27/08/12
 */
class GsConverter {

    static final String SUPER_METHOD_BEGIN = 'super_'

    Context context = new Context()
    Out out = new Out()

    //Where code of native functions stored, as a map. Used for GsNative annotation
    def nativeFunctions

    //Adds a console info if activated
    def consoleInfo = false

    //Conversion Options
    def convertDependencies = true
    Closure customization = null
    def classPath = null

    //Prefix and postfix for variables without clear scope
    def prefixOperator = '', postfixOperator = ''

    /**
     * Converts Groovy script to Javascript
     * @param String script in groovy
     * @return String script in javascript
     */
    def toJs(String script) {
        def result
        //Script not empty plz!
        def phase = 0
        if (script) {

            try {

                nativeFunctions = Util.getNativeFunctions(script)

                if (consoleInfo) {
                    GsConsole.message('Getting ast from code...')
                }
                def astList = new AstTreeGenerator(consoleInfo: consoleInfo, convertDependencies: convertDependencies,
                        classPath: classPath, customization: customization).fromText(script)

                if (consoleInfo) {
                    GsConsole.message('Processing AST...')
                }

                phase++
                result = processAstListToJs(astList)

                if (consoleInfo) {
                    GsConsole.message('Code processed.')
                }
            } catch (e) {
                GsConsole.error('Error getting AST from script: ' + e.message)
                if (phase==0) {
                    throw new Exception("Compiler ERROR on Script -" + e.message)
                } else {
                    throw new Exception("Compiler END ERROR on Script -" + e.message)
                }
            }
        }
        result
    }

    /**
     * Process an AST List from Groovy code to javascript script
     * @param list
     * @return
     */
    def processAstListToJs(list) {
        def result
        out.indent = 0
        out.resultScript = ''
        if (list && list.size() > 0) {
            //println '-----------------Size('+list.size+')->'+list
            context.variableScoping.clear()
            context.variableScoping.push([])
            context.variableStaticScoping.clear()
            context.variableStaticScoping.push([])
            context.actualScope.clear()
            context.actualScope.push([])
            //Store all methods here
            def methodList = []
            //Store all classes here
            def classList = []
            //We process blocks at the end
            def listBlocks = []
            list.each { it ->
                //println '------------------------------------it->'+it
                if (it instanceof BlockStatement) {
                    listBlocks << it
                } else if (it instanceof ClassNode) {
                    if (!it.isInterface()) {
                        classList << it
                    }
                } else if (it instanceof MethodNode) {
                    methodList << it
                } else {
                    GsConsole.error("AST Node not supported (${it?.class?.simpleName}).")
                }
            }

            //Process list of classes
            if (classList) {
                if (consoleInfo) {
                    GsConsole.message('Processing class list...')
                }
                processClassList(classList)
                if (consoleInfo) {
                    GsConsole.message('Done class list.')
                }
            }

            //Process list of methods
            methodList?.each { MethodNode methodNode ->
                if (consoleInfo) {
                    GsConsole.message('Processing method '+methodNode.name)
                }
                //processMethodNode(methodNode)
                context.variableScoping.peek().add(methodNode.name)
                processBasicFunction("var ${methodNode.name}", methodNode, false)
            }

            //Process blocks after
            listBlocks?.each { it->
                processBlockStatement(it, false)
            }

            result = out.resultScript
        }
        result
    }

    //Process list of classes in correct order, inheritance order
    //Save list of variables for inheritance
    private processClassList(List<ClassNode> list) {

        def finalList = []
        def extraClasses = []
        def enumClasses = []
        while ((finalList.size() + extraClasses.size() + enumClasses.size()) < list.size()) {

            list.each { ClassNode it ->
                //println 'it->'+it.name+' super - '+it.superClass.name
                if (it.superClass.name == 'java.lang.Object')  {
                    if (!finalList.contains(it.name)) {
                        //println 'Adding '+it.name+' - '+it.isInterface()
                        finalList.add(it.name)
                    }
                } else {
                    //Expando allowed
                    if (it.superClass.name == 'groovy.lang.Script') {
                        extraClasses.add(it.name)
                    } else {
                        //If father in the list, we can add it
                        if (finalList.contains(it.superClass.name)) {
                            //println 'Adding 2 '+it.name+' - '+it.isInterface()
                            finalList.add(it.name)
                        } else {
                            //Looking for superclass, only accepts superclass a class in same script
                            if (it.superClass.name.startsWith('java.') ||
                                it.superClass.name.startsWith('groovy.')) {
                                if (it.superClass.name == 'java.lang.Enum') {
                                    enumClasses.add(it.name)
                                } else {
                                    throw new Exception('Inheritance not Allowed on ' + it.name)
                                }
                            }
                        }
                    }
                }

            }
        }
        //Finally process classes in order
        finalList.each { String nameClass ->
            if (consoleInfo) {
                GsConsole.message('  Processing class ' + nameClass)
            }
            processClassNode(list.find { ClassNode it ->
                return it.name == nameClass
            })
            if (consoleInfo) {
                GsConsole.message('  Processing class done.')
            }
        }
        //Expandos - Nothing to do!
        extraClasses.each { String nameClass ->
            //println 'Class->'+nameClass
            processScriptClassNode(list.find { ClassNode it ->
                return it.name == nameClass
            })
        }
        //Enums!
        enumClasses.each { String nameClass ->
            processEnum(list.find { ClassNode it ->
                return it.name == nameClass
            })
        }
    }

    /**
     * Create code the js class definition, for execute constructor
     * @param numberArguments
     * @param paramList
     * @return
     */
    private addConditionConstructorExecution(numberArguments,paramList) {

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

    private processScriptClassNode(ClassNode node) {

        //Push name in stack
        context.variableScoping.push([])
        context.actualScope.push([])

        out.addLine()

        //Methods
        node?.methods?.each {
            if (it.name!='main' && it.name!='run') {
                //Add too method names to variable scoping
                context.variableScoping.peek().add(it.name)
                processBasicFunction(it.name, it, false)
            }
        }

        out.addLine()

        //Remove variable class names from the list
        context.variableScoping.pop()
        context.actualScope.pop()

    }

    private addPropertyToClass(fieldOrProperty,isStatic) {

        def previous = GS_OBJECT
        if (isStatic) {
            previous = ''
        }

        if (fieldOrProperty.initialExpression) {
            out.addScript("${previous}.${fieldOrProperty.name} = ")
            visitNode(fieldOrProperty.initialExpression)
            out.addScript(';', true)
        } else {
            out.addScript("${previous}.${fieldOrProperty.name} = null;", true)
        }
    }

    private addPropertyStaticToClass(String name) {

        out.addScript("${GS_OBJECT}.__defineGetter__('${name}', function(){ " +
                "return ${context.classNameStack.peek()}.${name}; });", true)
        out.addScript("${GS_OBJECT}.__defineSetter__('${name}', function(${VALUE}){ " +
                "${context.classNameStack.peek()}.${name} = ${VALUE}; });", true)
    }

    private haveAnnotationNonConvert(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            //If dont have to convert then exit
            if (it.getClassNode().nameWithoutPackage=='GsNotConvert') {
                exit = true
            }
        }
        return exit
    }

    private haveAnnotationNative(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            //If native then exit
            if (it.getClassNode().nameWithoutPackage == 'GsNative') {
                exit = true
            }
        }
        exit
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
            processMethodNode(it, true)

            addConditionConstructorExecution(numberArguments,it.parameters)
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
            out.addScript("${GS_OBJECT}.${node.nameWithoutPackage}1 = function(map) { " +
                    "${GS_PASS_MAP_TO_OBJECT}(map,this); return this;};", true)
            out.addScript("if (arguments.length==1) {" +
                    "${GS_OBJECT}.${node.nameWithoutPackage}1(arguments[0]); }", true)
        }
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
        out.addScript listMixins.collect { "'$it'"}.join(',')
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

    private putGsNativeMethod(String name,MethodNode method) {
        out.addScript("${name} = function(")
        context.actualScope.push([])
        processFunctionOrMethodParameters(method,false,false)
        context.actualScope.pop()
        out.addScript(nativeFunctions[method.name], true)
        out.indent--
        out.removeTabScript()
        out.addScript('}', true)
    }

    private processClassNode(ClassNode node) { //Starting class conversion

        //Exit if dont have to convert
        if (haveAnnotationNonConvert(node.annotations)) {
            return 0
        }

        out.addLine()

        //Push name in stack
        context.classNameStack.push(node.nameWithoutPackage)
        context.variableScoping.push([])
        context.variableStaticScoping.push([])

        out.block ("function ${node.nameWithoutPackage}() ") {

            context.superNameStack.push(node.superClass.name)
            //Allowed inheritance
            if (node.superClass.name != 'java.lang.Object') {
                //println 'Allowed!'+ node.superClass.class.name
                out.addScript("var ${GS_OBJECT} = ${node.superClass.nameWithoutPackage}();", true)
                //We add to this class scope variables of fathers
                context.variableScoping.peek().addAll(context.inheritedVariables[node.superClass.name])
            } else {
                out.addScript("var ${GS_OBJECT} = ${GS_INHERIT}(${GS_BASE_CLASS},'${node.nameWithoutPackage}');", true)
            }
            out.addScript("${GS_OBJECT}.${CLASS} = { name: '${node.name}', simpleName: '${node.nameWithoutPackage}'};", true)
            if (node.superClass) {
                out.addScript("${GS_OBJECT}.${CLASS}.superclass = { " +
                        "name: '${node.superClass.name}', simpleName: '${node.superClass.nameWithoutPackage}'};", true)
            }

            if (consoleInfo) {
                GsConsole.message("   Processing class ${node.name}, step 1")
            }

            //Adding initial values of properties
            node?.properties?.each { PropertyNode property ->
                if (!property.isStatic()) {
                    addPropertyToClass(property, false)
                    //We add variable names of the class
                    context.variableScoping.peek().add(property.name)
                } else {
                    context.variableStaticScoping.peek().add(property.name);
                    addPropertyStaticToClass(property.name)
                }
            }

            //Add fields not added as properties
            node.fields.each { FieldNode field ->
                if (field.owner.name == node.name && (field.isPublic()|| !node.properties.any { it.name == field.name})) {
                    if (!field.isStatic()) {
                        addPropertyToClass(field,false)
                        context.variableScoping.peek().add(field.name)
                    } else {
                        context.variableStaticScoping.peek().add(field.name)
                        addPropertyStaticToClass(field.name)
                    }
                }
            }

            if (consoleInfo) {
                GsConsole.message("   Processing class ${node.name}, step 2")
            }

            //Save variables from this class for use in 'son' classes
            context.inheritedVariables.put(node.name, context.variableScoping.peek())
            //Ignoring fields
            //node?.fields?.each { println 'field->'+it  }

            processClassMethods(node?.methods, node.nameWithoutPackage)

            if (consoleInfo) {
                GsConsole.message("   Processing class ${node.name}, step 3")
            }

            //Constructors
            checkConstructors(node)

            //@Mixin
            checkAddMixin(node.nameWithoutPackage, node.annotations)

            out.addLine()
            out.addScript("return ${GS_OBJECT};")
        }

        if (consoleInfo) {
            GsConsole.message("   Processing class ${node.name}, step 4")
        }

        //Static methods
        node?.methods?.each { MethodNode method ->
            if (!haveAnnotationNonConvert(method.annotations)) {
                if (method.isStatic()) {
                    if (haveAnnotationNative(method.annotations)) {
                        putGsNativeMethod("${node.nameWithoutPackage}.${method.name}",method)
                    } else {
                        processBasicFunction("${node.nameWithoutPackage}.${method.name}",method,false)
                    }
                }
            }
        }

        //Static properties
        node?.properties?.each { it-> //println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (it.isStatic()) {
                out.addScript(node.nameWithoutPackage)
                addPropertyToClass(it, true)
            }
        }

        //Remove variable class names from the list
        context.variableScoping.pop()
        context.variableStaticScoping.pop()

        //Pop name in stack
        context.classNameStack.pop()
        context.superNameStack.pop()

        //@Category
        checkAddCategory(node.nameWithoutPackage, node.annotations)

        //Finish class conversion
        if (consoleInfo) {
            GsConsole.message("   Processing class ${node.name}, Done.")
        }
    }

    private processClassMethods(List<MethodNode> methods, String nodeName) {

        methods?.each { MethodNode it ->
            //Add method names to variable scoping
            if (!it.isStatic() && !it.isAbstract()) {
                context.variableScoping.peek().add(it.name)
            }
        }

        context.processingClassMethods = true
        methods?.each { MethodNode it ->

            if (!haveAnnotationNonConvert(it.annotations) && !it.isAbstract()) {
                //Process the methods
                if (haveAnnotationNative(it.annotations) && !it.isStatic()) {
                    putGsNativeMethod("${GS_OBJECT}.${it.name}",it)
                } else if (!it.isStatic()) {
                    processMethodNode(it,false)
                } else {
                    //We put the number of params as x? name variables
                    def numberParams = 0
                    if (it.parameters && it.parameters.size()>0) {
                        numberParams = it.parameters.size()
                    }
                    def params = []
                    numberParams.times { number ->
                        params << 'x'+number
                    }

                    out.addScript("${GS_OBJECT}.${it.name} = function(${params.join(',')}) { return ${nodeName}.${it.name}(")
                    out.addScript(params.join(','))
                    out.addScript("); }", true)
                }
            }
        }
        context.processingClassMethods = false
    }

    private processFunctionOrMethodParameters(functionOrMethod, boolean isConstructor,boolean addItInParameter) {

        boolean first = true
        boolean lastParameterCanBeMore = false

        //Parameters with default values if not shown
        def initalValues = [:]

        //If no parameters, we add it by defaul
        if (addItInParameter && (!functionOrMethod.parameters || functionOrMethod.parameters.size()==0)) {
            out.addScript('it')
            context.addToActualScope('it')
        } else {

            functionOrMethod.parameters?.eachWithIndex { Parameter param, index ->

                //If the last parameter is an Object[] then, maybe, can get more parameters as optional
                if (param.type.name=='[Ljava.lang.Object;' && index+1 == functionOrMethod.parameters.size()) {
                    lastParameterCanBeMore = true
                }
                //println 'pe->'+param.toString()+' - '+param.type.name //+' - '+param.type

                if (param.getInitialExpression()) {
                    //println 'Initial->'+param.getInitialExpression()
                    initalValues.putAt(param.name,param.getInitialExpression())
                }
                if (!first) {
                    out.addScript(', ')
                }
                context.addToActualScope(param.name)
                out.addScript(param.name)
                first = false
            }
        }
        out.addScript(') {')
        out.indent++
        out.addLine()

        //At start we add initialization of default values
        initalValues.each { key,value ->
            out.addScript("if (${key} === undefined) ${key} = ")
            visitNode(value)
            out.addScript(';', true)
        }

        //We add initialization of it inside switch closure function
        if (context.addClosureSwitchInitialization) {
            def name = SWITCH_VAR_NAME + (context.switchCount - 1)
            out.addScript("if (it === undefined) it = ${name};", true)
            context.addClosureSwitchInitialization = false
        }

        if (lastParameterCanBeMore) {
            def Parameter lastParameter = functionOrMethod.parameters.last()
            out.addScript("if (arguments.length==${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([arguments[${functionOrMethod.parameters.size()}-1]]); }", true)
            out.addScript("if (arguments.length<${functionOrMethod.parameters.size()}) { " +
                    "${lastParameter.name}=${GS_LIST}([]); }", true)
            out.addScript("if (arguments.length>${functionOrMethod.parameters.size()}) {", true)
            out.addScript("  ${lastParameter.name}=${GS_LIST}([${lastParameter.name}]);", true)
            out.addScript("  for (${COUNT}=${functionOrMethod.parameters.size()};${COUNT} < arguments.length; ${COUNT}++) {", true)
            out.addScript("    ${lastParameter.name}.add(arguments[${COUNT}]);", true)
            out.addScript("  }", true)
            out.addScript("}", true)
        }
    }

    private putFunctionParametersAndBody(functionOrMethod, boolean isConstructor, boolean addItDefault) {

        context.actualScope.push([])

        processFunctionOrMethodParameters(functionOrMethod, isConstructor, addItDefault)

        //println 'Closure '+expression+' Code:'+expression.code
        if (functionOrMethod.code instanceof BlockStatement) {
            processBlockStatement(functionOrMethod.code, !isConstructor)
        } else {
            GsConsole.error("FunctionOrMethod Code not supported (${functionOrMethod.code.class.simpleName})")
        }

        context.actualScope.pop()
    }

    private processBasicFunction(name, method, isConstructor) {

        out.addScript("$name = function(")

        putFunctionParametersAndBody(method,isConstructor,true)

        out.indent--
        if (isConstructor) {
            out.addScript('return this;', true)
        } else {
            out.removeTabScript()
        }
        out.addScript('}', true)
    }

    private processMethodNode(MethodNode method,isConstructor) {

        //Starting method conversion
        //Ignoring annotations
        //node?.annotations?.each {

        //Ignoring modifiers
        //visitModifiers(node.modifiers)

        //Ignoring init methods
        //if (node.name == '<init>') {
        //} else if (node.name == '<clinit>') {
        //visitType node.returnType

        def name =  method.name
        //Constructor method
        if (isConstructor) {
            //Add number of params to constructor name
            //BEWARE Atm only accepts constructor with different number or arguments
            name = context.classNameStack.peek() + (method.parameters?method.parameters.size():'0')
        }

        processBasicFunction("${GS_OBJECT}['$name']",method,isConstructor)

    }

    private statementThatCanReturn(statement) {
        return !(statement instanceof ReturnStatement) &&
                !(statement instanceof IfStatement) && !(statement instanceof WhileStatement) &&
                !(statement instanceof AssertStatement) && !(statement instanceof BreakStatement) &&
                !(statement instanceof CaseStatement) && !(statement instanceof CatchStatement) &&
                !(statement instanceof ContinueStatement) && !(statement instanceof DoWhileStatement) &&
                !(statement instanceof ForStatement) && !(statement instanceof SwitchStatement) &&
                !(statement instanceof ThrowStatement) && !(statement instanceof TryCatchStatement) &&
                !(statement.metaClass.expression && statement.expression instanceof DeclarationExpression)
    }

    /**
     * Process an AST Block
     * @param block
     * @param addReturn put 'return ' before last statement
     * @return
     */
    private processBlockStatement(block, addReturn) {
        if (block) {
            def number = 1
            //println 'Block->'+block
            if (block instanceof EmptyStatement) {
                GsConsole.debug "BlockEmpty -> ${block.text}"
            } else {
                //println '------------------------------Block->'+block.text
                block.getStatements()?.each { statement ->
                    def lookingForIf = false
                    def position
                    context.returnScoping.push(false)
                    if (addReturn && ((number++) == block.getStatements().size())
                            && statementThatCanReturn(statement)) {

                        //println 'Saving statemen->'+it
                        //println 'Saving return - '+ variableScoping.peek()
                        //this statement can be a complex statement with a return
                        //Go looking for a return statement in last statement
                        position = out.getSavePoint()
                    }
                    if (addReturn && (number - 1) == block.getStatements().size() &&
                            !context.lookingForReturnStatementInIf && statement instanceof IfStatement) {
                        context.lookingForReturnStatementInIf = true
                        lookingForIf = true
                    }
                    def oldlookingForReturnStatementInIf = context.lookingForReturnStatementInIf
                    if ((number - 1) != block.getStatements().size()) {
                        context.lookingForReturnStatementInIf = false
                    }
                    processStatement(statement)
                    context.lookingForReturnStatementInIf = oldlookingForReturnStatementInIf
                    if (lookingForIf) {
                        context.lookingForReturnStatementInIf = false
                    }
                    if (addReturn && position) {
                        if (!context.returnScoping.peek()) {
                            //No return statement, then we want add return
                            //println 'Yes!'+position
                            out.addScriptAt('return ', position)
                        }
                    }
                    context.returnScoping.pop()
                }
            }
        }
    }

    //???? there are both used
    private processBlockStatement(block) {
        processBlockStatement(block, false)
    }

    /**
     * Process a statement, adding ; at the end
     * @param statement
     */
    private void processStatement(Statement statement) {

        //println "statement (${statement.class.simpleName})->"+statement+' - '+statement.text
        visitNode(statement)

        //Adds ;
        if (out.resultScript) {
            out.resultScript += ';'
        }
        out.addLine()
        //println 'end statement'
    }

    private processAssertStatement(AssertStatement statement) {
        Expression e = statement.booleanExpression
        out.addScript(GS_ASSERT)
        out.addScript('(')
        visitNode(e)
        if (statement.getMessageExpression() && !(statement.messageExpression instanceof EmptyExpression)) {
            out.addScript(', ')
            visitNode(statement.messageExpression)
        }
        out.addScript(')')
    }

    private handExpressionInBoolean(expression) {
        if (expression instanceof VariableExpression || expression instanceof PropertyExpression ||
                (expression instanceof NotExpression &&
                        expression.expression &&
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

    private processBooleanExpression(BooleanExpression expression) {
        //Groovy truth is a bit different, empty collections return false, we fix that here
        handExpressionInBoolean(expression.expression)
    }

    private processExpressionStatement(ExpressionStatement statement) {
        Expression e = statement.expression
        visitNode(e)
    }

    private processDeclarationExpression(DeclarationExpression expression) {
        //println 'l->'+expression.leftExpression
        //println 'r->'+expression.rightExpression
        //println 'v->'+expression.getVariableExpression()

        if (expression.isMultipleAssignmentDeclaration()) {
            TupleExpression tuple = (TupleExpression)(expression.getLeftExpression())
            def number = 0;
            tuple.expressions.each { Expression expr ->
                //println 'Multiple->'+expr
                if (expr instanceof VariableExpression && expr.name!='_') {
                    context.addToActualScope(expr.name)
                    out.addScript('var ')
                    processVariableExpression(expr, true)
                    out.addScript(' = ')
                    visitNode(expression.rightExpression)
                    out.addScript(".getAt(${number})")
                    if (number < tuple.expressions.size()) {
                        out.addScript(';')
                    }
                }
                number++
            }
        } else {

            context.addToActualScope(expression.variableExpression.name)

            out.addScript('var ')
            processVariableExpression(expression.variableExpression, true)

            if (!(expression.rightExpression instanceof EmptyExpression)) {
                out.addScript(' = ')
                visitNode(expression.rightExpression)
            } else {
                out.addScript(' = null')
            }

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

    private boolean isVariableWithMissingScope(VariableExpression expression) {
        !expression.isThisExpression() && !context.allActualScopeContains(expression.name) &&
            !context.variableScopingContains(expression.name) &&
                (context.processingClosure || context.processingClassMethods)
    }

    private addPrefixOrPostfixIfNeeded(name) {
        if (prefixOperator) {
            name = prefixOperator + name
        }
        if (postfixOperator) {
            name = name + postfixOperator
        }
        name
    }

    private processVariableExpression(VariableExpression expression, isDeclaringVariable = false) {
        //println "name:${expression.name} - scope:${variableScoping.peek()} - isThis - ${expression.isThisExpression()}"
        if (context.variableScoping.peek().contains(expression.name) && !(context.actualScopeContains(expression.name))) {
            out.addScript(addPrefixOrPostfixIfNeeded("${GS_OBJECT}."+expression.name))
        } else if (context.variableStaticScoping.peek().contains(expression.name) && !(context.actualScopeContains(expression.name))) {
            out.addScript(addPrefixOrPostfixIfNeeded(context.classNameStack.peek()+'.'+expression.name))
        } else {
            if (isVariableWithMissingScope(expression) && !isDeclaringVariable) {
                out.addScript("${GS_FIND_SCOPE}('${addPrefixOrPostfixIfNeeded(expression.name)}', this)")
            } else {
                out.addScript(addPrefixOrPostfixIfNeeded(expression.name))
            }
        }
    }

    private writeFunctionWithLeftAndRight(functionName, expression) {
        out.addScript("${functionName}(")
        upgradedExpresion(expression.leftExpression)
        out.addScript(', ')
        upgradedExpresion(expression.rightExpression)
        out.addScript(')')
    }

    private processBinaryExpression(BinaryExpression expression) {

        //println 'Binary->'+expression.text + ' - '+expression.operation.text
        //Getting a range from a list
        if (expression.operation.text == '[' && expression.rightExpression instanceof RangeExpression) {
            out.addScript("${GS_RANGE_FROM_LIST}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(", ")
            visitNode(expression.rightExpression.getFrom())
            out.addScript(", ")
            visitNode(expression.rightExpression.getTo())
            out.addScript(')')
        //leftShift and rightShift function
        } else if (expression.operation.text == '<<' || expression.operation.text == '>>') {
            def nameFunction = expression.operation.text == '<<' ? 'leftShift' : 'rightShift'
            out.addScript("${GS_METHOD_CALL}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(",'${nameFunction}', ${GS_LIST}([")
            upgradedExpresion(expression.rightExpression)
            out.addScript(']))')
        //Regular Expression exact match all
        } else if (expression.operation.text == '==~') {
            out.addScript("${GS_EXACT_MATCH}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                out.addScript('/')
                processConstantExpression(expression.rightExpression,false)
                out.addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            out.addScript(')')
        //A matcher of regular expresion
        } else if (expression.operation.text == '=~') {
            out.addScript("${GS_REG_EXP}(")
            //println 'rx->'+expression.leftExpression
            upgradedExpresion(expression.leftExpression)
            out.addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                out.addScript('/')
                processConstantExpression(expression.rightExpression,false)
                out.addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            out.addScript(')')
        //Equals
        } else if (expression.operation.text == '==') {
            writeFunctionWithLeftAndRight(GS_EQUALS, expression)
        //in
        } else if (expression.operation.text == 'in') {
            writeFunctionWithLeftAndRight(GS_IN, expression)
        //Spaceship operator <=>
        } else if (expression.operation.text == '<=>') {
            writeFunctionWithLeftAndRight(GS_SPACE_SHIP, expression)
        //instanceof
        } else if (expression.operation.text == 'instanceof') {
            out.addScript("${GS_INSTANCE_OF}(")
            upgradedExpresion(expression.leftExpression)
            out.addScript(', "')
            upgradedExpresion(expression.rightExpression)
            out.addScript('")')
        //Multiply
        } else if (expression.operation.text == '*') {
            writeFunctionWithLeftAndRight(GS_MULTIPLY, expression)
        //Plus
        } else if (expression.operation.text == '+') {
            writeFunctionWithLeftAndRight(GS_PLUS, expression)
        //Minus
        } else if (expression.operation.text == '-') {
            writeFunctionWithLeftAndRight(GS_MINUS, expression)
        } else {

            //Execute setter if available
            if (expression.leftExpression instanceof PropertyExpression &&
                    (expression.operation.text in ['=', '+=', '-=']) &&
                !(expression.leftExpression instanceof AttributeExpression)) {

                PropertyExpression pe = (PropertyExpression)expression.leftExpression
                out.addScript("${GS_SET_PROPERTY}(")
                upgradedExpresion(pe.objectExpression)
                out.addScript(',')
                upgradedExpresion(pe.property)
                out.addScript(',')
                if (expression.operation.text == '+=') {
                    processPropertyExpression(expression.leftExpression)
                    out.addScript(' + ')
                } else if (expression.operation.text == '-=') {
                    processPropertyExpression(expression.leftExpression)
                    out.addScript(' - ')
                }
                upgradedExpresion(expression.rightExpression)
                out.addScript(')')

            } else {
                //println ' other->'+expression.text
                //If we are assigning a variable, and don't exist in scope, we add to it
                if (expression.operation.text=='=' && expression.leftExpression instanceof VariableExpression
                    && !context.allActualScopeContains(expression.leftExpression.name) &&
                        !context.variableScopingContains(expression.leftExpression.name)) {
                    context.addToActualScope(expression.leftExpression.name)
                }

                //If is a boolean operation, we have to apply groovyTruth
                //Left
                if (expression.operation.text in ['&&', '||']) {
                    out.addScript '('
                    handExpressionInBoolean(expression.leftExpression)
                    out.addScript ')'
                } else {
                    upgradedExpresion(expression.leftExpression)
                }
                //Operator
                //println 'Operator->'+expression.operation.text
                out.addScript(' '+expression.operation.text+' ')
                //Right
                //println 'Right->'+expression.rightExpression
                if (expression.operation.text in ['&&','||']) {
                    out.addScript '('
                    handExpressionInBoolean(expression.rightExpression)
                    out.addScript ')'
                } else {
                    upgradedExpresion(expression.rightExpression)
                }
                if (expression.operation.text=='[') {
                    out.addScript(']')
                }
            }
        }
    }

    //Adding () for operators order, can spam loads of ()
    private upgradedExpresion(expresion) {
        if (expresion instanceof BinaryExpression) {
            out.addScript('(')
        }
        visitNode(expresion)
        if (expresion instanceof BinaryExpression) {
            out.addScript(')')
        }
    }

    private processConstantExpression(ConstantExpression expression) {
        //println 'ConstantExpression->'+expression.text+'< '+expression.nullExpression
        if (expression.value instanceof String) {
            //println 'Value->'+expression.value+'<'+expression.value.endsWith('\n')
            String value = ''
            if (expression.value.startsWith('\n')) {
                value = '\\n'
            }
            def list = []
            expression.value.eachLine {
                if (it) list << it
            }
            value += list.join('\\n')
            value = value.replaceAll('"','\\\\"')
            //println 'After->'+value+'<'+value.endsWith('\n')
            if (expression.value.endsWith('\n') && !value.endsWith('\n') && value != '\\n') {
                value += '\\n'
            }
            out.addScript('"'+value+'"')
        } else {
            out.addScript(expression.value)
        }
    }

    private processConstantExpression(ConstantExpression expression,boolean addStuff) {
        if (expression.value instanceof String && addStuff) {
            processConstantExpression(expression)
        } else {
            out.addScript(expression.value)
        }

    }

    /**
     * Finally GString is something like String + Value + String + Value + String....
     * So we convert to "  " + value + "    " + value ....
     * @param e
     * @return
     */
    private processGStringExpression(GStringExpression expression) {

        def number = 0
        expression.getStrings().each {   exp ->
            //println 'Exp->'+exp
            if (number>0) {
                out.addScript(' + ')
            }
            //out.addScript('"')
            visitNode(exp)
            //out.addScript('"')

            if (expression.getValues().size() > number) {
                out.addScript(' + (')
                visitNode(expression.getValue(number))
                out.addScript(')')
            }
            number++
        }
    }

    private processNotExpression(NotExpression expression) {
        out.addScript('!')
        visitNode(expression.expression)
    }

    private processConstructorCallExpression(ConstructorCallExpression expression) {

        //println 'ConstructorCallExpression->'+expression.type.name + ' super? '+expression?.isSuperCall()
        //Super expression in constructor is allowed
        if (expression?.isSuperCall()) {
            def name = context.superNameStack.peek()
            //println 'processNotExpression name->'+name
            if (name == 'java.lang.Object') {
                out.addScript("this.${CONSTRUCTOR}")
            } else {
                out.addScript("this.${name}${expression.arguments.expressions.size()}")
            }
        } else if (expression.type.name=='java.util.Date') {
            out.addScript(GS_DATE)
        } else if (expression.type.name=='groovy.util.Expando') {
            out.addScript(GS_EXPANDO)
        } else if (expression.type.name=='java.util.Random') {
            out.addScript(GS_RANDOM)
        } else if (expression.type.name=='java.util.HashSet') {
            out.addScript(GS_SET)
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
        visitNode(expression.arguments)
    }

    private processArgumentListExpression(ArgumentListExpression expression,boolean withParenthesis) {
        if (withParenthesis) {
            out.addScript '('
        }
        int count = expression?.expressions?.size()
        expression.expressions?.each {
            visitNode(it)
            count--
            if (count) {
                out.addScript ', '
            }
        }
        if (withParenthesis) {
            out.addScript ')'
        }

    }

    private processArgumentListExpression(ArgumentListExpression expression) {
        processArgumentListExpression(expression,true)
    }

    private processObjectExpressionFromProperty(PropertyExpression expression) {
        if (expression.objectExpression instanceof ClassExpression) {
            out.addScript(expression.objectExpression.type.nameWithoutPackage)
        } else {
            visitNode(expression.objectExpression)
        }
    }

    private processPropertyExpressionFromProperty(PropertyExpression expression) {
        if (expression.property instanceof GStringExpression) {
            visitNode(expression.property)
        } else {
            out.addScript('"')
            "process${expression.property.class.simpleName}"(expression.property,false)
            out.addScript('"')
        }
    }

    private processPropertyExpression(PropertyExpression expression) {

        //println 'Pe->'+expression.objectExpression
        //println 'Pro->'+expression.property

        //If metaClass property we ignore it, javascript permits add directly properties and methods
        if (expression.property instanceof ConstantExpression && expression.property.value == 'metaClass') {
            if (expression.objectExpression instanceof VariableExpression) {

                if (expression.objectExpression.name=='this') {
                    out.addScript('this')
                } else {

                    //I had to add variable = ... cause gSmetaClass changing object and sometimes variable don't change
                    out.addScript("(${expression.objectExpression.name} = ${GS_META_CLASS}(")
                    visitNode(expression.objectExpression)
                    out.addScript('))')
                }
            } else {
                if (expression.objectExpression instanceof ClassExpression &&
                    (expression.objectExpression.type.name.startsWith('java.') ||
                     expression.objectExpression.type.name.startsWith('groovy.'))) {
                    throw new Exception("Not allowed access metaClass of Groovy or Java types (${expression.objectExpression.type.name})")
                }
                out.addScript("${GS_META_CLASS}(")
                visitNode(expression.objectExpression)
                out.addScript(')')
            }
        } else if (expression.property instanceof ConstantExpression && expression.property.value == 'class') {
            visitNode(expression.objectExpression)
            out.addScript(".${CLASS}")
        } else {

            if (!(expression instanceof AttributeExpression)) {
                out.addScript("${GS_GET_PROPERTY}(")
                if (expression.objectExpression instanceof VariableExpression &&
                        expression.objectExpression.name=='this') {
                    out.addScript("${GS_THIS_OR_OBJECT}(this,${GS_OBJECT})")
                } else {
                    processObjectExpressionFromProperty(expression)
                }

                out.addScript(',')

                processPropertyExpressionFromProperty(expression)

                //If is a safe expression as item?.data, we add one more parameter
                if (expression.isSafe()) {
                    out.addScript(',true')
                }

                out.addScript(')')
            } else {

                processObjectExpressionFromProperty(expression)
                out.addScript('[')
                processPropertyExpressionFromProperty(expression)
                out.addScript(']')
            }
        }

    }

    private processMethodCallExpression(MethodCallExpression expression) {

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
                processArgumentListExpression(expression.arguments,false)
                out.addScript("]):${nameFunc}")
                visitNode(expression.arguments)
                out.addScript(")")
            } else {
                visitNode(expression.objectExpression)
            }
        //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='super') {
            out.addScript("${SUPER_METHOD_BEGIN}${expression.methodAsString}")
        //Function times, with a number, have to put (number) in javascript
        } else if (['times','upto','step'].contains(expression.methodAsString) && expression.objectExpression instanceof ConstantExpression) {
            out.addScript('(')
            visitNode(expression.objectExpression)
            out.addScript(')')
            out.addScript(".${expression.methodAsString}")
        //With
        } else if (expression.methodAsString == 'with' && expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpression(0) && expression.arguments.getExpression(0) instanceof ClosureExpression) {
            visitNode(expression.objectExpression)
            out.addScript(".${WITH}")
        //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression && expression.objectExpression.type.name=='java.lang.Math') {
            out.addScript("Math.${expression.methodAsString}")
        //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression && expression.objectExpression.type.name=='java.lang.Class' &&
                expression.methodAsString=='forName') {
            out.addScript("${GS_CLASS_FOR_NAME}(")
            processArgumentListExpression(expression.arguments,false)
            out.addScript(')')
            addParameters = false
        //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='this' && expression.methodAsString == 'use') {
            ArgumentListExpression args = expression.arguments
            addParameters = false
            out.addScript("${GS_CATEGORY_USE}(\"")
            out.addScript(args.expressions[0].type.nameWithoutPackage)
            out.addScript('",')
            visitNode(args.expressions[1])
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
            visitNode(expression.objectExpression)
            out.addScript(".collect(function(it) { return ${GS_METHOD_CALL}(it,'${expression.methodAsString}',${GS_LIST}([")
            processArgumentListExpression(expression.arguments,false)
            out.addScript(']));})')
        //Call a method in this, method exist in main context
        } else if (isThis(expression.objectExpression) && context.firstVariableScopingHasMethod(expression.methodAsString)) {
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
                visitNode(expression.objectExpression)
            }

            out.addScript(',')
            //MethodName
            visitNode(expression.method)

            //Parameters
            out.addScript(",${GS_LIST}([")
            "process${expression.arguments.class.simpleName}"(expression.arguments,false)
            out.addScript(']))')
        }

        if (addParameters) {
            visitNode(expression.arguments)
        }
    }

    private isThis(expression) {
        expression instanceof VariableExpression && expression.name == 'this'
    }

    private addPlusPlusFunction(expression, isBefore) {

        //Only in mind ++ and --
        def plus = true
        if (expression.operation.text=='--') {
            plus = false
        }

        out.addScript("${GS_PLUS_PLUS}(")
        processObjectExpressionFromProperty(expression.expression)
        out.addScript(',')
        processPropertyExpressionFromProperty(expression.expression)

        out.addScript(",${plus},${isBefore?'true':'false'})")
    }

    private processPostfixExpression(PostfixExpression expression) {
        if (expression.operation.text in ['++','--'] && expression.expression instanceof PropertyExpression) {
            addPlusPlusFunction(expression, false)
        } else {
            postfixOperator = expression.operation.text
            visitNode(expression.expression)
            //out.addScript(postfixOperator)
            postfixOperator = ''
        }
    }

    private processPrefixExpression(PrefixExpression expression) {
        if (expression.expression instanceof PropertyExpression) {
            addPlusPlusFunction(expression, true)
        } else {
            prefixOperator = expression.operation.text
            //out.addScript(prefixOperator)
            visitNode(expression.expression)
            prefixOperator = ''
        }
    }

    private processReturnStatement(ReturnStatement statement) {
        context.returnScoping.add(true)
        out.addScript('return ')
        visitNode(statement.expression)
    }

    private processClosureExpression(ClosureExpression expression) {
        processClosureExpression(expression, true)
    }

    private processClosureExpression(ClosureExpression expression, boolean addItDefault) {

        out.addScript("function(")

        context.processingClosure = true
        putFunctionParametersAndBody(expression,false,addItDefault)
        context.processingClosure = false

        out.indent--
        out.removeTabScript()
        out.addScript('}')

    }

    private processIfStatement(IfStatement statement) {
        out.addScript('if (')
        visitNode(statement.booleanExpression)
        out.addScript(') {')

        processIfOrElseBlock(statement.ifBlock)

        if (statement.elseBlock && !(statement.elseBlock instanceof EmptyStatement)) {
            //println 'Else->'+statement.elseBlock.text
            out.addScript(' else {')
            processIfOrElseBlock(statement.elseBlock)
        }
    }

    private processIfOrElseBlock(block) {
        out.indent++
        out.addLine()
        if (block instanceof BlockStatement) {
            processBlockStatement(block, context.lookingForReturnStatementInIf)
        } else {
            if (context.lookingForReturnStatementInIf && statementThatCanReturn(block)) {
                out.addScript('return ')
            }
            visitNode(block)
            out.addScript(';', true)
        }
        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processMapExpression(MapExpression expression) {
        out.addScript("${GS_MAP}()")
        expression.mapEntryExpressions?.each { ep ->
            out.addScript(".add(");
            visitNode(ep.keyExpression)
            out.addScript(",");
            visitNode(ep.valueExpression)
            out.addScript(")");
        }
    }

    private processListExpression(ListExpression expression) {
        out.addScript("${GS_LIST}([")
        def first = true
        expression?.expressions?.each { it ->
            if (!first) {
                out.addScript(' , ')
            } else {
                first = false
            }
            visitNode(it)
        }
        out.addScript('])')
    }

    private processRangeExpression(RangeExpression expression) {
        out.addScript("${GS_RANGE}(")
        visitNode(expression.from)
        out.addScript(", ")
        visitNode(expression.to)
        out.addScript(', '+expression.isInclusive())
        out.addScript(')')
    }

    private processForStatement(ForStatement statement) {

        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //We change this for in...  for a call lo closure each, that works fine in javascript
            visitNode(statement?.collectionExpression)
            out.addScript('.each(function(')
            visitNode(statement.variable)

        } else {
            out.addScript 'for ('
            visitNode(statement?.collectionExpression)
        }
        out.addScript ') {'
        out.indent++
        out.addLine()

        visitNode(statement?.loopBlock)

        out.indent--
        out.removeTabScript()
        out.addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            out.addScript(')')
        }
    }

    private processClosureListExpression(ClosureListExpression expression) {
        boolean first = true
        expression?.expressions?.each { it ->
            if (!first) {
                out.addScript(' ; ')
            }
            first = false
            visitNode(it)
        }
    }

    private processParameter(Parameter parameter) {
        //println 'Initial->'+parameter.getInitialExpression()
        out.addScript(parameter.name)
    }

    private processTryCatchStatement(TryCatchStatement statement) {
        //Try block
        out.addScript('try {')
        out.indent++
        out.addLine()

        visitNode(statement?.tryStatement)

        out.indent--
        out.removeTabScript()
        //Catch block
        out.addScript('} catch (')
        if (statement?.catchStatements[0]) {
            visitNode(statement?.catchStatements[0].variable)
        } else {
            out.addScript('e')
        }
        out.addScript(') {')
        out.indent++
        out.addLine()
        //Only process first catch
        visitNode(statement?.catchStatements[0])

        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processCatchStatement(CatchStatement statement) {
        processBlockStatement(statement.code,false)
    }

    private processTernaryExpression(TernaryExpression expression) {
        //println 'Ternary->'+expression.text
        out.addScript('(')
        visitNode(expression.booleanExpression)
        out.addScript(' ? ')
        visitNode(expression.trueExpression)
        out.addScript(' : ')
        visitNode(expression.falseExpression)
        out.addScript(')')
    }

    private getSwitchExpression(Expression expression,String varName) {

        if (expression instanceof ClosureExpression) {
            context.addClosureSwitchInitialization = true
            processClosureExpression(expression,true)
            out.addScript('()')
        } else {
            out.addScript("${varName} === ")
            visitNode(expression)
        }

    }

    private processSwitchStatement(SwitchStatement statement) {

        def varName = SWITCH_VAR_NAME + context.switchCount++

        out.addScript('var '+varName+' = ')
        visitNode(statement.expression)
        out.addScript(';', true)

        def first = true

        statement.caseStatements?.each { it ->
            if (first) {
                out.addScript("if (")
                first = false
            } else {
                out.addScript("} else if (")
            }
            getSwitchExpression(it.expression,varName)
            out.addScript(') {')
            out.indent++
            out.addLine()
            visitNode(it?.code)
            out.indent--
            out.removeTabScript()
        }
        if (statement.defaultStatement) {
            out.addScript('} else {')
            out.indent++
            out.addLine()
            visitNode(statement.defaultStatement)
            out.indent--
            out.removeTabScript()
        }

        out.addScript('}')

        context.switchCount--
    }

    private processCaseStatement(CaseStatement statement) {
        out.addScript 'case '
        visitNode(statement?.expression)
        out.addScript ':'
        out.indent++
        out.addLine()
        visitNode(statement?.code)
        out.indent--
        out.removeTabScript()
    }

    private processBreakStatement(BreakStatement statement) {
        if (context.switchCount == 0) {
            out.addScript('break')
        }
    }

    private processWhileStatement(WhileStatement statement) {
        out.addScript('while (')
        visitNode(statement.booleanExpression)
        out.addScript(') {')
        out.indent++
        out.addLine()
        visitNode(statement.loopBlock)
        out.indent--
        out.removeTabScript()
        out.addScript('}')
    }

    private processTupleExpression(TupleExpression expression, withParenthesis = true) {
        if (withParenthesis) {
            out.addScript('(')
        }
        out.addScript("${GS_MAP}()")
        expression.expressions.each {
            visitNode(it)
            if (withParenthesis) {
                out.addScript(')')
            }
        }
    }

    private processNamedArgumentListExpression(NamedArgumentListExpression expression) {
        expression.mapEntryExpressions.eachWithIndex { MapEntryExpression exp,i ->
            out.addScript('.add(')
            visitNode(exp.keyExpression)
            out.addScript(',')
            visitNode(exp.valueExpression)
            out.addScript(')')
        }
    }

    private processBitwiseNegationExpression(BitwiseNegationExpression expression) {
        out.addScript("/${expression.text}/")
    }

    private processEnum(ClassNode node) {

        out.addLine()

        //Push name in stack
        context.variableScoping.push([])

        out.addScript("var ${node.nameWithoutPackage} = {")

        out.indent ++
        out.addLine()

        //out.addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass

        //Fields
        def number = 1
        node?.fields?.each { it->
            if (!['MIN_VALUE', 'MAX_VALUE', '$VALUES'].contains(it.name)) {
                out.addScript("${it.name} : ${number++},", true)
                context.variableScoping.peek().add(it.name)
            }
        }

        //Methods
        node?.methods?.each { //println 'method->'+it;

            if (!['values', 'next', 'previous', 'valueOf', '$INIT', '<clinit>'].contains(it.name)) {

                context.variableScoping.peek().add(it.name)
                out.addScript("${it.name} : function(")
                putFunctionParametersAndBody(it,false,true)

                out.indent--
                out.removeTabScript()
                out.addScript('},', true)
            }
        }

        out.indent --
        out.addLine()
        out.addScript('}', true)

        //Remove variable class names from the list
        context.variableScoping.pop()
    }

    private processClassExpression(ClassExpression expression) {
        out.addScript(translateClassName(expression.text))
    }

    private translateClassName(String name) {
        def result = name
        def i = result.lastIndexOf('.')
        if (i > 0) {
            result = result.substring(i + 1)
        }
        result
    }

    private processThrowStatement(ThrowStatement statement) {
        out.addScript('throw "Exception"')
        //println 'throw expression'+statement.expression.text
    }

    private processStaticMethodCallExpression(StaticMethodCallExpression expression) {
        out.addScript("${expression.ownerType.name}.${expression.method}")
        visitNode(expression.arguments)
    }

    private processElvisOperatorExpression(ElvisOperatorExpression expression) {
        out.addScript("${GS_ELVIS}(")
        visitNode(expression.booleanExpression)
        out.addScript(' , ')
        visitNode(expression.trueExpression)
        out.addScript(' , ')
        visitNode(expression.falseExpression)
        out.addScript(')')
    }

    private processAttributeExpression(AttributeExpression expression) {
        processPropertyExpression(expression)
    }

    private processCastExpression(CastExpression expression) {
        if (expression.type.nameWithoutPackage == 'Set' && expression.expression instanceof ListExpression) {
            out.addScript("${GS_SET}(")
            visitNode(expression.expression)
            out.addScript(')')
        } else {
            throw new Exception('Casting not supported for '+expression.type.name)
        }
    }

    private processMethodPointerExpression(MethodPointerExpression expression) {
        visitNode(expression.expression)
        out.addScript('[')
        visitNode(expression.methodName)
        out.addScript(']')
    }

    private processSpreadExpression(SpreadExpression expression) {
        out.addScript("new ${GS_SPREAD}(")
        visitNode(expression.expression)
        out.addScript(')')
    }

    private processSpreadMapExpression(SpreadMapExpression expression) {
        out.addScript("'${SPREAD_MAP}'")
    }

    private processEmptyExpression(EmptyExpression expression) {
        //Nothing to do
    }

    private visitNode(expression) {
        "process${expression.class.simpleName}"(expression)
    }
}

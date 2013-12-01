package org.grooscript.convert

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

    //Indent for pretty print
    def indent
    static final TAB = '  '
    String resultScript
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

    def inheritedVariables = [:]

    //Where code of native functions stored, as a map. Used for GsNative annotation
    def nativeFunctions

    //Adds a console info if activated
    def consoleInfo = false

    //Control switch inside switch
    def switchCount = 0
    def addClosureSwitchInitialization = false

    //Conversion Options
    def convertDependencies = true
    Closure customization = null
    def classPath = null

    //Prefix and postfix for variables without clear scope
    def prefixOperator = '', postfixOperator = ''

    private addToActualScope(variableName) {
        if (!actualScope.isEmpty()) {
            actualScope.peek().add(variableName)
        }
    }

    private actualScopeContains(variableName) {
        if (!actualScope.isEmpty()) {
            return actualScope.peek().contains(variableName)
        } else {
            return false
        }
    }

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
                GsConsole.error('Error getting AST from script: '+e.message)
                if (phase==0) {
                    throw new Exception("Compiler ERROR on Script -"+e.message)
                } else {
                    throw new Exception("Compiler END ERROR on Script -"+e.message)
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
        indent = 0
        resultScript = ''
        if (list && list.size()>0) {
            //println '-----------------Size('+list.size+')->'+list
            variableScoping.clear()
            variableScoping.push([])
            variableStaticScoping.clear()
            variableStaticScoping.push([])
            actualScope.clear()
            actualScope.push([])
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
                processBasicFunction("var ${methodNode.name}",methodNode,false)
            }

            //Process blocks after
            listBlocks?.each { it->
                processBlockStament(it,false)
            }

            result = resultScript
        }
        result
    }

    //Process list of classes in correct order, inheritance order
    //Save list of variables for inheritance
    private processClassList(List<ClassNode> list) {

        def finalList = []
        def extraClasses = []
        def enumClasses = []
        while ((finalList.size()+extraClasses.size()+enumClasses.size())<list.size()) {

            list.each { ClassNode it ->
                //println 'it->'+it.name+' super - '+it.superClass.name
                if (it.superClass.name=='java.lang.Object')  {
                    if (!finalList.contains(it.name)) {
                        //println 'Adding '+it.name+' - '+it.isInterface()
                        finalList.add(it.name)
                    }
                } else {
                    //Expando allowed
                    if (it.superClass.name=='groovy.lang.Script') {
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
                                if (it.superClass.name=='java.lang.Enum') {
                                    enumClasses.add(it.name)
                                } else {
                                    throw new Exception('Inheritance not Allowed on '+it.name)
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
                GsConsole.message('  Processing class '+nameClass)
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

        addScript("if (arguments.length==${numberArguments}) {")
        addScript("${org.grooscript.JsNames.GS_OBJECT}.${translateClassName(classNameStack.peek())}${numberArguments}")

        addScript '('
        def count = 0
        paramList?.each { param ->
            if (count>0) addScript ', '
            addScript("arguments[${count}]")
            count++
        }
        addScript ')'

        addScript('; }')
        addLine()
    }

    private translateClassName(String name) {
        def result = name
        def i
        while ((i = result.indexOf('.'))>=0) {
            result = result.substring(i+1)
        }

        result
    }

    private processScriptClassNode(ClassNode node) {

        //Push name in stack
        variableScoping.push([])
        actualScope.push([])

        addLine()

        //Adding initial values of properties
        /*
        node?.properties?.each { it->
            println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (it.initialExpression) {
                addScript("${GS_OBJECT}.${it.name} = ")
                visitNode(it.initialExpression)
                addScript(';')
                addLine()
            } else {
                addScript("${GS_OBJECT}.${it.name} = null;")
                addLine()
            }

            //We add variable names of the class
            variableScoping.peek().add(it.name)
        }*/

        //Methods
        node?.methods?.each {
            if (it.name!='main' && it.name!='run') {
                //Add too method names to variable scoping
                variableScoping.peek().add(it.name)
                processBasicFunction(it.name,it,false)
            }
        }

        addLine()

        //Remove variable class names from the list
        variableScoping.pop()
        actualScope.pop()

    }

    private addPropertyToClass(fieldOrProperty,isStatic) {

        def previous = org.grooscript.JsNames.GS_OBJECT
        if (isStatic) {
            previous = ''
        }

        if (fieldOrProperty.initialExpression) {
            addScript("${previous}.${fieldOrProperty.name} = ")
            visitNode(fieldOrProperty.initialExpression)
            addScript(';')
            addLine()
        } else {
            addScript("${previous}.${fieldOrProperty.name} = null;")
            addLine()
        }
    }

    private addPropertyStaticToClass(String name) {

        addScript("${org.grooscript.JsNames.GS_OBJECT}.__defineGetter__('${name}', function(){ return ${translateClassName(classNameStack.peek())}.${name}; });")
        addLine()
        addScript("${org.grooscript.JsNames.GS_OBJECT}.__defineSetter__('${name}', function(${org.grooscript.JsNames.VALUE}){ ${translateClassName(classNameStack.peek())}.${name} = ${org.grooscript.JsNames.VALUE}; });")
        addLine()
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
            if (numberArguments==1) {
                has1parameterConstructor = true
            }
            processMethodNode(it,true)

            addConditionConstructorExecution(numberArguments,it.parameters)
        }

        if (haveAnnotationGroovyImmutable(node.annotations)) {
            //Add a constructor with params
            def paramSize = node.properties.size()
            def paramNames = node.properties.collect { it.name }.join(', ')
            def nameFunction = "${org.grooscript.JsNames.GS_OBJECT}.${translateClassName(node.name)}${paramSize}"
            addScript("${nameFunction} = function(${paramNames}) {")
            node.properties.collect { it.name }.each {
                addScript("  ${org.grooscript.JsNames.GS_OBJECT}.${it} = ${it}; ")
            }
            addScript("  return this; ")
            addScript("};")
            addLine()
            addScript("if (arguments.length==${paramSize}) {${nameFunction}.apply(${org.grooscript.JsNames.GS_OBJECT}, arguments); }")
            addLine()
            if (paramSize == 1) {
                has1parameterConstructor = true
            }
        }

        //If no constructor with 1 parameter, we create 1 that get a map, for put value on properties
        if (!has1parameterConstructor) {
            addScript("${org.grooscript.JsNames.GS_OBJECT}.${translateClassName(node.name)}1 = function(map) { ${org.grooscript.JsNames.GS_PASS_MAP_TO_OBJECT}(map,this); return this;};")
            addLine()
            addScript("if (arguments.length==1) {${org.grooscript.JsNames.GS_OBJECT}.${translateClassName(node.name)}1(arguments[0]); }")
            addLine()
        }
    }

    private checkAddMixin(className, annotations) {

        annotations.each { AnnotationNode annotationNode ->
            if (annotationNode.getClassNode().name=='groovy.lang.Mixin') {
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
        addScript("${org.grooscript.JsNames.GS_MIXIN_CLASS}('${className}',")
        addScript('[')
        addScript listMixins.collect { "'$it'"}.join(',')
        addScript(']);')
        addLine()
    }

    private checkAddCategory(className, annotations) {
        annotations.each { AnnotationNode annotationNode ->
            if (annotationNode.getClassNode().name=='groovy.lang.Category') {
                annotationNode.members.values().each { value ->
                    if (value instanceof ListExpression) {
                        value.expressions.each { it ->
                            addCategoryToClass(className, it.type.nameWithoutPackage)
                        }
                    } else {
                        addCategoryToClass(className, value.type.nameWithoutPackage)
                    }
                }
                addScript("${org.grooscript.JsNames.GS_MY_CATEGORIES}['${className}'] = ${className};")
                addLine()
            }
        }
    }

    private addCategoryToClass(categoryName, className) {
        addScript("${org.grooscript.JsNames.GS_ADD_CATEGORY_ANNOTATION}('${categoryName}','${className}');")
        addLine()
    }

    private putGsNativeMethod(String name,MethodNode method) {
        addScript("${name} = function(")
        actualScope.push([])
        processFunctionOrMethodParameters(method,false,false)
        actualScope.pop()
        addScript(nativeFunctions[method.name])
        addLine()
        indent--
        removeTabScript()
        addScript('}')
        addLine()
    }

    private processClassNode(ClassNode node) { //Starting class conversion

        //Exit if dont have to convert
        if (haveAnnotationNonConvert(node.annotations)) {
            return 0
        }

        addLine()

        //Push name in stack
        classNameStack.push(node.name)
        variableScoping.push([])
        variableStaticScoping.push([])

        addScript("function ${translateClassName(node.name)}() {")

        indent ++
        addLine()
        superNameStack.push(node.superClass.name)
        //Allowed inheritance
        if (node.superClass.name != 'java.lang.Object') {
            //println 'Allowed!'+ node.superClass.class.name
            addScript("var ${org.grooscript.JsNames.GS_OBJECT} = ${translateClassName(node.superClass.name)}();")
            //We add to this class scope variables of fathers
            variableScoping.peek().addAll(inheritedVariables[node.superClass.name])
        } else {
            addScript("var ${org.grooscript.JsNames.GS_OBJECT} = ${org.grooscript.JsNames.GS_INHERIT}(${org.grooscript.JsNames.GS_BASE_CLASS},'${translateClassName(node.name)}');")
        }
        addLine()
        addScript("${org.grooscript.JsNames.GS_OBJECT}.${org.grooscript.JsNames.CLASS} = { name: '${node.name}', simpleName: '${node.nameWithoutPackage}'};")
        addLine()
        if (node.superClass) {
            addScript("${org.grooscript.JsNames.GS_OBJECT}.${org.grooscript.JsNames.CLASS}.superclass = { name: '${node.superClass.name}', simpleName: '${node.superClass.nameWithoutPackage}'};")
            addLine()
        }

        if (consoleInfo) {
            GsConsole.message("   Processing class ${node.name}, step 1")
        }

        //Adding initial values of properties
        node?.properties?.each { it-> //println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (!it.isStatic()) {
                addPropertyToClass(it,false)
                //We add variable names of the class
                variableScoping.peek().add(it.name)
            } else {
                variableStaticScoping.peek().add(it.name);
                addPropertyStaticToClass(it.name)
            }
        }

        //Add fields not added as properties
        node.fields.each { FieldNode field ->
            if (field.owner.name == node.name && (field.isPublic()|| !node.properties.any { it.name == field.name})) {
                if (!field.isStatic()) {
                    addPropertyToClass(field,false)
                    variableScoping.peek().add(field.name)
                } else {
                    variableStaticScoping.peek().add(field.name)
                    addPropertyStaticToClass(field.name)
                }
            }
        }

        if (consoleInfo) {
            GsConsole.message("   Processing class ${node.name}, step 2")
        }

        //Save variables from this class for use in 'son' classes
        inheritedVariables.put(node.name,variableScoping.peek())
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

        addLine()
        indent --
        addScript("return ${org.grooscript.JsNames.GS_OBJECT};")
        addLine()
        addScript('};')
        addLine()

        if (consoleInfo) {
            GsConsole.message("   Processing class ${node.name}, step 4")
        }

        //Static methods
        node?.methods?.each { MethodNode method ->
            if (!haveAnnotationNonConvert(method.annotations)) {
                if (method.isStatic()) {
                    if (haveAnnotationNative(method.annotations)) {
                        putGsNativeMethod("${translateClassName(node.name)}.${method.name}",method)
                    } else {
                        processBasicFunction("${translateClassName(node.name)}.${method.name}",method,false)
                    }
                }
            }
        }

        //Static properties
        node?.properties?.each { it-> //println 'Property->'+it; println 'initialExpresion->'+it.initialExpression
            if (it.isStatic()) {
                addScript(translateClassName(node.name))
                addPropertyToClass(it,true)
            }
        }

        //Remove variable class names from the list
        variableScoping.pop()
        variableStaticScoping.pop()

        //Pop name in stack
        classNameStack.pop()
        superNameStack.pop()

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
                variableScoping.peek().add(it.name)
            }
        }

        processingClassMethods = true
        methods?.each { MethodNode it ->

            if (!haveAnnotationNonConvert(it.annotations) && !it.isAbstract()) {
                //Process the methods
                if (haveAnnotationNative(it.annotations) && !it.isStatic()) {
                    putGsNativeMethod("${org.grooscript.JsNames.GS_OBJECT}.${it.name}",it)
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

                    addScript("${org.grooscript.JsNames.GS_OBJECT}.${it.name} = function(${params.join(',')}) { return ${nodeName}.${it.name}(")
                    addScript(params.join(','))
                    addScript("); }")
                    addLine()
                }
            }
        }
        processingClassMethods = false
    }

    private processFunctionOrMethodParameters(functionOrMethod, boolean isConstructor,boolean addItInParameter) {

        boolean first = true
        boolean lastParameterCanBeMore = false

        //Parameters with default values if not shown
        def initalValues = [:]

        //If no parameters, we add it by defaul
        if (addItInParameter && (!functionOrMethod.parameters || functionOrMethod.parameters.size()==0)) {
            addScript('it')
            addToActualScope('it')
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
                    addScript(', ')
                }
                addToActualScope(param.name)
                addScript(param.name)
                first = false
            }
        }
        addScript(') {')
        indent++
        addLine()

        //At start we add initialization of default values
        initalValues.each { key,value ->
            addScript("if (${key} === undefined) ${key} = ")
            visitNode(value)
            addScript(';')
            addLine()
        }

        //We add initialization of it inside switch closure function
        if (addClosureSwitchInitialization) {
            def name = org.grooscript.JsNames.SWITCH_VAR_NAME + (switchCount - 1)
            addScript("if (it === undefined) it = ${name};")
            addLine()
            addClosureSwitchInitialization = false
        }

        if (lastParameterCanBeMore) {
            def Parameter lastParameter = functionOrMethod.parameters.last()
            addScript("if (arguments.length==${functionOrMethod.parameters.size()}) { ${lastParameter.name}=${org.grooscript.JsNames.GS_LIST}([arguments[${functionOrMethod.parameters.size()}-1]]); }")
            addLine()
            addScript("if (arguments.length<${functionOrMethod.parameters.size()}) { ${lastParameter.name}=${org.grooscript.JsNames.GS_LIST}([]); }")
            addLine()
            addScript("if (arguments.length>${functionOrMethod.parameters.size()}) {")
            addLine()
            addScript("  ${lastParameter.name}=${org.grooscript.JsNames.GS_LIST}([${lastParameter.name}]);")
            addLine()
            addScript("  for (${org.grooscript.JsNames.COUNT}=${functionOrMethod.parameters.size()};${org.grooscript.JsNames.COUNT} < arguments.length; ${org.grooscript.JsNames.COUNT}++) {")
            addLine()
            addScript("    ${lastParameter.name}.add(arguments[${org.grooscript.JsNames.COUNT}]);")
            addLine()
            addScript("  }")
            addLine()
            addScript("}")
            addLine()
        }
    }

    private putFunctionParametersAndBody(functionOrMethod, boolean isConstructor, boolean addItDefault) {

        actualScope.push([])

        processFunctionOrMethodParameters(functionOrMethod,isConstructor,addItDefault)

        //println 'Closure '+expression+' Code:'+expression.code
        if (functionOrMethod.code instanceof BlockStatement) {
            processBlockStament(functionOrMethod.code,!isConstructor)
        } else {
            GsConsole.error("FunctionOrMethod Code not supported (${functionOrMethod.code.class.simpleName})")
        }

        actualScope.pop()
    }

    private processBasicFunction(name, method, isConstructor) {

        addScript("$name = function(")

        putFunctionParametersAndBody(method,isConstructor,true)

        indent--
        if (isConstructor) {
            addScript('return this;')
            addLine()
        } else {
            removeTabScript()
        }
        addScript('}')
        addLine()

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
            name = translateClassName(classNameStack.peek()) + (method.parameters?method.parameters.size():'0')
        }

        processBasicFunction("${org.grooscript.JsNames.GS_OBJECT}['$name']",method,isConstructor)

    }

    /**
     * Process an AST Block
     * @param block
     * @param addReturn put 'return ' before last statement
     * @return
     */
    private processBlockStament(block,addReturn) {
        if (block) {
            def number = 1
            //println 'Block->'+block
            if (block instanceof EmptyStatement) {
                GsConsole.debug "BlockEmpty -> ${block.text}"
                //println 'Empty->'+block.getStatementLabel()
            } else {
                //println '------------------------------Block->'+block.text
                block.getStatements()?.each { statement ->
                    //println 'Block Statement-> size '+ block.getStatements().size() + ' number '+number+ ' it->'+it
                    //println 'is block-> '+ (it instanceof BlockStatement)
                    //println 'statement-> '+ statement.text
                    def position
                    returnScoping.push(false)
                    if (addReturn && ((number++)==block.getStatements().size()) && !(statement instanceof ReturnStatement)
                            && !(statement instanceof IfStatement) && !(statement instanceof WhileStatement)
                            && !(statement instanceof AssertStatement) && !(statement instanceof BreakStatement)
                            && !(statement instanceof CaseStatement) && !(statement instanceof CatchStatement)
                            && !(statement instanceof ContinueStatement) && !(statement instanceof DoWhileStatement)
                            && !(statement instanceof ForStatement) && !(statement instanceof SwitchStatement)
                            && !(statement instanceof ThrowStatement) && !(statement instanceof TryCatchStatement)
                            && !(statement.metaClass.expression && statement.expression instanceof DeclarationExpression)) {

                        //println 'Saving statemen->'+it
                        //println 'Saving return - '+ variableScoping.peek()
                        //this statement can be a complex statement with a return
                        //Go looking for a return statement in last statement
                        position = getSavePoint()
                        //We use actualScoping for getting return statement in this scope
                        //variableScoping.peek().remove(gSgotResultStatement)
                    }
                    processStatement(statement)
                    if (addReturn && position) {
                        if (!returnScoping.peek()) {
                            //No return statement, then we want add return
                            //println 'Yes!'+position
                            addScriptAt('return ',position)
                        }
                    }
                    returnScoping.pop()
                }
            }
        }
    }

    //???? there are both used
    private processBlockStatement(block) {
        processBlockStament(block,false)
    }

    /**
     * Add a line to javascript output
     * @param script
     * @param line
     * @return
     */
    private addLine() {
        //println "sc(${script}) line(${line})"
        if (resultScript) {
            resultScript += '\n'
        } else {
            resultScript = ''
        }
        indent.times { resultScript += TAB }
    }

    /**
     * Add a text to javascript output
     * @param text
     * @return
     */
    private addScript(text) {
        //println 'adding ->'+text
        //indent.times { resultScript += TAB }
        resultScript += text
    }

    /**
     * Add text to javascript output at some position
     * @param text
     * @param position
     * @return
     */
    private addScriptAt(text,position) {
        resultScript = resultScript.substring(0,position) + text + resultScript.substring(position)
    }

    /**
     * Remove a TAB from current javascript output
     * @return
     */
    private removeTabScript() {
        resultScript = resultScript[0..resultScript.size()-1-TAB.size()]
    }

    /**
     * Get actual position in javascript output
     * @return
     */
    private getSavePoint() {
        return resultScript.size()
    }

    /**
     * Process a statement, adding ; at the end
     * @param statement
     */
    private void processStatement(Statement statement) {

        //println "statement (${statement.class.simpleName})->"+statement+' - '+statement.text
        visitNode(statement)

        //Adds ;
        if (resultScript) {
            resultScript += ';'
        }
        addLine()
        //println 'end statement'
    }

    private processAssertStatement(AssertStatement statement) {
        Expression e = statement.booleanExpression
        addScript(org.grooscript.JsNames.GS_ASSERT)
        addScript('(')
        visitNode(e)
        if (statement.getMessageExpression() && !(statement.messageExpression instanceof EmptyExpression)) {
            addScript(', ')
            visitNode(statement.messageExpression)
        }
        addScript(')')
    }

    private handExpressionInBoolean(expression) {
        if (expression instanceof VariableExpression || expression instanceof PropertyExpression ||
                (expression instanceof NotExpression &&
                        expression.expression &&
                        (expression.expression instanceof VariableExpression || expression.expression instanceof PropertyExpression))) {
            if (expression instanceof NotExpression) {
                addScript("!${org.grooscript.JsNames.GS_BOOL}(")
                visitNode(expression.expression)
            } else {
                addScript("${org.grooscript.JsNames.GS_BOOL}(")
                visitNode(expression)
            }
            addScript(')')
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
                    addToActualScope(expr.name)
                    addScript('var ')
                    processVariableExpression(expr, true)
                    addScript(' = ')
                    visitNode(expression.rightExpression)
                    addScript(".getAt(${number})")
                    if (number < tuple.expressions.size()) {
                        addScript(';')
                    }
                }
                number++
            }
        } else {

            addToActualScope(expression.variableExpression.name)

            addScript('var ')
            processVariableExpression(expression.variableExpression, true)

            if (!(expression.rightExpression instanceof EmptyExpression)) {
                addScript(' = ')
                visitNode(expression.rightExpression)
            } else {
                addScript(' = null')
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
            def result = tourStack(stack,variableName)
            stack.push(keep)
            return result
        }
    }

    private variableScopingContains(variableName) {
        tourStack(variableScoping,variableName)
    }

    private allActualScopeContains(variableName) {
        tourStack(actualScope,variableName)
    }

    private boolean isVariableWithMissingScope(VariableExpression expression) {
        !expression.isThisExpression() && !allActualScopeContains(expression.name) &&
            !variableScopingContains(expression.name) && (processingClosure || processingClassMethods)
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
        if (variableScoping.peek().contains(expression.name) && !(actualScopeContains(expression.name))) {
            addScript(addPrefixOrPostfixIfNeeded("${org.grooscript.JsNames.GS_OBJECT}."+expression.name))
        } else if (variableStaticScoping.peek().contains(expression.name) && !(actualScopeContains(expression.name))) {
            addScript(addPrefixOrPostfixIfNeeded(translateClassName(classNameStack.peek())+'.'+expression.name))
        } else {
            if (isVariableWithMissingScope(expression) && !isDeclaringVariable) {
                addScript("${org.grooscript.JsNames.GS_FIND_SCOPE}('${addPrefixOrPostfixIfNeeded(expression.name)}', this)")
            } else {
                addScript(addPrefixOrPostfixIfNeeded(expression.name))
            }
        }
    }

    private writeFunctionWithLeftAndRight(functionName, expression) {
        addScript("${functionName}(")
        upgradedExpresion(expression.leftExpression)
        addScript(', ')
        upgradedExpresion(expression.rightExpression)
        addScript(')')
    }

    private processBinaryExpression(BinaryExpression expression) {

        //println 'Binary->'+expression.text + ' - '+expression.operation.text
        //Getting a range from a list
        if (expression.operation.text == '[' && expression.rightExpression instanceof RangeExpression) {
            addScript("${org.grooscript.JsNames.GS_RANGE_FROM_LIST}(")
            upgradedExpresion(expression.leftExpression)
            addScript(", ")
            visitNode(expression.rightExpression.getFrom())
            addScript(", ")
            visitNode(expression.rightExpression.getTo())
            addScript(')')
        //leftShift and rightShift function
        } else if (expression.operation.text == '<<' || expression.operation.text == '>>') {
            def nameFunction = expression.operation.text == '<<' ? 'leftShift' : 'rightShift'
            addScript("${org.grooscript.JsNames.GS_METHOD_CALL}(")
            upgradedExpresion(expression.leftExpression)
            addScript(",'${nameFunction}', ${org.grooscript.JsNames.GS_LIST}([")
            upgradedExpresion(expression.rightExpression)
            addScript(']))')
        //Regular Expression exact match all
        } else if (expression.operation.text == '==~') {
            addScript("${org.grooscript.JsNames.GS_EXACT_MATCH}(")
            upgradedExpresion(expression.leftExpression)
            addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                addScript('/')
                processConstantExpression(expression.rightExpression,false)
                addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            addScript(')')
        //A matcher of regular expresion
        } else if (expression.operation.text == '=~') {
            addScript("${org.grooscript.JsNames.GS_REG_EXP}(")
            //println 'rx->'+expression.leftExpression
            upgradedExpresion(expression.leftExpression)
            addScript(',')
            //If is a regular expresion /fgsg/, comes like a contantExpresion fgsg, we keep /'s for javascript
            if (expression.rightExpression instanceof ConstantExpression) {
                addScript('/')
                processConstantExpression(expression.rightExpression,false)
                addScript('/')
            } else {
                upgradedExpresion(expression.rightExpression)
            }

            addScript(')')
        //Equals
        } else if (expression.operation.text == '==') {
            writeFunctionWithLeftAndRight(org.grooscript.JsNames.GS_EQUALS, expression)
        //in
        } else if (expression.operation.text == 'in') {
            writeFunctionWithLeftAndRight(org.grooscript.JsNames.GS_IN, expression)
        //Spaceship operator <=>
        } else if (expression.operation.text == '<=>') {
            writeFunctionWithLeftAndRight(org.grooscript.JsNames.GS_SPACE_SHIP, expression)
        //instanceof
        } else if (expression.operation.text == 'instanceof') {
            addScript("${org.grooscript.JsNames.GS_INSTANCE_OF}(")
            upgradedExpresion(expression.leftExpression)
            addScript(', "')
            upgradedExpresion(expression.rightExpression)
            addScript('")')
        //Multiply
        } else if (expression.operation.text == '*') {
            writeFunctionWithLeftAndRight(org.grooscript.JsNames.GS_MULTIPLY, expression)
        //Plus
        } else if (expression.operation.text == '+') {
            writeFunctionWithLeftAndRight(org.grooscript.JsNames.GS_PLUS, expression)
        //Minus
        } else if (expression.operation.text == '-') {
            writeFunctionWithLeftAndRight(org.grooscript.JsNames.GS_MINUS, expression)
        } else {

            //Execute setter if available
            if (expression.leftExpression instanceof PropertyExpression &&
                    (expression.operation.text in ['=', '+=', '-=']) &&
                !(expression.leftExpression instanceof AttributeExpression)) {

                PropertyExpression pe = (PropertyExpression)expression.leftExpression
                addScript("${org.grooscript.JsNames.GS_SET_PROPERTY}(")
                upgradedExpresion(pe.objectExpression)
                addScript(',')
                upgradedExpresion(pe.property)
                addScript(',')
                if (expression.operation.text == '+=') {
                    processPropertyExpression(expression.leftExpression)
                    addScript(' + ')
                } else if (expression.operation.text == '-=') {
                    processPropertyExpression(expression.leftExpression)
                    addScript(' - ')
                }
                upgradedExpresion(expression.rightExpression)
                addScript(')')

            } else {
                //println ' other->'+expression.text
                //If we are assigning a variable, and don't exist in scope, we add to it
                if (expression.operation.text=='=' && expression.leftExpression instanceof VariableExpression
                    && !allActualScopeContains(expression.leftExpression.name) &&
                        !variableScopingContains(expression.leftExpression.name)) {
                    addToActualScope(expression.leftExpression.name)
                }

                //If is a boolean operation, we have to apply groovyTruth
                //Left
                if (expression.operation.text in ['&&', '||']) {
                    addScript '('
                    handExpressionInBoolean(expression.leftExpression)
                    addScript ')'
                } else {
                    upgradedExpresion(expression.leftExpression)
                }
                //Operator
                //println 'Operator->'+expression.operation.text
                addScript(' '+expression.operation.text+' ')
                //Right
                //println 'Right->'+expression.rightExpression
                if (expression.operation.text in ['&&','||']) {
                    addScript '('
                    handExpressionInBoolean(expression.rightExpression)
                    addScript ')'
                } else {
                    upgradedExpresion(expression.rightExpression)
                }
                if (expression.operation.text=='[') {
                    addScript(']')
                }
            }
        }
    }

    //Adding () for operators order, can spam loads of ()
    private upgradedExpresion(expresion) {
        if (expresion instanceof BinaryExpression) {
            addScript('(')
        }
        visitNode(expresion)
        if (expresion instanceof BinaryExpression) {
            addScript(')')
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
            addScript('"'+value+'"')
        } else {
            addScript(expression.value)
        }
    }

    private processConstantExpression(ConstantExpression expression,boolean addStuff) {
        if (expression.value instanceof String && addStuff) {
            processConstantExpression(expression)
        } else {
            addScript(expression.value)
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
                addScript(' + ')
            }
            //addScript('"')
            visitNode(exp)
            //addScript('"')

            if (expression.getValues().size() > number) {
                addScript(' + (')
                visitNode(expression.getValue(number))
                addScript(')')
            }
            number++
        }
    }

    private processNotExpression(NotExpression expression) {
        addScript('!')
        visitNode(expression.expression)
    }

    private processConstructorCallExpression(ConstructorCallExpression expression) {

        //println 'ConstructorCallExpression->'+expression.type.name + ' super? '+expression?.isSuperCall()
        //Super expression in constructor is allowed
        if (expression?.isSuperCall()) {
            def name = superNameStack.peek()
            //println 'processNotExpression name->'+name
            if (name == 'java.lang.Object') {
                addScript("this.${org.grooscript.JsNames.CONSTRUCTOR}")
            } else {
                addScript("this.${name}${expression.arguments.expressions.size()}")
            }
        } else if (expression.type.name=='java.util.Date') {
            addScript(org.grooscript.JsNames.GS_DATE)
        } else if (expression.type.name=='groovy.util.Expando') {
            addScript(org.grooscript.JsNames.GS_EXPANDO)
        } else if (expression.type.name=='java.util.Random') {
            addScript(org.grooscript.JsNames.GS_RANDOM)
        } else if (expression.type.name=='java.util.HashSet') {
            addScript(org.grooscript.JsNames.GS_SET)
        } else if (expression.type.name=='groovy.lang.ExpandoMetaClass') {
            addScript(org.grooscript.JsNames.GS_EXPANDO_META_CLASS)
        } else if (expression.type.name=='java.lang.StringBuffer') {
            addScript(org.grooscript.JsNames.GS_STRING_BUFFER)
        } else {
            if (expression.type.name.startsWith('java.') || expression.type.name.startsWith('groovy.util.')) {
                throw new Exception('Not support type '+expression.type.name)
            }
            //Constructor have name with number of params on it
            def name = translateClassName(expression.type.name)
            addScript(name)
        }
        visitNode(expression.arguments)
    }

    private processArgumentListExpression(ArgumentListExpression expression,boolean withParenthesis) {
        if (withParenthesis) {
            addScript '('
        }
        int count = expression?.expressions?.size()
        expression.expressions?.each {
            visitNode(it)
            count--
            if (count) addScript ', '
        }
        if (withParenthesis) {
            addScript ')'
        }

    }

    private processArgumentListExpression(ArgumentListExpression expression) {
        processArgumentListExpression(expression,true)
    }

    private processObjectExpressionFromProperty(PropertyExpression expression) {
        if (expression.objectExpression instanceof ClassExpression) {
            addScript(translateClassName(expression.objectExpression.type.name))
        } else {
            visitNode(expression.objectExpression)
        }
    }

    private processPropertyExpressionFromProperty(PropertyExpression expression) {
        if (expression.property instanceof GStringExpression) {
            visitNode(expression.property)
        } else {
            addScript('"')
            "process${expression.property.class.simpleName}"(expression.property,false)
            addScript('"')
        }
    }

    private processPropertyExpression(PropertyExpression expression) {

        //println 'Pe->'+expression.objectExpression
        //println 'Pro->'+expression.property

        //If metaClass property we ignore it, javascript permits add directly properties and methods
        if (expression.property instanceof ConstantExpression && expression.property.value == 'metaClass') {
            if (expression.objectExpression instanceof VariableExpression) {

                if (expression.objectExpression.name=='this') {
                    addScript('this')
                } else {

                    //I had to add variable = ... cause gSmetaClass changing object and sometimes variable don't change
                    addScript("(${expression.objectExpression.name} = ${org.grooscript.JsNames.GS_META_CLASS}(")
                    visitNode(expression.objectExpression)
                    addScript('))')
                }
            } else {
                if (expression.objectExpression instanceof ClassExpression &&
                    (expression.objectExpression.type.name.startsWith('java.') ||
                     expression.objectExpression.type.name.startsWith('groovy.'))) {
                    throw new Exception("Not allowed access metaClass of Groovy or Java types (${expression.objectExpression.type.name})")
                }
                addScript("${org.grooscript.JsNames.GS_META_CLASS}(")
                visitNode(expression.objectExpression)
                addScript(')')
            }
        } else if (expression.property instanceof ConstantExpression && expression.property.value == 'class') {
            visitNode(expression.objectExpression)
            addScript(".${org.grooscript.JsNames.CLASS}")
        } else {

            if (!(expression instanceof AttributeExpression)) {
                addScript("${org.grooscript.JsNames.GS_GET_PROPERTY}(")
                if (expression.objectExpression instanceof VariableExpression &&
                        expression.objectExpression.name=='this') {
                    addScript("${org.grooscript.JsNames.GS_THIS_OR_OBJECT}(this,${org.grooscript.JsNames.GS_OBJECT})")
                } else {
                    processObjectExpressionFromProperty(expression)
                }

                addScript(',')

                processPropertyExpressionFromProperty(expression)

                //If is a safe expression as item?.data, we add one more parameter
                if (expression.isSafe()) {
                    addScript(',true')
                }

                addScript(')')
            } else {

                processObjectExpressionFromProperty(expression)
                addScript('[')
                processPropertyExpressionFromProperty(expression)
                addScript(']')
            }
        }

    }

    private processMethodCallExpression(MethodCallExpression expression) {

        //println "MCE ${expression.objectExpression} - ${expression.methodAsString}"
        def addParameters = true

        //Change println for javascript function
        if (expression.methodAsString == 'println' || expression.methodAsString == 'print') {
            addScript(org.grooscript.JsNames.GS_PRINTLN)
        //Remove call method call from closures
        } else if (expression.methodAsString == 'call') {
            //println 'Calling!->'+expression.objectExpression

            if (expression.objectExpression instanceof VariableExpression) {
                addParameters = false
                def nameFunc = expression.objectExpression.text
                addScript("(${nameFunc}.delegate!=undefined?${org.grooscript.JsNames.GS_APPLY_DELEGATE}(${nameFunc},${nameFunc}.delegate,[")
                processArgumentListExpression(expression.arguments,false)
                addScript("]):${nameFunc}")
                visitNode(expression.arguments)
                addScript(")")
            } else {
                visitNode(expression.objectExpression)
            }
        //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='super') {
            addScript("${SUPER_METHOD_BEGIN}${expression.methodAsString}")
        //Function times, with a number, have to put (number) in javascript
        } else if (['times','upto','step'].contains(expression.methodAsString) && expression.objectExpression instanceof ConstantExpression) {
            addScript('(')
            visitNode(expression.objectExpression)
            addScript(')')
            addScript(".${expression.methodAsString}")
        //With
        } else if (expression.methodAsString == 'with' && expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpression(0) && expression.arguments.getExpression(0) instanceof ClosureExpression) {
            visitNode(expression.objectExpression)
            addScript(".${org.grooscript.JsNames.WITH}")
        //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression && expression.objectExpression.type.name=='java.lang.Math') {
            addScript("Math.${expression.methodAsString}")
        //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression && expression.objectExpression.type.name=='java.lang.Class' &&
                expression.methodAsString=='forName') {
            addScript("${org.grooscript.JsNames.GS_CLASS_FOR_NAME}(")
            processArgumentListExpression(expression.arguments,false)
            addScript(')')
            addParameters = false
        //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='this' && expression.methodAsString == 'use') {
            ArgumentListExpression args = expression.arguments
            addParameters = false
            addScript("${org.grooscript.JsNames.GS_CATEGORY_USE}(\"")
            addScript(translateClassName(args.expressions[0].type.name))
            addScript('",')
            visitNode(args.expressions[1])
            addScript(')')
        //Mixin Classes
        } else if (expression.objectExpression instanceof ClassExpression && expression.methodAsString == 'mixin') {
            //println 'Mixin!'
            addParameters = false
            addScript("${org.grooscript.JsNames.GS_MIXIN_CLASS}('${translateClassName(expression.objectExpression.type.name)}',")
            addScript('[')
            ArgumentListExpression args = expression.arguments
            addScript args.expressions.inject ([]) { item,expr->
                item << '"'+translateClassName(expr.type.name)+'"'
            }.join(',')
            addScript('])')
        //Mixin Objects
        } else if (expression.objectExpression instanceof PropertyExpression &&
                expression.objectExpression.property instanceof ConstantExpression &&
                expression.objectExpression.property.text == 'metaClass' &&
                expression.methodAsString == 'mixin') {
            addParameters = false
            addScript("${org.grooscript.JsNames.GS_MIXIN_OBJECT}(${expression.objectExpression.objectExpression.text},")
            addScript('[')
            ArgumentListExpression args = expression.arguments
            addScript args.expressions.inject ([]) { item,expr->
                item << '"'+translateClassName(expr.type.name)+'"'
            }.join(',')
            addScript('])')
        //Spread method call [1,2,3]*.toString()
        } else if (expression.isSpreadSafe()) {
            //println 'spreadsafe!'
            addParameters = false
            visitNode(expression.objectExpression)
            addScript(".collect(function(it) { return ${org.grooscript.JsNames.GS_METHOD_CALL}(it,'${expression.methodAsString}',${org.grooscript.JsNames.GS_LIST}([")
            processArgumentListExpression(expression.arguments,false)
            addScript(']));})')
        } else {


            //println 'Method->'+expression.methodAsString+' - '+expression.arguments.class.simpleName
            addParameters = false

            addScript("${org.grooscript.JsNames.GS_METHOD_CALL}(")
            //Object
            if (expression.objectExpression instanceof VariableExpression &&
                    expression.objectExpression.name == 'this' &&
                    variableScoping.peek()?.contains(expression.methodAsString)) {
                addScript(org.grooscript.JsNames.GS_OBJECT)
            } else {
                visitNode(expression.objectExpression)
            }

            addScript(',')
            //MethodName
            visitNode(expression.method)

            //Parameters
            addScript(",${org.grooscript.JsNames.GS_LIST}([")
            "process${expression.arguments.class.simpleName}"(expression.arguments,false)
            addScript(']))')
        }

        if (addParameters) {
            visitNode(expression.arguments)
        }
    }

    private addPlusPlusFunction(expression, isBefore) {

        //Only in mind ++ and --
        def plus = true
        if (expression.operation.text=='--') {
            plus = false
        }

        addScript("${org.grooscript.JsNames.GS_PLUS_PLUS}(")
        processObjectExpressionFromProperty(expression.expression)
        addScript(',')
        processPropertyExpressionFromProperty(expression.expression)

        addScript(",${plus},${isBefore?'true':'false'})")
    }

    private processPostfixExpression(PostfixExpression expression) {
        if (expression.operation.text in ['++','--'] && expression.expression instanceof PropertyExpression) {
            addPlusPlusFunction(expression, false)
        } else {
            postfixOperator = expression.operation.text
            visitNode(expression.expression)
            //addScript(postfixOperator)
            postfixOperator = ''
        }
    }

    private processPrefixExpression(PrefixExpression expression) {
        if (expression.expression instanceof PropertyExpression) {
            addPlusPlusFunction(expression, true)
        } else {
            prefixOperator = expression.operation.text
            //addScript(prefixOperator)
            visitNode(expression.expression)
            prefixOperator = ''
        }
    }

    private processReturnStatement(ReturnStatement statement) {
        //variableScoping.peek().add(gSgotResultStatement)
        returnScoping.add(true)
        addScript('return ')
        visitNode(statement.expression)
    }

    private processClosureExpression(ClosureExpression expression) {
        processClosureExpression(expression, true)
    }

    private processClosureExpression(ClosureExpression expression, boolean addItDefault) {

        addScript("function(")

        processingClosure = true
        putFunctionParametersAndBody(expression,false,addItDefault)
        processingClosure = false

        indent--
        //actualScope = []
        removeTabScript()
        addScript('}')

    }

    private processIfStatement(IfStatement statement) {
        addScript('if (')
        visitNode(statement.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        if (statement.ifBlock instanceof BlockStatement) {
            processBlockStament(statement.ifBlock,false)
        } else {
            //println 'if2->'+ statement.ifBlock.text
            visitNode(statement.ifBlock)
            addLine()
        }

        indent--
        removeTabScript()
        addScript('}')
        if (statement.elseBlock && !(statement.elseBlock instanceof EmptyStatement)) {
            //println 'Else->'+statement.elseBlock.text
            addScript(' else {')
            indent++
            addLine()
            if (statement.elseBlock instanceof BlockStatement) {
                processBlockStament(statement.elseBlock,false)
            } else {
                //println 'if2->'+ statement.ifBlock.text
                visitNode(statement.elseBlock)
                addLine()
            }
            indent--
            removeTabScript()
            addScript('}')
        }
    }

    private processMapExpression(MapExpression expression) {
        addScript("${org.grooscript.JsNames.GS_MAP}()")
        expression.mapEntryExpressions?.each { ep ->
            addScript(".add(");
            visitNode(ep.keyExpression)
            addScript(",");
            visitNode(ep.valueExpression)
            addScript(")");
        }
    }

    private processListExpression(ListExpression expression) {
        addScript("${org.grooscript.JsNames.GS_LIST}([")
        def first = true
        expression?.expressions?.each { it ->
            if (!first) {
                addScript(' , ')
            } else {
                first = false
            }
            visitNode(it)
        }
        addScript('])')
    }

    private processRangeExpression(RangeExpression expression) {
        addScript("${org.grooscript.JsNames.GS_RANGE}(")
        visitNode(expression.from)
        addScript(", ")
        visitNode(expression.to)
        addScript(', '+expression.isInclusive())
        addScript(')')
    }

    private processForStatement(ForStatement statement) {

        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //We change this for in...  for a call lo closure each, that works fine in javascript
            visitNode(statement?.collectionExpression)
            addScript('.each(function(')
            visitNode(statement.variable)

        } else {
            addScript 'for ('
            visitNode(statement?.collectionExpression)
        }
        addScript ') {'
        indent++
        addLine()

        visitNode(statement?.loopBlock)

        indent--
        removeTabScript()
        addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            addScript(')')
        }
    }

    private processClosureListExpression(ClosureListExpression expression) {
        boolean first = true
        expression?.expressions?.each { it ->
            if (!first) {
                addScript(' ; ')
            }
            first = false
            visitNode(it)
        }
    }

    private processParameter(Parameter parameter) {
        //println 'Initial->'+parameter.getInitialExpression()
        addScript(parameter.name)
    }

    private processTryCatchStatement(TryCatchStatement statement) {
        //Try block
        addScript('try {')
        indent++
        addLine()

        visitNode(statement?.tryStatement)

        indent--
        removeTabScript()
        //Catch block
        addScript('} catch (')
        if (statement?.catchStatements[0]) {
            visitNode(statement?.catchStatements[0].variable)
        } else {
            addScript('e')
        }
        addScript(') {')
        indent++
        addLine()
        //Only process first catch
        visitNode(statement?.catchStatements[0])

        indent--
        removeTabScript()
        addScript('}')
    }

    private processCatchStatement(CatchStatement statement) {
        processBlockStament(statement.code,false)
    }

    private processTernaryExpression(TernaryExpression expression) {
        //println 'Ternary->'+expression.text
        addScript('(')
        visitNode(expression.booleanExpression)
        addScript(' ? ')
        visitNode(expression.trueExpression)
        addScript(' : ')
        visitNode(expression.falseExpression)
        addScript(')')
    }

    private getSwitchExpression(Expression expression,String varName) {

        if (expression instanceof ClosureExpression) {
            addClosureSwitchInitialization = true
            processClosureExpression(expression,true)
            addScript('()')
        } else {
            addScript("${varName} === ")
            visitNode(expression)
        }

    }

    private processSwitchStatement(SwitchStatement statement) {

        def varName = org.grooscript.JsNames.SWITCH_VAR_NAME + switchCount++

        addScript('var '+varName+' = ')
        visitNode(statement.expression)
        addScript(';')
        addLine()

        def first = true

        statement.caseStatements?.each { it ->
            if (first) {
                addScript("if (")
                first = false
            } else {
                addScript("} else if (")
            }
            getSwitchExpression(it.expression,varName)
            addScript(') {')
            indent++
            addLine()
            visitNode(it?.code)
            indent--
            removeTabScript()
        }
        if (statement.defaultStatement) {
            addScript('} else {')
            indent++
            addLine()
            visitNode(statement.defaultStatement)
            indent--
            removeTabScript()
        }

        addScript('}')

        switchCount--
    }

    private processCaseStatement(CaseStatement statement) {
        addScript 'case '
        visitNode(statement?.expression)
        addScript ':'
        indent++
        addLine()
        visitNode(statement?.code)
        indent--
        removeTabScript()
    }

    private processBreakStatement(BreakStatement statement) {
        if (switchCount==0) {
            addScript('break')
        }
    }

    private processWhileStatement(WhileStatement statement) {
        addScript('while (')
        visitNode(statement.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        visitNode(statement.loopBlock)
        indent--
        removeTabScript()
        addScript('}')
    }

    private processTupleExpression(TupleExpression expression, withParenthesis = true) {
        if (withParenthesis) {
            addScript('(')
        }
        addScript("${org.grooscript.JsNames.GS_MAP}()")
        expression.expressions.each {
            visitNode(it)
            if (withParenthesis) {
                addScript(')')
            }
        }
    }

    private processNamedArgumentListExpression(NamedArgumentListExpression expression) {
        expression.mapEntryExpressions.eachWithIndex { MapEntryExpression exp,i ->
            addScript('.add(')
            visitNode(exp.keyExpression)
            addScript(',')
            visitNode(exp.valueExpression)
            addScript(')')
        }
    }

    private processBitwiseNegationExpression(BitwiseNegationExpression expression) {
        addScript("/${expression.text}/")
    }

    private processEnum(ClassNode node) {

        addLine()

        //Push name in stack
        variableScoping.push([])

        addScript("var ${translateClassName(node.name)} = {")

        indent ++
        addLine()

        //addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass

        //Fields
        def number = 1
        node?.fields?.each { it->
            if (!['MIN_VALUE','MAX_VALUE','$VALUES'].contains(it.name)) {
                addScript("${it.name} : ${number++},")
                addLine()
                variableScoping.peek().add(it.name)
            }
        }

        //Methods
        node?.methods?.each { //println 'method->'+it;

            if (!['values','next','previous','valueOf','$INIT','<clinit>'].contains(it.name)) {

                variableScoping.peek().add(it.name)
                addScript("${it.name} : function(")
                putFunctionParametersAndBody(it,false,true)

                indent--
                removeTabScript()
                addScript('},')
                addLine()

            }
        }

        indent --
        addLine()
        addScript('}')
        addLine()

        //Remove variable class names from the list
        variableScoping.pop()
    }

    private processClassExpression(ClassExpression expression) {
        addScript(translateClassName(expression.text))
    }

    private processThrowStatement(ThrowStatement statement) {
        addScript('throw "Exception"')
        //println 'throw expression'+statement.expression.text
    }

    private processStaticMethodCallExpression(StaticMethodCallExpression expression) {
        addScript("${expression.ownerType.name}.${expression.method}")
        visitNode(expression.arguments)
    }

    private processElvisOperatorExpression(ElvisOperatorExpression expression) {
        addScript("${org.grooscript.JsNames.GS_ELVIS}(")
        visitNode(expression.booleanExpression)
        addScript(' , ')
        visitNode(expression.trueExpression)
        addScript(' , ')
        visitNode(expression.falseExpression)
        addScript(')')
    }

    private processAttributeExpression(AttributeExpression expression) {
        processPropertyExpression(expression)
    }

    private processCastExpression(CastExpression expression) {
        if (expression.type.nameWithoutPackage == 'Set' && expression.expression instanceof ListExpression) {
            addScript("${org.grooscript.JsNames.GS_SET}(")
            visitNode(expression.expression)
            addScript(')')
        } else {
            throw new Exception('Casting not supported for '+expression.type.name)
        }
    }

    private processMethodPointerExpression(MethodPointerExpression expression) {
        visitNode(expression.expression)
        addScript('[')
        visitNode(expression.methodName)
        addScript(']')
    }

    private processSpreadExpression(SpreadExpression expression) {
        addScript("new ${org.grooscript.JsNames.GS_SPREAD}(")
        visitNode(expression.expression)
        addScript(')')
    }

    private processSpreadMapExpression(SpreadMapExpression expression) {
        addScript("'${org.grooscript.JsNames.SPREAD_MAP}'")
    }

    private processEmptyExpression(EmptyExpression expression) {
        //Nothing to do
    }

    private visitNode(expression) {
        "process${expression.class.simpleName}"(expression)
    }
}

package org.grooscript

import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.ast.stmt.*
import org.codehaus.groovy.ast.expr.*
import org.grooscript.util.Util
import org.grooscript.util.GsConsole
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.CompilationUnit

/**
 * JFL 27/08/12
 */
class GsConverter {

    //Indent for pretty print
    def indent
    def static final TAB = '  '
    def String resultScript
    def Stack<String> classNameStack = new Stack<String>()
    def Stack<String> superNameStack = new Stack<String>()
    //Use for variable scoping, for class variable names and function names mainly
    def Stack variableScoping = new Stack()
    def Stack variableStaticScoping = new Stack()
    def Stack returnScoping = new Stack()
    //def actualScope = []
    //Use por function variable names
    def Stack actualScope = new Stack()
    //def String gSgotResultStatement = 'gSgotResultStatement'
    def String superMethodBegin = 'super_'
    def boolean processingClosure = false

    def inheritedVariables = [:]
    //def methodVariableNames
    //def scriptScope

    //Where code of native functions stored, as a map. Used for GsNative annotation
    def nativeFunctions

    //Adds a console info if activated
    def consoleInfo = false

    //Control switch inside switch
    def switchCount = 0
    def addClosureSwitchInitialization = false

    //We get this function names from unused_functions.groovy
    //Not now, changed, maybe in future can use a file for define that
    def assertFunction
    def printlnFunction

    //Conversion Options
    def addClassNames = false
    def convertDependencies = true

    //Constant names for javascript out
    def static final GS_OBJECT = 'gSobject'

    //When true, we dont add this no variables
    //TODO remove this variable properly
    //def dontAddMoreThis

    /**
     * Constructor
     * @return
     */
    def GsConverter() {
        initFunctionNames()
    }

    def private initFunctionNames() {
        assertFunction = 'gSassert'
        printlnFunction = 'gSprintln'
    }

    def private addToActualScope(variableName) {
        if (!actualScope.isEmpty()) {
            actualScope.peek().add(variableName)
        }
    }

    def private actualScopeContains(variableName) {
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

        return toJs(script,null)

    }

    /**
     * Converts Groovy script to Javascript
     * @param String script in groovy
     * @param String classPath to add to classpath
     * @return String script in javascript
     */
    def toJs(String script,Object classPath) {
        def result
        //Classpath must be a String or a list
        if (classPath && !(classPath instanceof String || classPath instanceof Collection)) {
            throw new Exception('The classpath must be a String or a List')
        }
        //Script not empty plz!
        def phase = 0
        if (script) {

            try {

                nativeFunctions = Util.getNativeFunctions(script)

                //def AstBuilder ast
                def list

                //System.getProperty("java.class.path", ".").tokenize(File.pathSeparator).each {
                //    println '->'+it
                //}

                if (classPath) {

                    //println 'cp->'+ classPath
                    list = getAstFromText(script,classPath)
                    //println 'list->'+ list

                } else {
                    //list = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS,script)
                    list = getAstFromText(script,null)
                }

                phase++
                result = processAstListToJs(list)
            } catch (e) {
                //println 'Exception in conversion ->'+e.message
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
     * Get AST tree from code, add classpath to compilation
     * @param text
     * @param classpath
     * @return
     */
    def getAstFromText(text,Object classpath) {

        //By default, convertDependencies = true
        //All the imports in a file are added to the source to be compiled, if not added, compiler fails
        def classesToConvert = []
        if (!convertDependencies) {
            def matcher = text =~ /\bclass\s+(\w+)\s*\{/
            matcher.each {
                //println 'Matcher1->'+it[1]
                classesToConvert << it[1]
            }
        }

        def scriptClassName = "script" + System.currentTimeMillis()
        GroovyClassLoader classLoader = new GroovyClassLoader()
        //Add classpath to classloader
        if (classpath) {
            if (classpath instanceof Collection) {
                classpath.each {
                    classLoader.addClasspath(it)
                }
            } else {
                classLoader.addClasspath(classpath)
            }
        }
        GroovyCodeSource codeSource = new GroovyCodeSource(text, scriptClassName + ".groovy", "/groovy/script")
        CompilerConfiguration conf = CompilerConfiguration.DEFAULT
        //Add classpath to configuration
        if (classpath && classpath instanceof String) {
            conf.setClasspath(classpath)
        }
        if (classpath && classpath instanceof Collection) {
            conf.setClasspathList(classpath)
        }
        CompilationUnit cu = new CompilationUnit(conf, codeSource.codeSource, classLoader)
        cu.addSource(codeSource.getName(), text);
        cu.compile(CompilePhase.SEMANTIC_ANALYSIS.phaseNumber)
        // collect all the ASTNodes into the result, possibly ignoring the script body if desired
        def list = cu.ast.modules.inject([]) {List acc, ModuleNode node ->
            //node.statementBlock
            //println ' Acc node->'+node+ ' - '+ node.getStatementBlock().getStatements().size()
            if (node.statementBlock) {
            //if (node.statementBlock && node.statementBlock.statements?.size()>0) {
                acc.add(node.statementBlock)

                node.classes?.each { ClassNode cl ->

                    /*
                    println 'add->'+cl.name
                    cl.metaClass.methods.each { MetaMethod method ->
                        if (method.name.startsWith('is')) {
                            if (method.name != 'isDerivedFrom') {
                                println ' '+method.name+ ' = '+cl."${method.name}"()
                            }
                        }
                    }*/

                    if (!(cl.name == scriptClassName) && cl.isPrimaryClassNode()) {
                        //println 'add->'+cl.name

                        //If we dont want to convert dependencies in the result
                        if (!convertDependencies) {
                            if (classesToConvert.contains(cl.name)) {
                                acc << cl
                            }
                        } else {
                            acc << cl
                        }
                    }
                }
            }
            acc
        }
        return list
    }

    /**
     * Process an AST List from Groovy code to javascript script
     * @param list
     * @return
     */
    def private processAstListToJs(list) {
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
            //Store all classes here
            def classList = []
            //We process blocks at the end
            def listBlocks = []
            list.each { it ->
                //println '------------------------------------it->'+it
                if (it instanceof BlockStatement) {
                    //scriptScope = true
                    listBlocks << it
                    //processBlockStament(it,false)
                } else if (it instanceof ClassNode) {
                    //scriptScope = false
                    classList << it
                    //processClassNode(it)
                } else {
                    GsConsole.error("AST Node not supported (${it.class.simpleName}).")
                }
            }
            //Process list of classes
            if (classList) {
                if (consoleInfo) {
                    println 'Processing class list...'
                }
                processClassList(classList)
                if (consoleInfo) {
                    println 'Done class list.'
                }
            }
            //Process blocks after
            listBlocks?.each { it->
                processBlockStament(it,false)
            }

            result = resultScript
        }
        //println 'res->'+ result
        result
    }

    //Process list of classes in correct order, inheritance order
    //Save list of variables for inheritance
    def private processClassList(List<ClassNode> list) {

        def finalList = []
        def extraClasses = []
        def enumClasses = []
        while ((finalList.size()+extraClasses.size()+enumClasses.size())<list.size()) {

            list.each { ClassNode it ->
                //println 'it->'+it.name+' super - '+it.superClass.name+
                if (it.superClass.name=='java.lang.Object')  {
                    if (!finalList.contains(it.name)) {
                        //println 'Adding '+it.name
                        finalList.add(it.name)
                    }
                } else {
                    //Expando allowed
                    if (it.superClass.name=='groovy.lang.Script') {
                        extraClasses.add(it.name)
                    } else {

                        //If father in the list, we can add it
                        if (finalList.contains(it.superClass.name)) {
                            //println 'Adding 2 '+it.name
                            finalList.add(it.name)
                        } else {

                            //Looking for superclass, only accepts superclass a class in same script
                            if (it.superClass.name.indexOf('.')>=0) {
                                if (it.superClass.name=='java.lang.Enum') {
                                    //processEnum(it)
                                    enumClasses.add(it.name)
                                } else {
                                    throw new Exception('Inheritance not Allowed on '+it.superClass.class.name)
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
                println '  Processing class '+nameClass
            }

            //println 'Class->'+nameClass
            processClassNode(list.find { ClassNode it ->
                return it.name == nameClass
            })

            if (consoleInfo) {
                println '  Processing class done.'
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
    def private addConditionConstructorExecution(numberArguments,paramList) {

        addScript("if (arguments.length==${numberArguments}) {")
        addScript("${GS_OBJECT}.${translateClassName(classNameStack.peek())}${numberArguments}")

        addScript '('
        def count = 0
        paramList?.each { param ->
            //"process${param.class.simpleName}"(param)
            //count--
            if (count>0) addScript ', '
            addScript("arguments[${count}]")
            count++
        }
        addScript ')'

        addScript('; }')
        addLine()
    }

    def private translateClassName(String name) {
        def result = name
        def i
        while ((i = result.indexOf('.'))>=0) {
            result = result.substring(i+1)
        }

        result
    }

    def private processScriptClassNode(ClassNode node) {

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
                "process${it.initialExpression.class.simpleName}"(it.initialExpression)
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

            //println 'method->'+it.name;

            if (it.name!='main' && it.name!='run') {

                //Add too method names to variable scoping
                variableScoping.peek().add(it.name)

                processBasicFunction(it.name,it,false)

                //processMethodNode(it,false)
            }
        }

        addLine()

        //Remove variable class names from the list
        variableScoping.pop()
        actualScope.pop()

    }

    def private addPropertyToClass(fieldOrProperty,isStatic) {

        def previous = GS_OBJECT
        if (isStatic) {
            previous = ''
        }

        if (fieldOrProperty.initialExpression) {
            addScript("${previous}.${fieldOrProperty.name} = ")
            "process${fieldOrProperty.initialExpression.class.simpleName}"(fieldOrProperty.initialExpression)
            addScript(';')
            addLine()
        } else {
            addScript("${previous}.${fieldOrProperty.name} = null;")
            addLine()
        }
    }

    def private addPropertyStaticToClass(String name) {

        addScript("${GS_OBJECT}.__defineGetter__('${name}', function(){ return ${translateClassName(classNameStack.peek())}.${name}; });")
        addLine()
        addScript("${GS_OBJECT}.__defineSetter__('${name}', function(gSval){ ${translateClassName(classNameStack.peek())}.${name} = gSval; });")
        addLine()
    }

    def private haveAnnotationNonConvert(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            //If dont have to convert then exit
            if (it.getClassNode().nameWithoutPackage=='GsNotConvert') {
                exit = true
            }
        }
        return exit
    }

    def private haveAnnotationNative(annotations) {
        boolean exit = false
        annotations.each { AnnotationNode it ->
            //If native then exit
            if (it.getClassNode().nameWithoutPackage=='GsNative') {
                exit = true
            }
        }
        return exit
    }

    def private processClassNode(ClassNode node) {

        //Exit if dont have to convert
        if (haveAnnotationNonConvert(node.annotations)) {
            return 0
        }

        //Starting class conversion

        //Ignoring modifiers
        //visitModifiers(node.modifiers)

        //println "class-> $node.name"
        addLine()

        //Push name in stack
        classNameStack.push(node.name)
        variableScoping.push([])
        variableStaticScoping.push([])

        //addScript("function gsCreate${translateClassName(node.name)}() {")
        addScript("function ${translateClassName(node.name)}() {")

        indent ++
        addLine()

        superNameStack.push(node.superClass.name)

        //Allowed inheritance
        if (node.superClass.name != 'java.lang.Object') {
            //println 'Allowed!'+ node.superClass.class.name
            addScript("var ${GS_OBJECT} = ${translateClassName(node.superClass.name)}();")

            //We add to this class scope variables of fathers
            variableScoping.peek().addAll(inheritedVariables[node.superClass.name])
        } else {
            addScript("var ${GS_OBJECT} = inherit(gsBaseClass,'${translateClassName(node.name)}');")
        }
        addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass

        //Add class name and super name
        if (addClassNames) {
            addClassNames(node.name, (node.superClass.name != 'java.lang.Object'?node.superClass.name:null))
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
            //println 'Field->'+field.name+' owner:'+field.owner+' t:'+node.name + ' p:'+node.syntheticPublic

        }

        //Save variables from this class for use in 'son' classes
        inheritedVariables.put(node.name,variableScoping.peek())
        //Ignoring fields
        //node?.fields?.each { println 'field->'+it  }

        //Methods
        node?.methods?.each { MethodNode it -> //println 'method->'+it;

            //Even if not converting, we add name to scope
            //if (!haveAnnotationNonConvert(it.annotations)) {
                //Add too method names to variable scoping
                if (!it.isStatic()) {
                    variableScoping.peek().add(it.name)
                }
            //}
        }
        node?.methods?.each { MethodNode it -> //println 'method->'+it;

            if (!haveAnnotationNonConvert(it.annotations)) {
                //Process the methods
                if (haveAnnotationNative(it.annotations)) {
                    addScript("${GS_OBJECT}.${it.name} = function(")
                    processFunctionOrMethodParameters(it,false,false)
                    //addScript(") {")
                    addScript(nativeFunctions[it.name])
                    addLine()
                    indent--
                    removeTabScript()
                    addScript('}')
                    addLine()

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

                    addScript("${GS_OBJECT}.${it.name} = function(${params.join(',')}) { return ${translateClassName(node.name)}.${it.name}(")
                    addScript(params.join(','))
                    addScript("); }")
                    addLine()
                }
            }
        }

        //Constructors
        //If no constructor with 1 parameter, we create 1 that get a map, for put value on properties
        boolean has1parameterConstructor = false
        //boolean has0parameterConstructor = false
        node?.declaredConstructors?.each { MethodNode it->
            def numberArguments = it.parameters?.size()
            if (numberArguments==1) {
                has1parameterConstructor = true
            }
            //if (it.parameters?.size()==0) {
            //    has0parameterConstructor = true
            //}
            processMethodNode(it,true)

            addConditionConstructorExecution(numberArguments,it.parameters)

        }
        if (!has1parameterConstructor) {
            addScript("${GS_OBJECT}.${translateClassName(node.name)}1 = function(map) { gSpassMapToObject(map,this); return this;};")
            addLine()
            addScript("if (arguments.length==1) {${GS_OBJECT}.${translateClassName(node.name)}1(arguments[0]); }")
            addLine()
        }

        addLine()
        indent --
        addScript("return ${GS_OBJECT};")
        addLine()
        addScript('};')
        addLine()

        //Static methods
        node?.methods?.each { MethodNode method ->
            if (!haveAnnotationNonConvert(method.annotations)) {
                if (method.isStatic()) {
                    //println 'Static!'
                    processBasicFunction("${translateClassName(node.name)}.${method.name}",method,false)
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

        //Finish class conversion
    }

    def addClassNames(actualClassName,superClassName) {

        if (superClassName) {
            addScript("var temp = ${GS_OBJECT}.gSclass;")
            addLine()
            addScript("${GS_OBJECT}.gSclass = [];")
            addLine()
            addScript("${GS_OBJECT}.gSclass.superclass = temp;")
            addLine()
        } else {
            addScript("${GS_OBJECT}.gSclass = [];")
            addLine()
            addScript("${GS_OBJECT}.gSclass.superclass = [];")
            addLine()
            addScript("${GS_OBJECT}.gSclass.superclass.name= 'java.lang.Object';")
            addLine()
            addScript("${GS_OBJECT}.gSclass.superclass.simpleName= 'Object';")
            addLine()
        }
        addScript("${GS_OBJECT}.gSclass.name = '${actualClassName}';")
        addLine()
        addScript("${GS_OBJECT}.gSclass.simpleName = '${translateClassName(actualClassName)}';")
        addLine()

    }

    def private processFunctionOrMethodParameters(functionOrMethod, boolean isConstructor,boolean addItInParameter) {

        boolean first = true
        boolean lastParameterCanBeMore = false

        //Parameters with default values if not shown
        def initalValues = [:]

        //If no parameters, we add it by defaul
        if (addItInParameter && (!functionOrMethod.parameters || functionOrMethod.parameters.size()==0)) {
            addScript('it')
            //actualScope.add('it')
            addToActualScope('it')
            //variableScoping.peek().add('it')
        } else {

            functionOrMethod.parameters?.eachWithIndex { Parameter param, index ->

                //If the last parameter is an Object[] then, maybe, can get more parameters as optional
                if (param.type.name=='[Ljava.lang.Object;' && index+1==functionOrMethod.parameters.size()) {
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
                //actualScope.add(param.name)
                addToActualScope(param.name)
                //variableScoping.peek().add(param.name)
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
            "process${value.class.simpleName}"(value)
            addScript(';')
            addLine()
        }

        //We add initialization of it inside switch closure function
        if (addClosureSwitchInitialization) {
            def name = 'gSswitch' + (switchCount - 1)
            addScript("if (it === undefined) it = ${name};")
            addLine()
            addClosureSwitchInitialization = false
        }

        if (lastParameterCanBeMore) {
            def Parameter lastParameter = functionOrMethod.parameters.last()
            addScript("if (arguments.length==${functionOrMethod.parameters.size()}) { ${lastParameter.name}=gSlist([arguments[${functionOrMethod.parameters.size()}-1]]); }")
            addLine()
            addScript("if (arguments.length<${functionOrMethod.parameters.size()}) { ${lastParameter.name}=gSlist([]); }")
            addLine()
            addScript("if (arguments.length>${functionOrMethod.parameters.size()}) {")
            addLine()
            addScript("  ${lastParameter.name}=gSlist([${lastParameter.name}]);")
            addLine()
            addScript("  for (gScount=${functionOrMethod.parameters.size()};gScount<arguments.length;gScount++) {")
            addLine()
            addScript("    ${lastParameter.name}.add(arguments[gScount]);")
            addLine()
            addScript("  }")
            addLine()
            addScript("}")
            addLine()
        }
    }

    def private putFunctionParametersAndBody(functionOrMethod, boolean isConstructor, boolean addItDefault) {

        //actualScope = []
        actualScope.push([])
        //variableScoping.push([])

        processFunctionOrMethodParameters(functionOrMethod,isConstructor,addItDefault)

        //println 'Closure '+expression+' Code:'+expression.code
        if (functionOrMethod.code instanceof BlockStatement) {
            processBlockStament(functionOrMethod.code,!isConstructor)
        } else {
            GsConsole.error("FunctionOrMethod Code not supported (${functionOrMethod.code.class.simpleName})")
        }

        //actualScope = []
        actualScope.pop()
        //variableScoping.pop()
    }

    def private processBasicFunction(name, method, isConstructor) {

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

    def private processMethodNode(MethodNode method,isConstructor) {

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
            //println 'Fucking name-'+name
        }

        processBasicFunction("${GS_OBJECT}.$name",method,isConstructor)

    }

    /**
     * Process an AST Block
     * @param block
     * @param addReturn put 'return ' before last statement
     * @return
     */
    def private processBlockStament(block,addReturn) {
        if (block) {
            def number = 1
            //println 'Block->'+block
            if (block instanceof EmptyStatement) {
                println 'BlockEmpty->'+block.text
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
    def private processBlockStatement(block) {
        processBlockStament(block,false)
    }

    /**
     * Add a line to javascript output
     * @param script
     * @param line
     * @return
     */
    def private addLine() {
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
    def private addScript(text) {
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
    def private addScriptAt(text,position) {
        resultScript = resultScript.substring(0,position) + text + resultScript.substring(position)
    }

    /**
     * Remove a TAB from current javascript output
     * @return
     */
    def private removeTabScript() {
        resultScript = resultScript[0..resultScript.size()-1-TAB.size()]
    }

    /**
     * Get actual position in javascript output
     * @return
     */
    def private getSavePoint() {
        return resultScript.size()
    }

    /**
     * Process a statement, adding ; at the end
     * @param statement
     */
    def private void processStatement(Statement statement) {

        //println "statement (${statement.class.simpleName})->"+statement+' - '+statement.text
        "process${statement.class.simpleName}"(statement)

        //Adds ;
        if (resultScript) {
            resultScript += ';'
        }
        addLine()
        //println 'end statement'
    }

    def private processAssertStatement(AssertStatement statement) {
        Expression e = statement.booleanExpression
        addScript(assertFunction)
        addScript('(')
        "process${e.class.simpleName}"(e)
        if (statement.getMessageExpression() && !(statement.messageExpression instanceof EmptyExpression)) {
            addScript(', ')
            "process${statement.messageExpression.class.simpleName}"(statement.messageExpression)
        }
        addScript(')')
    }

    def private processBooleanExpression(BooleanExpression expression) {
        //println 'BooleanExpression->'+expression
        //println 'BooleanExpression Inside->'+expression.expression

        //Groovy truth is a bit different, empty collections return false, we fix that here
        if (expression.expression instanceof VariableExpression || expression.expression instanceof PropertyExpression ||
            (expression.expression instanceof NotExpression &&
                    expression.expression.expression &&
                    (expression.expression.expression instanceof VariableExpression || expression.expression.expression instanceof PropertyExpression))) {
            if (expression.expression instanceof NotExpression) {
                addScript('!gSbool(')
                "process${expression.expression.expression.class.simpleName}"(expression.expression.expression)
            } else {
                addScript('gSbool(')
                "process${expression.expression.class.simpleName}"(expression.expression)
            }
            addScript(')')
        } else {
            "process${expression.expression.class.simpleName}"(expression.expression)
        }
    }

    def private processExpressionStatement(ExpressionStatement statement) {
        //println 'begin->'+statement
        Expression e = statement.expression
        "process${e.class.simpleName}"(e)
    }

    def private processDeclarationExpression(DeclarationExpression expression) {
        //println 'l->'+expression.leftExpression
        //println 'r->'+expression.rightExpression
        //println 'v->'+expression.getVariableExpression()

        if (expression.isMultipleAssignmentDeclaration()) {
            TupleExpression tuple = (TupleExpression)(expression.getLeftExpression())
            def number = 0;
            tuple.expressions.each { Expression expr ->
                //println '->'+expr
                if (expr instanceof VariableExpression && expr.name!='_') {
                    addScript('var ')
                    processVariableExpression(expr)
                    addScript(' = ')
                    "process${expression.rightExpression.class.simpleName}"(expression.rightExpression)
                    addScript(".getAt(${number})")
                    if (number<tuple.expressions.size()) {
                        addScript(';')
                    }
                }
                number++
            }
        } else {

            //actualScope.add(expression.variableExpression.name)
            addToActualScope(expression.variableExpression.name)
            //variableScoping.add(expression.variableExpression.name)

            addScript('var ')
            processVariableExpression(expression.variableExpression)

            if (!(expression.rightExpression instanceof EmptyExpression)) {
                addScript(' = ')
                "process${expression.rightExpression.class.simpleName}"(expression.rightExpression)
            } else {
                addScript(' = null')
            }

        }
    }

    def private tourStack(Stack stack,variableName) {
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

    def private variableScopingContains(variableName) {
        //println 'vs('+variableName+')->'+fuckStack(variableScoping,variableName) //variableScoping.peek()?.contains(variableName) //variableScoping.search(variableName)
        //println 'actualScope->'+actualScope
        return tourStack(variableScoping,variableName)
    }

    def allActualScopeContains(variableName) {
        //println 'as('+variableName+')->'+fuckStack(actualScope,variableName) //variableScoping.peek()?.contains(variableName) //variableScoping.search(variableName)
        return tourStack(actualScope,variableName)
    }

    def private processVariableExpression(VariableExpression expression) {

        //println "name:${expression.name} - scope:${variableScoping.peek()} - isThis - ${expression.isThisExpression()}"
        //if (!variableScoping.peek().contains(v.name) && !declaringVariable &&!dontAddMoreThis && variableScoping.size()>1) {
        if (variableScoping.peek().contains(expression.name) && !(actualScopeContains(expression.name))) {
            addScript("${GS_OBJECT}."+expression.name)
        } else if (variableStaticScoping.peek().contains(expression.name) && !(actualScopeContains(expression.name))) {
            addScript(translateClassName(classNameStack.peek())+'.'+expression.name)
        } else {
            if (processingClosure && !expression.isThisExpression()
                    && !allActualScopeContains(expression.name) && !variableScopingContains(expression.name)) {
                addScript('this.')
            }
            addScript(expression.name)
        }
    }

    /**
     *
     * @param b
     * @return
     */
    def private processBinaryExpression(BinaryExpression expression) {

        //println 'Binary->'+expression.text + ' - '+expression.operation.text
        //Getting a range from a list
        if (expression.operation.text=='[' && expression.rightExpression instanceof RangeExpression) {
            addScript('gSrangeFromList(')
            upgradedExpresion(expression.leftExpression)
            addScript(", ")
            "process${expression.rightExpression.getFrom().class.simpleName}"(expression.rightExpression.getFrom())
            addScript(", ")
            "process${expression.rightExpression.getTo().class.simpleName}"(expression.rightExpression.getTo())
            addScript(')')
        //LeftShift function
        } else if (expression.operation.text=='<<') {
            //We call add function
            //println 'le->'+ expression.leftExpression
            upgradedExpresion(expression.leftExpression)
            addScript('.leftShift(')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        //Regular Expression exact match all
        } else if (expression.operation.text=='==~') {
            addScript('gSexactMatch(')
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
        } else if (expression.operation.text=='=~') {
            addScript('gSregExp(')
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
        } else if (expression.operation.text=='==') {
                addScript('gSequals(')
                upgradedExpresion(expression.leftExpression)
                addScript(', ')
                upgradedExpresion(expression.rightExpression)
                addScript(')')
        //in
        } else if (expression.operation.text=='in') {
            addScript('gSin(')
            upgradedExpresion(expression.leftExpression)
            addScript(', ')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        //Spaceship operator <=>
        } else if (expression.operation.text=='<=>') {
            addScript('gSspaceShip(')
            upgradedExpresion(expression.leftExpression)
            addScript(', ')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        //instanceof
        } else if (expression.operation.text=='instanceof') {
            addScript('gSinstanceOf(')
            upgradedExpresion(expression.leftExpression)
            addScript(', "')
            upgradedExpresion(expression.rightExpression)
            addScript('")')
        //Multiply
        } else if (expression.operation.text=='*') {
            addScript('gSmultiply(')
            upgradedExpresion(expression.leftExpression)
            addScript(', ')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        //Plus
        } else if (expression.operation.text=='+') {
            addScript('gSplus(')
            upgradedExpresion(expression.leftExpression)
            addScript(', ')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        //Minus
        } else if (expression.operation.text=='-') {
            addScript('gSminus(')
            upgradedExpresion(expression.leftExpression)
            addScript(', ')
            upgradedExpresion(expression.rightExpression)
            addScript(')')
        } else {

            //Execute setter if available
            if (expression.leftExpression instanceof PropertyExpression &&
                    (expression.operation.text in ['=','+=','-=']) &&
                !(expression.leftExpression instanceof AttributeExpression)) {
                    //(expression.leftExpression instanceof PropertyExpression && !expression.leftExpression instanceof AttributeExpression)) {

                PropertyExpression pe = (PropertyExpression)expression.leftExpression
                //println 'pe->'+pe.propertyAsString
                addScript('gSsetProperty(')
                upgradedExpresion(pe.objectExpression)
                addScript(',')
                //addScript(pe.propertyAsString)
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
                //Left
                upgradedExpresion(expression.leftExpression)
                //Operator
                //println 'Operator->'+expression.operation.text
                addScript(' '+expression.operation.text+' ')
                //Right
                //println 'Right->'+expression.rightExpression
                upgradedExpresion(expression.rightExpression)
                if (expression.operation.text=='[') {
                    addScript(']')
                }
            }
        }
    }

    //Adding () for operators order, can spam loads of ()
    def private upgradedExpresion(expresion) {
        if (expresion instanceof BinaryExpression) {
            addScript('(')
        }
        "process${expresion.class.simpleName}"(expresion)
        if (expresion instanceof BinaryExpression) {
            addScript(')')
        }
    }

    def private processConstantExpression(ConstantExpression expression) {
        //println 'ConstantExpression->'+expression.text
        if (expression.value instanceof String) {
            //println 'Value->'+expression.value+'<'+expression.value.endsWith('\n')
            def String value = ''
            if (expression.value.startsWith('\n')) {
                value = '\\n'
            }
            //if (expression.value.size()>0 && expression.value.endsWith('\n') && !value.endsWith('\n')) {
            //    value += '\\n'
            //}
            def list = []
            expression.value.eachLine {
                if (it) list << it
            }
            value += list.join('\\n')
            //expression.value.eachLine { if (it) value += it }
            //println 'Before->'+value
            //value = value.replaceAll('"','\\\\u0022')
            value = value.replaceAll('"','\\\\"')
            //println 'After->'+value+'<'+value.endsWith('\n')
            if (expression.value.endsWith('\n') && !value.endsWith('\n')) {
                value += '\\n'
            }
            addScript('"'+value+'"')
        } else {
            addScript(expression.value)
        }

    }

    def private processConstantExpression(ConstantExpression expression,boolean addStuff) {
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
    def private processGStringExpression(GStringExpression expression) {

        def number = 0
        expression.getStrings().each {   exp ->
            if (number>0) {
                addScript(' + ')
            }
            //addScript('"')
            "process${exp.class.simpleName}"(exp)
            //addScript('"')

            if (expression.getValues().size() > number) {
                addScript(' + (')
                "process${expression.getValue(number).class.simpleName}"(expression.getValue(number))
                addScript(')')
            }
            number++
        }
    }

    def private processNotExpression(NotExpression expression) {
        addScript('!')
        "process${expression.expression.class.simpleName}"(expression.expression)
    }

    def private processConstructorCallExpression(ConstructorCallExpression expression) {

        //println 'ConstructorCallExpression->'+expression.type.name + ' super? '+expression?.isSuperCall()
        //Super expression in constructor is allowed
        if (expression?.isSuperCall()) {
            def name = superNameStack.peek()
            //println 'processNotExpression name->'+name
            if (name == 'java.lang.Object') {
                addScript('this.gSconstructor')
            } else {
                addScript("this.${name}${expression.arguments.expressions.size()}")
            }
        } else if (expression.type.name=='java.util.Date') {
            addScript('gSdate')
        } else if (expression.type.name=='groovy.util.Expando') {
            addScript('gSexpando')
        } else if (expression.type.name=='java.util.Random') {
            addScript('gSrandom')
        } else if (expression.type.name=='java.util.HashSet') {
            addScript('gSset')
        } else {
            //println 'processConstructorCallExpression->'+ expression.type.name
            if (expression.type.name.startsWith('java.util.') || expression.type.name.startsWith('groovy.util.')) {
                throw new Exception('Not support type '+expression.type.name)
            }
            //Constructor have name with number of params on it
            //addScript("gsCreate${expression.type.name}().${expression.type.name}${expression.arguments.expressions.size()}")
            def name = translateClassName(expression.type.name)
            //addScript("gsCreate${name}")
            addScript(name)
        }
        "process${expression.arguments.class.simpleName}"(expression.arguments)
    }

    def private processArgumentListExpression(ArgumentListExpression expression,boolean withParenthesis) {
        if (withParenthesis) {
            addScript '('
        }
        int count = expression?.expressions?.size()
        expression.expressions?.each {
            "process${it.class.simpleName}"(it)
            count--
            if (count) addScript ', '
        }
        if (withParenthesis) {
            addScript ')'
        }

    }

    def private processArgumentListExpression(ArgumentListExpression expression) {
        processArgumentListExpression(expression,true)
    }

    def private processObjectExpressionFromProperty(PropertyExpression expression) {
        if (expression.objectExpression instanceof ClassExpression) {
            addScript(translateClassName(expression.objectExpression.type.name))
        } else {
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
        }
    }

    def private processPropertyExpressionFromProperty(PropertyExpression expression) {
        if (expression.property instanceof GStringExpression) {
            "process${expression.property.class.simpleName}"(expression.property)
        } else {
            addScript('"')
            "process${expression.property.class.simpleName}"(expression.property,false)
            addScript('"')
        }
    }

    def private processPropertyExpression(PropertyExpression expression) {

        //println 'Pe->'+expression.objectExpression

        //If metaClass property we ignore it, javascript permits add directly properties and methods
        if (expression.property instanceof ConstantExpression && expression.property.value == 'metaClass') {
            if (expression.objectExpression instanceof VariableExpression) {

                if (expression.objectExpression.name=='this') {
                    addScript('this')
                } else {

                    //I had to add variable = ... cause gSmetaClass changing object and sometimes variable don't change
                    addScript("(${expression.objectExpression.name} = gSmetaClass(")
                    "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
                    addScript('))')
                }
            } else {
                addScript('gSmetaClass(')
                "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
                addScript(')')
            }
        } else if (expression.property instanceof ConstantExpression && expression.property.value == 'class') {
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
            addScript('.gSclass')
        } else {

            if (!(expression instanceof AttributeExpression)) {
                //println 'expr->'+expression
                addScript('gSgetProperty(')

                if (expression.objectExpression instanceof VariableExpression &&
                        expression.objectExpression.name=='this') {
                    addScript("gSthisOrObject(this,${GS_OBJECT})")
                } else {
                    processObjectExpressionFromProperty(expression)
                }

                addScript(',')

                processPropertyExpressionFromProperty(expression)

                //If is a safe expresion as item?.data, we add one more parameter
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

    def private processMethodCallExpression(MethodCallExpression expression) {
        //println "MCE ${expression.objectExpression} - ${expression.methodAsString}"
        //if (expression.objectExpression instanceof VariableExpression) {
        //    if (expression.objectExpression.name == 'this') {
        //        //dontAddMoreThis = true
        //    }
        //}

        def addParameters = true

        //Change println for javascript function
        if (expression.methodAsString == 'println' || expression.methodAsString == 'print') {
            addScript(printlnFunction)
        //Remove call method call from closures
        } else if (expression.methodAsString == 'call') {
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
        //Dont use dot(.) in super calls
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='super') {
            addScript("${superMethodBegin}${expression.methodAsString}")
        //Function times, with a number, have to put (number) in javascript
        } else if (['times','upto','step'].contains(expression.methodAsString) && expression.objectExpression instanceof ConstantExpression) {
            addScript('(')
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
            addScript(')')
            addScript(".${expression.methodAsString}")
        //With
        } else if (expression.methodAsString == 'with' && expression.arguments instanceof ArgumentListExpression &&
                expression.arguments.getExpression(0) && expression.arguments.getExpression(0) instanceof ClosureExpression) {
            "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
            addScript(".gSwith")
        //Using Math library
        } else if (expression.objectExpression instanceof ClassExpression && expression.objectExpression.type.name=='java.lang.Math') {
            addScript("Math.${expression.methodAsString}")
        //Adding class.forName
        } else if (expression.objectExpression instanceof ClassExpression && expression.objectExpression.type.name=='java.lang.Class' &&
                expression.methodAsString=='forName') {
            addScript('gSclassForName(')
            //println '->'+expression.arguments[0]
            "process${expression.arguments.class.simpleName}"(expression.arguments,false)
            addScript(')')
            addParameters = false
        //this.use {} Categories
        } else if (expression.objectExpression instanceof VariableExpression &&
                expression.objectExpression.name=='this' && expression.methodAsString == 'use') {
            //println 'Category going!'
            ArgumentListExpression args = expression.arguments
            //println 'cat size()->'+args.expressions.size()
            //println '0->'+ args.expressions[0].type.name

            addParameters = false
            addScript('gScategoryUse("')
            addScript(translateClassName(args.expressions[0].type.name))
            addScript('",')
            "process${args.expressions[1].class.simpleName}"(args.expressions[1])
            addScript(')')
        } else {


            //println 'Method->'+expression.methodAsString+' - '+expression.arguments.class.simpleName
            addParameters = false

            addScript('gSmethodCall(')
            //Object
            if (expression.objectExpression instanceof VariableExpression &&
                    expression.objectExpression.name == 'this' &&
                    variableScoping.peek()?.contains(expression.methodAsString)) {
                //Remove this and put ${GS_OBJECT} for variable scoping
                addScript(GS_OBJECT)
            } else {
                "process${expression.objectExpression.class.simpleName}"(expression.objectExpression)
            }

            addScript(',"')
            //MethodName
            addScript(expression.methodAsString)

            addScript('",gSlist([')
            //Parameters
            "process${expression.arguments.class.simpleName}"(expression.arguments,false)

            addScript(']))')
        }

        if (addParameters) {
            "process${expression.arguments.class.simpleName}"(expression.arguments)
        }

        //dontAddMoreThis = false
    }

    def private processPostfixExpression(PostfixExpression expression) {

        if (expression.expression instanceof PropertyExpression) {

            //Only in mind ++ and --
            def plus = true
            if (expression.operation.text=='--') {
                plus = false
            }
            addScript('gSplusplus(')
            processObjectExpressionFromProperty(expression.expression)
            addScript(',')
            processPropertyExpressionFromProperty(expression.expression)
            addScript(",${plus},false)")
        } else {

            "process${expression.expression.class.simpleName}"(expression.expression)
            addScript(expression.operation.text)

        }
    }

    def private processPrefixExpression(PrefixExpression expression) {
        if (expression.expression instanceof PropertyExpression) {
            def plus = true
            if (expression.operation.text=='--') {
                plus = false
            }
            addScript('gSplusplus(')
            processObjectExpressionFromProperty(expression.expression)
            addScript(',')
            processPropertyExpressionFromProperty(expression.expression)
            addScript(",${plus},true)")
        } else {
            addScript(expression.operation.text)
            "process${expression.expression.class.simpleName}"(expression.expression)
        }
    }

    def private processReturnStatement(ReturnStatement statement) {
        //variableScoping.peek().add(gSgotResultStatement)
        returnScoping.add(true)
        addScript('return ')
        "process${statement.expression.class.simpleName}"(statement.expression)
    }

    def private processClosureExpression(ClosureExpression expression) {
        processClosureExpression(expression, true)
    }

    def private processClosureExpression(ClosureExpression expression, boolean addItDefault) {

        addScript("function(")

        processingClosure = true
        putFunctionParametersAndBody(expression,false,addItDefault)
        processingClosure = false

        indent--
        //actualScope = []
        removeTabScript()
        addScript('}')

    }

    def private processIfStatement(IfStatement statement) {
        addScript('if (')
        "process${statement.booleanExpression.class.simpleName}"(statement.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        if (statement.ifBlock instanceof BlockStatement) {
            processBlockStament(statement.ifBlock,false)
        } else {
            //println 'if2->'+ statement.ifBlock.text
            "process${statement.ifBlock.class.simpleName}"(statement.ifBlock)
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
                "process${statement.elseBlock.class.simpleName}"(statement.elseBlock)
                addLine()
            }
            indent--
            removeTabScript()
            addScript('}')
        }
    }

    def private processMapExpression(MapExpression expression) {
        addScript('gSmap()')
        expression.mapEntryExpressions?.each { ep ->
            addScript(".add(");
            "process${ep.keyExpression.class.simpleName}"(ep.keyExpression)
            addScript(",");
            "process${ep.valueExpression.class.simpleName}"(ep.valueExpression)
            addScript(")");
        }
    }

    def private processListExpression(ListExpression expression) {
        addScript('gSlist([')
        //println 'List->'+l.expressions
        //l.each { println it}
        def first = true
        expression?.expressions?.each { it ->
            if (!first) {
                addScript(' , ')
            } else {
                first = false
            }
            "process${it.class.simpleName}"(it)
        }
        addScript('])')
    }

    def private processRangeExpression(RangeExpression expression) {
        addScript('gSrange(')

        //println 'Is inclusive->'+r.isInclusive()
        "process${expression.from.class.simpleName}"(expression.from)
        addScript(", ")
        "process${expression.to.class.simpleName}"(expression.to)
        addScript(', '+expression.isInclusive())
        addScript(')')
    }

    def private processForStatement(ForStatement statement) {

        //????
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            //println 'DUMMY!-'+statement.variable
            //We change this for in...  for a call lo closure each, that works fine in javascript
            //"process${statement.variable.class.simpleName}"(statement.variable)
            //addScript ' in '

            "process${statement?.collectionExpression?.class.simpleName}"(statement?.collectionExpression)
            addScript('.each(function(')
            "process${statement.variable.class.simpleName}"(statement.variable)

        } else {
            addScript 'for ('
            //println 'collectionExpression-'+ statement?.collectionExpression.text
            "process${statement?.collectionExpression?.class.simpleName}"(statement?.collectionExpression)
        }
        addScript ') {'
        indent++
        addLine()

        "process${statement?.loopBlock?.class.simpleName}"(statement?.loopBlock)

        indent--
        removeTabScript()
        addScript('}')
        if (statement?.variable != ForStatement.FOR_LOOP_DUMMY) {
            addScript(')')
        }
    }

    def private processClosureListExpression(ClosureListExpression expression) {
        //println 'ClosureListExpression-'+expression.text
        boolean first = true
        expression?.expressions?.each { it ->
            if (!first) {
                addScript(' ; ')
            }
            first = false
            "process${it.class.simpleName}"(it)
        }
    }

    def private processParameter(Parameter parameter) {
        //println 'Initial->'+parameter.getInitialExpression()
        addScript(parameter.name)
    }

    def private processTryCatchStatement(TryCatchStatement statement) {
        //Try block
        addScript('try {')
        indent++
        addLine()

        "process${statement?.tryStatement.class.simpleName}"(statement?.tryStatement)

        indent--
        removeTabScript()
        //Catch block
        addScript('} catch (')
        if (statement?.catchStatements[0]) {
            "process${statement?.catchStatements[0].variable.class.simpleName}"(statement?.catchStatements[0].variable)
        } else {
            addScript('e')
        }
        addScript(') {')
        indent++
        addLine()
        //Only process first catch
        "process${statement?.catchStatements[0]?.class.simpleName}"(statement?.catchStatements[0])

        indent--
        removeTabScript()
        addScript('}')
    }

    def private processCatchStatement(CatchStatement statement) {
        processBlockStament(statement.code,false)
    }

    def private processTernaryExpression(TernaryExpression expression) {
        //println 'Ternary->'+expression.text
        addScript('(')
        "process${expression.booleanExpression.class.simpleName}"(expression.booleanExpression)
        addScript(' ? ')
        "process${expression.trueExpression.class.simpleName}"(expression.trueExpression)
        addScript(' : ')
        "process${expression.falseExpression.class.simpleName}"(expression.falseExpression)
        addScript(')')
    }

    def private getSwitchExpression(Expression expression,String varName) {

        if (expression instanceof ClosureExpression) {
            def ClosureExpression clos = (ClosureExpression)expression
            addClosureSwitchInitialization = true
            "process${expression.class.simpleName}"(expression,true)
            addScript('()')
        } else {
            addScript("${varName} === ")
            "process${expression.class.simpleName}"(expression)
        }

    }

    def private processSwitchStatement(SwitchStatement statement) {

        def varName = 'gSswitch' + switchCount++

        addScript('var '+varName+' = ')
        "process${statement.expression.class.simpleName}"(statement.expression)
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
            //println 'Exp->'+it?.expression
            addScript(') {')
            indent++
            addLine()
            "process${it?.code.class.simpleName}"(it?.code)
            indent--
            removeTabScript()

            //"process${it.class.simpleName}"(it)
        }
        if (statement.defaultStatement) {
            addScript('} else {')
            indent++
            addLine()
            "process${statement.defaultStatement.class.simpleName}"(statement.defaultStatement)
            indent--
            removeTabScript()
        }

        addScript('}')

        /* changed javascript switch to if
        addScript('switch (')
        "process${statement.expression.class.simpleName}"(statement.expression)
        addScript(') {')
        indent++
        addLine()
        statement.caseStatements?.each { it ->
            "process${it.class.simpleName}"(it)
            //addScript('break;')
        }
        if (statement.defaultStatement) {
            addScript('default :')
            "process${statement.defaultStatement.class.simpleName}"(statement.defaultStatement)
        }
        indent--
        removeTabScript()
        addScript('}')
        //addLine()
        */

        switchCount--
    }

    def private processCaseStatement(CaseStatement statement) {
        addScript 'case '
        "process${statement?.expression.class.simpleName}"(statement?.expression)
        addScript ':'
        indent++
        addLine()
        "process${statement?.code.class.simpleName}"(statement?.code)
        indent--
        removeTabScript()
        //addLine()
    }

    def private processBreakStatement(BreakStatement statement) {
        if (switchCount==0) {
            addScript('break')
        }
        //addLine()
    }

    def private processWhileStatement(WhileStatement statement) {
        addScript('while (')
        "process${statement.booleanExpression.class.simpleName}"(statement.booleanExpression)
        addScript(') {')
        indent++
        addLine()
        "process${statement.loopBlock.class.simpleName}"(statement.loopBlock)
        indent--
        removeTabScript()
        addScript('}')
    }

    def private processTupleExpression(TupleExpression expression, withParenthesis = true) {
        //println 'Tuple->'+expression.text
        //expression.expressions.each { println '-'+it}
        if (withParenthesis) {
            addScript('(')
        }
        addScript('gSmap()')
        expression.expressions.each {
            "process${it.class.simpleName}"(it)
            if (withParenthesis) {
                addScript(')')
            }
        }
    }

    def private processNamedArgumentListExpression(NamedArgumentListExpression expression) {
        expression.mapEntryExpressions.eachWithIndex { MapEntryExpression exp,i ->
            //println 'key->'+ exp.keyExpression
            addScript('.add(')
            "process${exp.keyExpression.class.simpleName}"(exp.keyExpression)
            addScript(',')
            "process${exp.valueExpression.class.simpleName}"(exp.valueExpression)
            addScript(')')
        }
        //"process${expression.transformExpression().class.simpleName}"(expression.transformExpression())
    }

    def private processBitwiseNegationExpression(BitwiseNegationExpression expression) {
        //addScript("gSpattern('/${expression.text}/')")
        addScript("/${expression.text}/")
    }

    def private processEnum(ClassNode node) {

        addLine()

        //Push name in stack
        variableScoping.push([])

        addScript("var ${translateClassName(node.name)} = {")

        indent ++
        addLine()

        //Allowed inheritance
        //addScript('var ${GS_OBJECT} = inherit(gsBaseClass);')

        //addLine()
        //ignoring generics and interfaces and extends atm
        //visitGenerics node?.genericsTypes
        //node.interfaces?.each {
        //visitType node.superClass

        //Fields
        def numero = 1
        node?.fields?.each { it->
            if (!['MIN_VALUE','MAX_VALUE','$VALUES'].contains(it.name)) {
                addScript("${it.name} : ${numero++},")
                addLine()
                variableScoping.peek().add(it.name)
            }
        }

        //Methods
        node?.methods?.each { //println 'method->'+it;

            if (!['values','next','previous','valueOf','$INIT','<clinit>'].contains(it.name)) {

                //println 'Method->'+ it.name
                variableScoping.peek().add(it.name)
                //processMethodNode(it,false)
                //processBasicFunction(it.name,it,false)

                addScript("${it.name} : function(")
                putFunctionParametersAndBody(it,false,true)

                indent--
                removeTabScript()
                addScript('},')
                addLine()

            }
        }

        //addLine()

        indent --
        //addScript("return ${GS_OBJECT};")
        addLine()
        addScript('}')
        addLine()

        //Remove variable class names from the list
        variableScoping.pop()

        //Pop name in stack

    }

    /*
    def processFieldExpression(FieldExpression expression) {
        //println '->'+expression.fieldName

        FieldNode node = expression.field
        println '->'+node.name
    }

    def processStaticMethodCallExpression(StaticMethodCallExpression expression) {
        println 'StaticMethodCallExpression->'+expression.text
    }*/

    def private processClassExpression(ClassExpression expression) {
        //println 'ClassExpression-'+ expression.text
        //addScript(translateClassName(expression.text))
        if (expression.text.startsWith('java.lang')) {
            addScript(expression.text)
        } else {
            addScript(translateClassName(expression.text))
        }
    }

    def private processThrowStatement(ThrowStatement statement) {
        addScript('throw "Exception"')
        //println 'throw expression'+statement.expression.text
    }

    def private processStaticMethodCallExpression(StaticMethodCallExpression expression) {

        //println 'SMCE->'+expression.text
        addScript("${expression.ownerType.name}.${expression.method}")
        "process${expression.arguments.class.simpleName}"(expression.arguments)
    }

    def private processElvisOperatorExpression(ElvisOperatorExpression expression) {
        //println 'Elvis->'+expression.text
        //println 'true->'+expression.trueExpression
        //println 'false->'+expression.falseExpression

        addScript('gSelvis(')
        "process${expression.booleanExpression.class.simpleName}"(expression.booleanExpression)
        addScript(' , ')
        "process${expression.trueExpression.class.simpleName}"(expression.trueExpression)
        addScript(' , ')
        "process${expression.falseExpression.class.simpleName}"(expression.falseExpression)
        addScript(')')

    }

    def private processAttributeExpression(AttributeExpression expression) {
        processPropertyExpression(expression)
        //addScript(expression.propertyAsString)
        //upgradedExpresion(expression.property)
    }

    def private processCastExpression(CastExpression expression) {
        if (expression.type.nameWithoutPackage == 'Set' && expression.expression instanceof ListExpression) {
            addScript('gSset(')
            "process${expression.expression.class.simpleName}"(expression.expression)
            addScript(')')
        } else {
            throw new Exception('Casting not supported for '+expression.type.name)
        }
    }

    def private processMethodPointerExpression(MethodPointerExpression expression) {
        //println 'Exp-'+expression.expression
        //println 'dynamic-'+expression.dynamic
        //println 'methodName-'+expression.methodName
        "process${expression.expression.class.simpleName}"(expression.expression)
        addScript('[')
        "process${expression.methodName.class.simpleName}"(expression.methodName)
        addScript(']')
    }

    def private processSpreadExpression(SpreadExpression expression) {
        //println 'exp-'+expression
        addScript('new GSspread(')
        "process${expression.expression.class.simpleName}"(expression.expression)
        addScript(')')
    }

    def private processSpreadMapExpression(SpreadMapExpression expression) {
        //println 'Map exp-'+expression.text
        addScript('"gSspreadMap"')
    }

    def methodMissing(String name, Object args) {
        def message
        if (name?.startsWith('process')) {
            message = 'Conversion not supported for '+name.substring(7)
        } else {
            message = 'Error methodMissing '+name
        }
        GsConsole.error(message)
        throw new Exception(message)

    }

}

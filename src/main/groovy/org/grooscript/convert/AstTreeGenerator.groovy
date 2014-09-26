package org.grooscript.convert

import org.codehaus.groovy.ast.InnerClassNode
import org.grooscript.util.Util
import org.grooscript.util.GrooScriptException
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.grooscript.util.GsConsole

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

/**
 * User: jorgefrancoleza
 * Date: 13/10/13
 */
class AstTreeGenerator {

    def consoleInfo
    def convertDependencies
    def classPath
    def customization

    /**
     * Get AST tree from code
     * @param text
     * @return
     */
    def fromText(String text) {
        if (consoleInfo) {
            GsConsole.message('Converting string code to AST')
            GsConsole.message(' Option convertDependencies: ' + convertDependencies)
            GsConsole.message(' Classpath: ' + classPath)
            GsConsole.message(' Customization: ' + customization)
        }
        //All the imports in a file are added to the source to be compiled, if not added, compiler fails
        def classesToConvert = []
        if (!convertDependencies) {
            ['class', 'enum'].each { type ->
                def matcher = text =~ /\b${type}\s+(\w+)/
                matcher.each {
                    classesToConvert << it[1]
                }
            }
        }
        def traitsToConvert = []
        if (!convertDependencies) {
            def matcher = text =~ /\btrait\s+(\w+)/
            matcher.each {
                traitsToConvert << it[1]
            }
        }

        def scriptClassName = 'script' + System.currentTimeMillis()
        CompilerConfiguration conf = new CompilerConfiguration()
        //Add customization to configuration
        if (customization) {
            withConfig(conf, customization)
        }

        GroovyClassLoader classLoader =
                new GroovyClassLoader(Thread.currentThread().getContextClassLoader(), conf)

        //Add classpath to classloader
        if (classPath) {
            //Classpath must be a String or a list
            if (!(classPath instanceof String || classPath instanceof Collection)) {
                throw new GrooScriptException('The classpath must be a String or a List')
            }

            if (classPath instanceof Collection) {
                classPath.each {
                    classLoader.addClasspath(it)
                }
            } else {
                classLoader.addClasspath(classPath)
            }
        }
        GroovyCodeSource codeSource = new GroovyCodeSource(text, scriptClassName + '.groovy', '/groovy/script')

        CompilationUnit cu = compiledCode(conf, codeSource, classLoader, text)

        [
            listAstNodes(cu.ast.modules, scriptClassName, classesToConvert, traitsToConvert),
            nativeFunctionsFromOtherSources(cu.ast.modules)
        ]
    }

    private compiledCode(conf, codeSource, classLoader, text) {
        try {
            def compilationUnit = new CompilationUnit(conf, codeSource.codeSource, classLoader)
            compilationUnit.addSource(codeSource.getName(), text)
            compilationUnit.compile(CompilePhase.INSTRUCTION_SELECTION.phaseNumber)
        } catch (e) {
            GsConsole.error 'Compilation error in INSTRUCTION_SELECTION phase'
            throw e
        }

        def compilationUnitFinal = new CompilationUnit(conf, codeSource.codeSource, classLoader)
        compilationUnitFinal.addSource(codeSource.getName(), text)
        compilationUnitFinal.compile(CompilePhase.SEMANTIC_ANALYSIS.phaseNumber)
        compilationUnitFinal
    }

    private List listAstNodes(List<ModuleNode> modules, String scriptClassName,
                              List classesToConvert, List traitsToConvert) {
        // collect all the ASTNodes into the result, possibly ignoring the script body if desired
        modules.inject([]) { List listAstNodes, ModuleNode node ->
            if (node.statementBlock) {
                listAstNodes.add(node.statementBlock)

                node.classes?.each { ClassNode cl ->
                    if (consoleInfo) {
                        GsConsole.message('  Processing classNode: ' + cl.name)
                    }
                    if (!(cl.name == scriptClassName) && cl.isPrimaryClassNode()) {

                        //If we dont want to convert dependencies in the result
                        if (convertDependencies) {
                            listAstNodes << cl
                        } else {
                            if (classesToConvert.contains(cl.nameWithoutPackage)
                                    || cl.isScript() || cl instanceof InnerClassNode) {
                                listAstNodes << cl
                            } else if (traitsToConvert) {
                                if (traitsToConvert.contains(cl.nameWithoutPackage)) {
                                    listAstNodes << cl
                                } else if (cl.name.endsWith('Trait$Helper') &&
                                    traitsToConvert.contains(cl.outerClass.nameWithoutPackage)) {
                                        listAstNodes << cl
                                }
                            }
                        }
                    } else {
                        if (cl.name == scriptClassName && cl.methods) {
                            //Lets take a look to script methods, and add methods in script
                            cl.methods.each { MethodNode methodNode ->
                                if (!(methodNode.name in ['main', 'run'])) {
                                    listAstNodes << methodNode
                                }
                            }
                        }
                    }
                }
            }
            listAstNodes
        }
    }

    private List<NativeFunction> nativeFunctionsFromOtherSources(List<ModuleNode> modules) {
        modules.inject([]) { List listNativeFunctions, ModuleNode moduleNode ->
            if (moduleNode.description.startsWith('file:')) {
                File file = new File(moduleNode.description.substring(5))
                if (file && file.exists() && file.isFile()) {
                    def className = null
                    if (modules.classes.size() == 1) {
                        className = modules.classes.first().nameWithoutPackage
                    }
                    listNativeFunctions = listNativeFunctions + Util.getNativeFunctions(file.text, className)
                }
            }
            listNativeFunctions
        }
    }
}

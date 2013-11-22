package org.grooscript.convert

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
            GsConsole.message(' Option convertDependencies: '+convertDependencies)
            GsConsole.message(' Classpath: '+classPath)
            GsConsole.message(' Customization: '+customization)
        }
        //By default, convertDependencies = true
        //All the imports in a file are added to the source to be compiled, if not added, compiler fails
        def classesToConvert = []
        if (!convertDependencies) {
            def matcher = text =~ /\bclass\s+(\w+)\s*\{/
            matcher.each {
                classesToConvert << it[1]
            }
        }

        def scriptClassName = "script" + System.currentTimeMillis()
        GroovyClassLoader classLoader = new GroovyClassLoader()
        //Add classpath to classloader
        if (classPath) {
            //Classpath must be a String or a list
            if (!(classPath instanceof String || classPath instanceof Collection)) {
                throw new Exception('The classpath must be a String or a List')
            }

            if (classPath instanceof Collection) {
                classPath.each {
                    classLoader.addClasspath(it)
                }
            } else {
                classLoader.addClasspath(classPath)
            }
        }
        GroovyCodeSource codeSource = new GroovyCodeSource(text, scriptClassName + ".groovy", "/groovy/script")
        CompilerConfiguration conf = new CompilerConfiguration()
        //Add classpath to configuration
        if (classPath && classPath instanceof String) {
            conf.setClasspath(classPath)
        }
        if (classPath && classPath instanceof Collection) {
            conf.setClasspathList(classPath)
        }
        if (customization) {
            withConfig(conf, customization)
        }
        CompilationUnit cu = compiledCode(conf, codeSource, classLoader, text)

        // collect all the ASTNodes into the result, possibly ignoring the script body if desired
        def list = cu.ast.modules.inject([]) {List listAstNodes, ModuleNode node ->
            if (node.statementBlock) {
                listAstNodes.add(node.statementBlock)

                node.classes?.each { ClassNode cl ->

                    if (!(cl.name == scriptClassName) && cl.isPrimaryClassNode()) {

                        //If we dont want to convert dependencies in the result
                        if (!convertDependencies) {
                            if (classesToConvert.contains(cl.nameWithoutPackage)) {
                                listAstNodes << cl
                            }
                        } else {
                            listAstNodes << cl
                        }
                    } else {
                        if (cl.name == scriptClassName && cl.methods) {
                            //Lets take a look to script methods, and add methods in script
                            cl.methods.each { MethodNode methodNode ->
                                if (!(methodNode.name in ['main','run'])) {
                                    listAstNodes << methodNode
                                }
                            }
                        }
                    }
                }
            }
            listAstNodes
        }
        if (consoleInfo) {
            GsConsole.message('Done converting string code to AST. Number of nodes: '+list.size())
        }
        return list
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
}
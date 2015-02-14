package org.grooscript.convert.ast

import org.codehaus.groovy.ast.InnerClassNode
import org.grooscript.convert.NativeFunction
import org.grooscript.util.Util
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.grooscript.util.GsConsole

/**
 * User: jorgefrancoleza
 * Date: 13/10/13
 */
class AstTreeGenerator extends GrooscriptCompiler {

    def consoleInfo

    /**
     * Get AST tree from code
     * @param sourceCode
     * @return
     */
    List fromText(String sourceCode) {
        if (consoleInfo) {
            GsConsole.message('Converting string code to AST')
            GsConsole.message(' Classpath: ' + classPath)
            GsConsole.message(' Customization: ' + customization)
        }
        //All the imports in a file are added to the source to be compiled, if not added, compiler fails
        def classesToConvert = []
        ['class', 'enum'].each { type ->
            def matcher = sourceCode =~ /\b${type}\s+(\w+)/
            matcher.each {
                classesToConvert << it[1]
            }
        }
        //Traits
        def traitsToConvert = []
        def matcher = sourceCode =~ /\btrait\s+(\w+)/
        matcher.each {
            traitsToConvert << it[1]
        }

        def scriptClassName = defaultScriptName

        CompilationUnit cu = astCompiledCode(sourceCode, scriptClassName)

        [
            listAstNodes(cu.ast.modules, scriptClassName, classesToConvert, traitsToConvert),
            nativeFunctionsFromOtherSources(cu.ast.modules)
        ]
    }

    private CompilationUnit astCompiledCode(String sourceCode, String scriptClassName) {
        try {
            compiledCode(sourceCode, scriptClassName, CompilePhase.INSTRUCTION_SELECTION.phaseNumber)
         } catch (e) {
            GsConsole.error 'Compilation error in INSTRUCTION_SELECTION phase'
            throw e
        }
        compiledCode(sourceCode, scriptClassName)
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
                if (file?.exists() && file.isFile()) {
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

package org.grooscript.convert.ast

import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.grooscript.util.GrooScriptException

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

/**
 * Created by jorgefrancoleza on 14/2/15.
 */
class GrooscriptCompiler {

    private static int GROOSCRIPT_PHASE = CompilePhase.SEMANTIC_ANALYSIS.phaseNumber

    def classPath
    Closure customization

    protected CompilationUnit compiledCode(
            String sourceCode, String scriptClassName = defaultScriptName, int phaseNumber = GROOSCRIPT_PHASE) {
        def conf = customizedCompilerConfiguration
        def compilationUnitFinal = new CompilationUnit(conf, null, grooscriptClassLoader(conf))
        compilationUnitFinal.addSource(scriptClassName, sourceCode)
        compilationUnitFinal.compile(phaseNumber)
        compilationUnitFinal
    }

    protected String getDefaultScriptName() {
        'script' + System.currentTimeMillis()
    }

    private CompilerConfiguration getCustomizedCompilerConfiguration() {
        CompilerConfiguration conf = new CompilerConfiguration()
        //Add customization to configuration
        if (customization) {
            withConfig(conf, customization)
        }
    }

    private GroovyClassLoader grooscriptClassLoader(CompilerConfiguration conf) {
        def parent = new GroovyClassLoader()
        addClassPathToGroovyClassLoader(parent)
        GroovyClassLoader classLoader = new GroovyClassLoader(parent, conf)
        addClassPathToGroovyClassLoader(classLoader)
        classLoader
    }

    private addClassPathToGroovyClassLoader(classLoader) {
        if (classPath) {
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
    }
}

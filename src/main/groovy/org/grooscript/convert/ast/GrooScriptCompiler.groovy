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
package org.grooscript.convert.ast

import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.grooscript.util.GrooScriptException
import org.grooscript.util.GsConsole

import static org.codehaus.groovy.control.customizers.builder.CompilerCustomizationBuilder.withConfig

class GrooScriptCompiler {

    private static int GROOSCRIPT_PHASE = CompilePhase.SEMANTIC_ANALYSIS.phaseNumber

    def classpath
    Closure customization
    public boolean consoleInfo = false

    protected CompilationUnit compiledCode(
            String sourceCode, String scriptClassName = defaultScriptName, int phaseNumber = GROOSCRIPT_PHASE) {
        try {
            def conf = getCustomizedCompilerConfiguration()
            def compilationUnitFinal = new CompilationUnit(conf, null, grooscriptClassLoader(conf))
            compilationUnitFinal.addSource(scriptClassName, sourceCode)
            compilationUnitFinal.compile(phaseNumber)
            return compilationUnitFinal
        } catch (e) {
            GsConsole.error "Compilation error in ${CompilePhase.phases[phaseNumber].name()} phase"
            if (consoleInfo) {
                e.printStackTrace()
            }
            throw e
        }
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

    private void addClassPathToGroovyClassLoader(GroovyClassLoader classLoader) {
        if (classpath) {
            if (!(classpath instanceof String || classpath instanceof GString || classpath instanceof Collection)) {
                throw new GrooScriptException('The classpath must be a String or a List')
            }

            if (classpath instanceof Collection) {
                classpath.each {
                    classLoader.addClasspath(it)
                }
            } else {
                classLoader.addClasspath(classpath)
            }
        }
    }
}

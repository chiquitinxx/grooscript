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
package org.grooscript.convert.util

import org.grooscript.util.FileSolver

import java.util.regex.Matcher

import static org.grooscript.util.Util.LINE_SEPARATOR
import static org.grooscript.util.Util.SEP

class RequireJsFileGenerator {

    static final DEFINE_MODULE = 'define(function () {' + LINE_SEPARATOR
    static final END_MODULE = '});'
    FileSolver fileSolver

    void generate(RequireJsTemplate requireTemplate) {
        fileSolver.saveFile(
                destinationFilePath(requireTemplate.requireFolder, requireTemplate.destinationFile),
                template(requireTemplate)
        )
    }

    private String destinationFilePath(String destFolder, String destFile) {
        if (!destFolder || destFolder == '.') {
            destFile
        } else {
            def file = new File(destFolder + SEP + destFile)
            file.path
        }
    }

    private String template(RequireJsTemplate requireTemplate) {
        (requireTemplate.dependencies ? moduleWithDependencies(requireTemplate.dependencies) : DEFINE_MODULE) +
                requireTemplate.jsCode + LINE_SEPARATOR + returnClasses(requireTemplate.classes) + END_MODULE
    }

    private String moduleWithDependencies(List<RequireJsDependency> dependencies) {
        def paths = dependencies.collect { "'" + jsPath(it.path) + "'" }
        def moduleNames = dependencies.collect { it.name }
        'define([' + paths.join(',')+'], function ('+moduleNames.join(',')+') {' + LINE_SEPARATOR
    }

    private String returnClasses(List<String> classes) {
        if (!classes) {
            ''
        } else {
            if (classes.size() == 1) {
                "  return ${classes[0]};${LINE_SEPARATOR}"
            } else {
                def mapClasses = classes.collect { "${it}:${it}" }.join(',')
                "  return {${mapClasses}};${LINE_SEPARATOR}"
            }
        }
    }

    private String jsPath(String path) {
        path.replaceAll("\\\\", Matcher.quoteReplacement('/'))
    }
}

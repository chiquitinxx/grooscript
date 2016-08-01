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
package org.grooscript

import org.grooscript.util.GsConsole

class JsGenerator {

    static final HTML_BUILDER_SOURCE = 'src/main/groovy/org/grooscript/builder/HtmlBuilder.groovy'

    static final GROOSCRIPT_TOOLS_FILE = 'src/main/resources/META-INF/resources/grooscript-tools.js'

    static void generateGrooscriptToolsJs() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, GROOSCRIPT_TOOLS_FILE)
        GsConsole.info("File $GROOSCRIPT_TOOLS_FILE has been generated.")
    }

    private static void convertFile(File file, String destinationFile, Map conversionOptions = null) {
        new File(destinationFile).text = GrooScript.convert(file.text, conversionOptions)
    }
}

JsGenerator.generateGrooscriptToolsJs()

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

    public static final HTML_BUILDER_SOURCE = 'src/main/groovy/org/grooscript/builder/HtmlBuilder.groovy'

    public static void generateHtmlBuilder(String pathJsDestination) {
        generateGrooscriptConvertedJs(HTML_BUILDER_SOURCE, pathJsDestination)
    }

    private static void generateGrooscriptConvertedJs(String pathSource, String pathJsDestination) {
        File source = new File(pathSource)
        convertFile(source, pathJsDestination)
        GsConsole.info("File $pathJsDestination has been generated.")
    }

    private static void convertFile(File file, String destinationFile, Map conversionOptions = null) {
        new File(destinationFile).text = GrooScript.convert(file.text, conversionOptions)
    }
}

JsGenerator.generateHtmlBuilder('src/main/resources/META-INF/resources/grooscript-html-builder.js')

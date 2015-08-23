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
    static final BUILDER_FILE = 'src/main/resources/META-INF/resources/grooscript-builder.js'

    static final JQUERY_SOURCE = 'src/main/groovy/org/grooscript/jquery/GQueryImpl.groovy'
    static final JQUERY_FILE = 'src/main/resources/META-INF/resources/gQueryImpl.js'

    static final OBSERVABLE_SOURCE = 'src/main/groovy/org/grooscript/rx/Observable.groovy'
    static final OBSERVABLE_FILE = 'src/main/resources/META-INF/resources/observable.js'

    static final GROOSCRIPT_TOOLS_FILE = 'src/main/resources/META-INF/resources/grooscript-tools.js'

    static void generateGrooscriptToolsJs() {
        generateHtmlBuilder()
        generateJQuery()
        generateObservable()
        generateGrooscriptJsToolsComplete()
    }

    private static void generateHtmlBuilder() {
        File source = new File(HTML_BUILDER_SOURCE)
        convertFile(source, BUILDER_FILE)
    }

    private static void generateObservable() {
        File source = new File(OBSERVABLE_SOURCE)
        convertFile(source, OBSERVABLE_FILE)
    }

    private static void generateJQuery() {
        File source = new File(JQUERY_SOURCE)
        convertFile(source, JQUERY_FILE)
    }

    private static void generateGrooscriptJsToolsComplete() {
        GrooScript.joinListOfFiles(BUILDER_FILE, OBSERVABLE_FILE, JQUERY_FILE, GROOSCRIPT_TOOLS_FILE)
        GsConsole.info("File $GROOSCRIPT_TOOLS_FILE has been generated.")
    }

    private static void convertFile(File file, String destinationFile, Map conversionOptions = null) {
        new File(destinationFile).text = GrooScript.convert(file.text, conversionOptions)
    }
}

JsGenerator.generateGrooscriptToolsJs()

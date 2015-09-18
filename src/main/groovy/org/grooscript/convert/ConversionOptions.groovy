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
// tag::options[]
package org.grooscript.convert

enum ConversionOptions {

    CLASSPATH('classpath', null),
    CUSTOMIZATION('customization', null),
    MAIN_CONTEXT_SCOPE('mainContextScope', null),
    INITIAL_TEXT('initialText', null),
    FINAL_TEXT('finalText', null),
    RECURSIVE('recursive', false),
    ADD_GS_LIB('addGsLib', null),
    REQUIRE_JS_MODULE('requireJsModule', false),
    CONSOLE_INFO('consoleInfo', false),
    INCLUDE_DEPENDENCIES('includeDependencies', false),
    NASHORN_CONSOLE('nashornConsole', false)

    String text
    def defaultValue

    ConversionOptions(String text, defaultValue) {
        this.text = text
        this.defaultValue = defaultValue
    }
}
// end::options[]
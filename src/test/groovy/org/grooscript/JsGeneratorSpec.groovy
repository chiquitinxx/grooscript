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

import org.junit.Rule
import org.junit.rules.TemporaryFolder

class JsGeneratorSpec extends GroovyTestCase {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    void testGenerationOfHtmlBuilderFile() {
        folder.create()
        String destination = folder.newFile('htmlbuilder.js').getPath()

        assert sizeOfDestinationFile(destination) == 0

        JsGenerator.generateHtmlBuilder(destination)
        assert sizeOfDestinationFile(destination) > 100
    }

    private int sizeOfDestinationFile(String destination) {
        new File(destination).text.size()
    }
}

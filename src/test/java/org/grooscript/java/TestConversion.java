/**
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
package org.grooscript.java;

import org.grooscript.GrooScript;
import org.junit.Test;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestConversion {

    final static Charset ENCODING = StandardCharsets.UTF_8;

    @Test
    public void testConvertJavaFile() throws Exception {

        String source = "src/test/java/org/grooscript/java/Numbers.java";
        String destination = "src/test/resources";
        Path path = Paths.get(destination + "/Numbers.js");
        try {
            GrooScript.convert(source, destination);

            List<String> lines = Files.readAllLines(path, ENCODING);
            assertEquals("function Numbers() {", lines.get(0));
        } catch (Exception e) {
            fail("Don't have to fail convert java file " + source);
        } finally {
            Files.delete(path);
        }
    }
}

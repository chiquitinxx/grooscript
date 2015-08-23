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
import org.grooscript.test.JavascriptEngine;
import org.grooscript.test.NodeJs;
import org.grooscript.util.Util;
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class TestTypes {

    @Test
    public void testNumbers() throws Exception {
        //First validate java code
        assertTrue(new Numbers().validate());
        //Convert and validate javascript converted code
        evaluateInJavascript("Numbers");
    }

    @Test
    public void testMaps() throws Exception {
        assertTrue(new Maps().validate());
        evaluateInJavascript("Maps");
    }

    @Test
    public void testSets() throws Exception {
        assertTrue(new Sets().validate());
        evaluateInJavascript("Sets");
    }

    @Test
    public void testLists() throws Exception {
        assertTrue(new Lists().validate());
        evaluateInJavascript("Lists");
    }

    @Test
    public void testStrings() throws Exception {
        assertTrue(new Strings().validate());
        evaluateInJavascript("Strings");
    }

    @Test
    public void testDates() throws Exception {
        assertTrue(new Dates().validate());
        evaluateInJavascript("Dates");
    }

    final static Charset ENCODING = StandardCharsets.UTF_8;

    private void evaluateInJavascript(String nameClass) throws Exception {
        String result = GrooScript.convert(readFile(nameClass));
        result += Util.getLINE_SEPARATOR() + nameClass + "().validate();";
        //System.out.println("**-"+result);
        //Javascript engine
        assertEquals(false, JavascriptEngine.jsEval(result).getAssertFails());
        //Node.js
        assertEquals(false, new NodeJs().evaluate(result).getAssertFails());
    }

    private String readFile(String fileName) throws IOException {
        Path path = Paths.get("src/test/java/org/grooscript/java/" + fileName + ".java");
        List<String> lines = Files.readAllLines(path, ENCODING);
        String result = "";
        for (String line: lines) {
            result = result + line + Util.getLINE_SEPARATOR();
        }
        return result;
    }
}

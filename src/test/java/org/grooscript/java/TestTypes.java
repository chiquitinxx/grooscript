package org.grooscript.java;

import org.grooscript.GrooScript;
import org.grooscript.test.JavascriptEngine;
import org.grooscript.test.JsTestResult;
import org.grooscript.test.NodeJs;
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: jorgefrancoleza
 * Date: 10/06/14
 */
public class TestTypes {

    @Test
    public void testNumbers() throws Exception {

        //First validate java code
        new Numbers().validate();

        //Convert and validate javascript converted code
        evaluateInJavascript("Numbers");
    }

    @Test
    public void testMaps() throws Exception {
        new Maps().validate();
        evaluateInJavascript("Maps");
    }

    final static Charset ENCODING = StandardCharsets.UTF_8;

    private void evaluateInJavascript(String nameClass) throws IOException {
        String result = GrooScript.convert(readFile(nameClass));
        result += "\r\n" + nameClass + "().validate();";
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
            result = result + line + "\r\n";
        }
        return result;
    }
}

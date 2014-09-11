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

/**
 * User: jorgefrancoleza
 * Date: 11/09/14
 */
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
            assertEquals(lines.get(0), "function Numbers() {");
        } catch (Exception e) {
            fail("Don't have to fail convert java file " + source);
        } finally {
            Files.delete(path);
        }
    }
}

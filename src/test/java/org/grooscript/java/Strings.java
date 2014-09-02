package org.grooscript.java;

/**
 * User: jorgefrancoleza
 * Date: 10/06/14
 */
public class Strings {

    public void validate() throws Exception {
        String hello = "hello";
        String bye = new String("bye");

        assert hello.substring(1, 2).equals("e");
        assert (hello + bye).equals("hellobye");
    }
}

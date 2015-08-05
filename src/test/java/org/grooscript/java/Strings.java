package org.grooscript.java;

/**
 * User: jorgefrancoleza
 * Date: 10/06/14
 */
public class Strings {

    public boolean validate() throws Exception {
        String hello = "hello";
        String bye = new String("bye");

        return hello.substring(1, 2).equals("e") && (hello + bye).equals("hellobye");
    }
}

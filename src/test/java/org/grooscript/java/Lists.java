package org.grooscript.java;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jorgefrancoleza
 * Date: 01/09/14
 */
public class Lists {
    public void validate() throws Exception {
        List list = new ArrayList();

        list.add("Hello");
        list.clear();
        assert list.size() == 0;
        assert list.isEmpty();

        list.add("Bye");
        assert list.contains("Bye");
        assert 0 == list.indexOf("Bye");

        list.add(1, "Groovy");
        assert 1 == list.lastIndexOf("Groovy");
    }
}

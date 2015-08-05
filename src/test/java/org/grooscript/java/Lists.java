package org.grooscript.java;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jorgefrancoleza
 * Date: 01/09/14
 */
public class Lists {
    public boolean validate() throws Exception {
        List list = new ArrayList();

        list.add("Hello");
        list.clear();
        boolean partial = (list.size() == 0 && list.isEmpty());

        list.add("Bye");
        partial = partial && list.contains("Bye");
        partial = partial && 0 == list.indexOf("Bye");

        list.add(1, "Groovy");
        assert 1 == list.lastIndexOf("Groovy");

        return partial && 1 == list.lastIndexOf("Groovy");
    }
}

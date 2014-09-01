package org.grooscript.java;

import java.util.HashSet;
import java.util.Set;

/**
 * User: jorgefrancoleza
 * Date: 27/08/14
 */
public class Sets {
    public void validate() throws Exception {
        Set<String> set = new HashSet<String>();
        set.clear();

        set.add("Hello");
        set.add("Hello");

        assert set.contains("Hello") == true;
        assert set.size() == 1;

        for (String elem : set) {
            assert elem == "Hello";
        }

        set.add("Bye");
        Set<String> otherSet = new HashSet<String>();
        otherSet.add("Bye");

        assert set.containsAll(otherSet);
        assert set.remove("Hello") == true;
    }
}

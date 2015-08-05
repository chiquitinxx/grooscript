package org.grooscript.java;

import java.util.HashSet;
import java.util.Set;

/**
 * User: jorgefrancoleza
 * Date: 27/08/14
 */
public class Sets {
    public boolean validate() throws Exception {
        Set<String> set = new HashSet<String>();
        set.clear();

        set.add("Hello");
        set.add("Hello");

        boolean partial = (set.size() == 1);

        for (String elem : set) {
            partial = partial && elem.equals("Hello");
        }

        set.add("Bye");
        Set<String> otherSet = new HashSet<String>();
        otherSet.add("Bye");

        return set.contains("Hello") == true && partial && set.containsAll(otherSet) && set.remove("Hello") == true;
    }
}

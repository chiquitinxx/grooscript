package org.grooscript.java;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jorgefrancoleza
 * Date: 19/08/14
 */
public class Maps {
    public boolean validate() throws Exception {
        Map map = new HashMap();
        map.clear();

        map.put("key1", "element 1");
        map.put("key2", "element 2");
        map.put("key3", "element 3");

        String elem1 = (String) map.get("key1");

        Map<String, String> stringMap = new HashMap<String, String>();
        for(Object key : map.keySet()) {
            Object value = map.get(key);
            stringMap.put((String)key, (String)value);
        }

        return map.size() == 3 && elem1.equals("element 1") && stringMap.size() == 3;
    }
}

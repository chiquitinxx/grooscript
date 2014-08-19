package org.grooscript.java;

import java.util.HashMap;
import java.util.Map;

/**
 * User: jorgefrancoleza
 * Date: 19/08/14
 */
public class Maps {
    public void validate() throws Exception {
        Map map = new HashMap();

        map.put("key1", "element 1");
        map.put("key2", "element 2");
        map.put("key3", "element 3");

        assert map.size() == 3;

        String elem1 = (String) map.get("key1");
        assert elem1 == "element 1";

        Map<String, String> stringMap = new HashMap<String, String>();
        for(Object key : map.keySet()) {
            Object value = map.get(key);
            stringMap.put((String)key, (String)value);
        }

        assert stringMap.size() == 3;
    }
}

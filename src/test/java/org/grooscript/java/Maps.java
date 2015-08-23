/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.java;

import java.util.HashMap;
import java.util.Map;

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

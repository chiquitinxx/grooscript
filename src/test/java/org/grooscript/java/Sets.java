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

import java.util.HashSet;
import java.util.Set;

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

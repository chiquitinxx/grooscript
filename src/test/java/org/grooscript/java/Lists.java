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

import java.util.ArrayList;
import java.util.List;

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

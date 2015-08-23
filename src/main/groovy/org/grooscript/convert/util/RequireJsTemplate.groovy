/*
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
package org.grooscript.convert.util

import groovy.transform.Canonical

@Canonical
class RequireJsTemplate {

    String destinationFile
    String requireFolder
    List<RequireJsDependency> dependencies
    String jsCode
    List<String> classes
}

@Canonical
class RequireJsDependency {
    String path
    String name
}

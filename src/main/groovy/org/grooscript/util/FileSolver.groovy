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
package org.grooscript.util

import java.util.regex.Matcher

import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.GROOVY_EXTENSION

class FileSolver {

    public static final DEFAULT_PATH = '.'

    boolean exists(String pathFile) {
        def file = new File(pathFile)
        file && file.exists() && file.file
    }

    String readFile(String pathFile) {
        exists(pathFile) ? new File(pathFile).text : null
    }

    String canonicalPath(String pathFile) {
        new File(pathFile).canonicalPath
    }

    String filePathFromClassName(String className) {
        className.replaceAll("\\.", Matcher.quoteReplacement(SEP))
    }

    String filePathFromClassName(String className, String classPath) {
        def begin = classPath ? classPath + SEP : ''
        begin + filePathFromClassName(className) + GROOVY_EXTENSION
    }

    void saveFile(String filePath, String content) {
        File file = new File(filePath)
        file.getParentFile()?.mkdirs()
        file.text = content
    }

    boolean isFolder(String pathFolder) {
        File file = new File(pathFolder)
        file && file.exists() && file.directory
    }


    String classPathFolder(classpath) {
        if (!classpath) {
            return DEFAULT_PATH
        } else {
            return firstFolderFrom(classpath)
        }
    }

    private String firstFolderFrom(classPath) {
        if (classPath instanceof String && isFolder(classPath)) {
            return classPath
        }
        if (classPath instanceof List) {
            return classPath.find { isFolder(it) } ?: DEFAULT_PATH
        }
        DEFAULT_PATH
    }
}

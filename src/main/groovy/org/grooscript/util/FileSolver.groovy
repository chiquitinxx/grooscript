package org.grooscript.util

import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.GROOVY_EXTENSION
/**
 * Created by jorgefrancoleza on 10/3/15.
 */
class FileSolver {
    String readFile(String pathFile) {
        new File(pathFile).text
    }

    String filePathFromClassName(String className, String classPath) {
        def begin = classPath ? classPath + SEP : ''
        begin + className.replaceAll(/\./, SEP) + GROOVY_EXTENSION
    }
}

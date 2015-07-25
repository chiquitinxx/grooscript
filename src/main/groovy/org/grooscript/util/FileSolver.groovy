package org.grooscript.util

import java.util.regex.Matcher

import static org.grooscript.util.Util.SEP
import static org.grooscript.util.Util.GROOVY_EXTENSION
/**
 * Created by jorgefrancoleza on 10/3/15.
 */
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

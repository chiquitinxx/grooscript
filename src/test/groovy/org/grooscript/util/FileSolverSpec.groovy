package org.grooscript.util

import spock.lang.Specification

import static org.grooscript.util.Util.SEP
/**
 * Created by jorgefrancoleza on 10/3/15.
 */
class FileSolverSpec extends Specification {

    void 'file exists'() {
        expect:
        fileSolver.exists(FILE_PATH)
    }

    void 'file path from class name'() {
        expect:
        fileSolver.filePathFromClassName('Name', null) == 'Name.groovy'
        fileSolver.filePathFromClassName('Name', '') == 'Name.groovy'
        fileSolver.filePathFromClassName('Name', '/gol') == "/gol${SEP}Name.groovy"
        fileSolver.filePathFromClassName('org.Name', '') == "org${SEP}Name.groovy"
        fileSolver.filePathFromClassName('org.Name', "src${SEP}main${SEP}groovy") ==
                "src${SEP}main${SEP}groovy${SEP}org${SEP}Name.groovy"
    }

    void 'read file content'() {
        expect:
        new File(FILE_PATH).text == fileSolver.readFile(FILE_PATH)
    }

    void 'save a file'() {
        given:
        def fileName = 'CASUAL'
        def content = 'content file'

        when:
        fileSolver.saveFile(fileName, content)

        then:
        new File(fileName).text == content

        cleanup:
        new File(fileName).delete()
    }

    void 'test is folder'() {
        expect:
        fileSolver.isFolder('.')
    }

    private static final FILE_PATH = 'LICENSE.txt'
    private FileSolver fileSolver = new FileSolver()
}

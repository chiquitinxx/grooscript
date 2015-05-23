package org.grooscript.convert.util

import org.grooscript.util.FileSolver

import static org.grooscript.util.Util.LINE_SEPARATOR
import static org.grooscript.util.Util.SEP

/**
 * Created by jorgefrancoleza on 12/4/15.
 */
class RequireJsFileGenerator {

    static final DEFINE_MODULE = 'define(function () {' + LINE_SEPARATOR
    static final END_MODULE = '});'
    FileSolver fileSolver

    void generate(RequireJsTemplate requireTemplate) {
        fileSolver.saveFile(
                destinationFilePath(requireTemplate.requireFolder, requireTemplate.destinationFile),
                template(requireTemplate)
        )
    }

    private String destinationFilePath(String destFolder, String destFile) {
        if (!destFolder || destFolder == '.') {
            destFile
        } else {
            def file = new File(destFolder + SEP + destFile)
            file.path
        }
    }

    private String template(RequireJsTemplate requireTemplate) {
        (requireTemplate.dependencies ? moduleWithDependencies(requireTemplate.dependencies) : DEFINE_MODULE) +
                requireTemplate.jsCode + LINE_SEPARATOR + returnClasses(requireTemplate.classes) + END_MODULE
    }

    private String moduleWithDependencies(List<RequireJsDependency> dependencies) {
        def paths = dependencies.collect { "'" + it.path + "'" }
        def moduleNames = dependencies.collect { it.name }
        'define([' + paths.join(',')+'], function ('+moduleNames.join(',')+') {' + LINE_SEPARATOR
    }

    private String returnClasses(List<String> classes) {
        if (!classes) {
            ''
        } else {
            if (classes.size() == 1) {
                "  return ${classes[0]};${LINE_SEPARATOR}"
            } else {
                def mapClasses = classes.collect { "${it}:${it}" }.join(',')
                "  return {${mapClasses}};${LINE_SEPARATOR}"
            }
        }
    }
}

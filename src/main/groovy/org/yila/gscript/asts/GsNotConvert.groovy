package org.yila.gscript.asts

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import java.lang.annotation.ElementType
import org.codehaus.groovy.transform.GroovyASTTransformationClass

/**
 * JFL 10/11/12
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD,ElementType.TYPE])
//@GroovyASTTransformationClass(["Nativing"])
@GroovyASTTransformationClass(['org.yila.gscript.asts.NotConvert'])
public @interface GsNotConvert {
}

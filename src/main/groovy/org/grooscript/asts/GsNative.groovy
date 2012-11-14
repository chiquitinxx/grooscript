package org.grooscript.asts

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * JFL 10/11/12
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
//@GroovyASTTransformationClass(["Nativing"])
@GroovyASTTransformationClass(['org.grooscript.asts.Native'])
public @interface GsNative {
}

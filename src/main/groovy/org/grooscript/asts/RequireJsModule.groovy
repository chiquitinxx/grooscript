package org.grooscript.asts

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * User: jorgefrancoleza
 * Date: 23/05/15
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.FIELD])
@GroovyASTTransformationClass(['org.grooscript.asts.RequireJsModuleImpl'])
public @interface RequireJsModule {
    String path ()
}
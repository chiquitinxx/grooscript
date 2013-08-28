package org.grooscript.asts

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * User: jorgefrancoleza
 * Date: 30/03/13
 */
@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(['org.grooscript.asts.PhantomJsTestImpl'])
public @interface PhantomJsTest {
    String url ()
    String capture () default ''
}
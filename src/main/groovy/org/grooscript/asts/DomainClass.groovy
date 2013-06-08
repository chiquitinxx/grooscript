package org.grooscript.asts

import org.codehaus.groovy.transform.GroovyASTTransformationClass

/**
 * User: jorgefrancoleza
 * Date: 28/01/13
 */
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(['org.grooscript.asts.DomainClassImpl'])
public @interface DomainClass {
}
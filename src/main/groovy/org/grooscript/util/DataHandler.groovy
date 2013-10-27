package org.grooscript.util

/**
 * User: jorgefrancoleza
 * Date: 26/08/13
 */
interface DataHandler {
    long getDomainItem(String className, id)
    long list(String className, params)
    long insert(String className, item)
    long update(String className, item)
    long delete(String className, item)
}

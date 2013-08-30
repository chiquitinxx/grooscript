package org.grooscript.util

/**
 * User: jorgefrancoleza
 * Date: 26/08/13
 */
interface DataHandler {
    long insert(String className, item)
    long update(String className, item)
    long delete(String className, item)
}

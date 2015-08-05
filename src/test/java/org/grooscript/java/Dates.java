package org.grooscript.java;

import java.util.Date;

/**
 * User: jorgefrancoleza
 * Date: 14/09/14
 */
public class Dates {

    public boolean validate() throws Exception {
        Date initial = new Date();

        initial.setTime(0);
        return new Date().after(initial) && initial.before(new Date());
    }
}

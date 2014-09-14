package org.grooscript.java;

import java.util.Date;

/**
 * User: jorgefrancoleza
 * Date: 14/09/14
 */
public class Dates {

    public void validate() throws Exception {
        Date initial = new Date();

        initial.setTime(0);
        assert new Date().after(initial);
        assert initial.before(new Date());
    }
}

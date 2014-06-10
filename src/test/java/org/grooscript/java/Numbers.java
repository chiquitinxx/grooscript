package org.grooscript.java;

import java.math.BigDecimal;

/**
 * User: jorgefrancoleza
 * Date: 10/06/14
 */
public class Numbers {

    int intNumber;
    double doubleNumber;
    BigDecimal bigDecimalNumber;

    public Numbers() {
        intNumber = 5;
        doubleNumber = 12.34;
        bigDecimalNumber = new BigDecimal("10");
    }

    public boolean validate() throws Exception {
        if (intNumber != 5 || doubleNumber != 12.34 || bigDecimalNumber.intValue() != 10) {
            return false;
        } else {
            return true;
        }
    }
}

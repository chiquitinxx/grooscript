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

    public void validate() throws Exception {
        assert intNumber == 5;
        assert doubleNumber == 12.34;
        assert bigDecimalNumber.intValue() == 10;
    }
}

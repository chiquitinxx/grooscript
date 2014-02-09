package classes

/**
 * User: jorgefrancoleza
 * Date: 09/02/14
 */

String[] gr8 = new String[2]
gr8.putAt(0, 'Groovy')
gr8.putAt(1, 'Grails')

assert gr8 == ['Groovy', 'Grails']

byte[] anArrayOfBytes = new byte[1]
anArrayOfBytes[0] = 100.byteValue()
assert anArrayOfBytes == [100]
short[] anArrayOfShorts = new short[1]
anArrayOfShorts[0] = 90.shortValue()
assert anArrayOfShorts == [90]
long[] anArrayOfLongs = new long[1]
anArrayOfLongs[0] = 80.longValue()
assert anArrayOfLongs == [80]
float[] anArrayOfFloats = new float[1]
anArrayOfFloats[0] = 70.floatValue()
assert anArrayOfFloats == [70]
double[] anArrayOfDoubles = new double[1]
anArrayOfDoubles[0] = 60.doubleValue()
assert anArrayOfDoubles == [60]
boolean[] anArrayOfBooleans = new boolean[1]
anArrayOfBooleans[0] = false
assert anArrayOfBooleans == [false]
char[] anArrayOfChars = new char[1]
anArrayOfChars[0] = 'a'
assert anArrayOfChars == ['a']



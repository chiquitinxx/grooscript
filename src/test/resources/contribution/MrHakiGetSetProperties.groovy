package contribution

/**
 * JFL 22/12/12
 */

// Simple Java Bean.
public class Simple {
    private String text;

    public Simple() {
        super();
    }

    public String getMessage() {
        return "GET " + text;
    }

    public void setMessage(final String text) {
        this.text = "SET " + text
    }
}

def s1 = new Simple()
s1.setMessage('Old style')
assert 'GET SET Old style' == s1.getMessage()
s1.setMessage 'A bit more Groovy'  // No parenthesis.
assert 'GET SET A bit more Groovy' == s1.getMessage()

def s2 = new Simple(message: 'Groovy constructor')  // Named parameter in constructor.
assert 'GET SET Groovy constructor' == s2.getMessage()

def s3 = new Simple()
s3.message = 'Groovy style'  // = assigment for property.
assert 'GET SET Groovy style' == s3.message  // get value with . notation.
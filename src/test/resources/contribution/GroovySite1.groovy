package contribution

/**
 * Created by jorge on 23/7/15.
 */
class POGO {

    String property

    void setProperty(String name, Object value) {
        this.@"$name" = 'overriden'
    }
}

def pogo = new POGO()
pogo.property = 'a'

assert pogo.property == 'overriden'
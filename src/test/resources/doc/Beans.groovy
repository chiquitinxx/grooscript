package doc

/**
 * Created by jorgefrancoleza on 8/1/15.
 */
// tag::mark[]
class MyBean {
    def a
    def b

    def getA() { 5 }

    def getC() { 6 }
}

def myBean = new MyBean(a: 3, b: 4)
assert myBean.a == 5
assert myBean.@a == 3
assert myBean.b == 4
assert myBean.c == 6
// end::mark[]
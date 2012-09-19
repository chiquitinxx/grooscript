package contribution

def m() {
    def i=0
    return {++i}
}
def cl = m()
def a = cl()
def b = cl()
assert a<b

package contribution

/**
 * Created by jorgefrancoleza on 19/9/15.
 */

def a = 0

def cl1 = { a ++ }
def cl2 = { a += 2 }

def list = [cl1, cl2]

for (Runnable f: list)
    f.run()

assert a == 3

def b = 0

for (Closure cl: list) {
    b++
}

assert b == 2

def c = 0

if (b) c = 1 else c = 2

assert c == 1
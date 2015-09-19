package contribution

/**
 * Created by jorgefrancoleza on 19/9/15.
 */

String[] names = ["webgl", "experimental-webgl", "webkit-3d", "moz-webgl"]
def result = null;
for (String name : names) {
    break;
    result = '1'
}

assert result == null

for (aName in names) {
    result = aName
    break;
    result = null;
}

assert result == names[0]

def val = 0
for (int i = 0; i < names.size(); i++) {
    val++
    break;
}
assert val == 1
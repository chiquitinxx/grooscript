package traits

/**
 * Created by jorge on 17/04/14.
 */

trait Counter {
    private int count = 0
    int count() { count += 1; count }
}
class NewFoo implements Counter {}
def f = new NewFoo()
assert f.count() == 1
assert f.count() == 2

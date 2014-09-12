package classes

/**
 * Created by jorge on 12/09/14.
 */
class WithThis {
    private String info = 'Data'

    String getInfo() {
        return this.info
    }
}

assert new WithThis().getInfo() == 'Data'
assert new WithThis().info == 'Data'

package classes

/**
 * Created by jorge on 12/09/14.
 */
class WithThis {
    private String info = 'Data'

    String getInfo() {
        return this.info
    }

    void setInfo(info) {
        this.info = info
    }
}

assert new WithThis().getInfo() == 'Data'
assert new WithThis().info == 'Data'
def info = new WithThis(info: 'Info')
assert info.info == 'Info'
info.setInfo('NewInfo')
assert info.info == 'NewInfo'
assert info.getInfo() == 'NewInfo'
info.info = 'Back'
assert info.info == 'Back'
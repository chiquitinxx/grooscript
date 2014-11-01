package contribution

/**
 * Created by jorge on 1/11/14.
 */
class ExecCall {

    CallItem callItem

    ExecCall(callItemT) {
        this.callItem = callItemT
        assert callItemT(3).number == 3
        assert callItemT(3).one == 1
        tryHere()
        assert callItem(3).number == 3
        assert callItem(3).one == 1
    }

    def tryHere() {
        assert callItem(3).number == 3
        assert callItem(3).one == 1
    }
}

class CallItem {
    def call(number) {
        [one: 1, two: 2, number: number]
    }
}

new ExecCall(new CallItem())
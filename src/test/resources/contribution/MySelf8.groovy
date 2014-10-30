package contribution

/**
 * Created by jorge on 30/10/14.
 */
class ExecutingClosures {
    static executeWithData(data, closure) {
        closure.delegate = data
        closure()
    }
}

ExecutingClosures.executeWithData(me: 'jorge') {
    def a = 0
    3.times {
        a++
    }
    assert a == 3
    assert me == 'jorge'
}
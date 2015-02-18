package contribution

/**
 * Created by jorge on 17/2/15.
 */

class MyTask {

    def data = 'data'
    def other = 'other'

    static doIt(Closure dsl) {
        def myTask = new MyTask()
        myTask.with {
            data = newData
        }
        dsl.delegate = myTask
        dsl()
        myTask
    }

    static getNewData() {
        'newData'
    }
}

def task = MyTask.doIt {
    other = 'newOther'
}

assert task.data == 'newData', "->${task.data}"
assert task.other == 'other' //!?!?
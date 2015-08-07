package org.grooscript.rx

/**
 * Created by jorgefrancoleza on 14/11/14.
 */
class Observable {

    List subscribers = []
    List sourceList = null
    List chain = []

    static Observable listen() {
        new Observable()
    }

    static Observable from(List list) {
        new Observable(sourceList: list)
    }

    void produce(event) {
        subscribers.each {
            processFunction(event, it)
        }
    }

    Observable map(Closure cl) {
        chain << cl
        this
    }

    Observable filter(Closure cl) {
        chain << { it ->
            if (cl(it)) {
                it
            } else {
                throw new Exception()
            }
        }
        this
    }

    void subscribe(Closure cl) {
        while (chain) {
            cl = cl << chain.pop()
        }
        subscribers << cl
        if (sourceList) {
            sourceList.each {
                processFunction(it, cl)
            }
        }
    }

    void removeSubscribers() {
        subscribers = []
    }

    private processFunction(data, cl) {
        try {
            cl(data)
        } catch (e) {
            //println "Exception: ${e.message}"
        }
    }
}


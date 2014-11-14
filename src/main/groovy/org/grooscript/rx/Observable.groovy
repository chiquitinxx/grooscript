package org.grooscript.rx

/**
 * Created by jorgefrancoleza on 14/11/14.
 */
class Observable {

    def subscribers = []
    def sourceList = null

    static listen() {
        new Observable()
    }

    static from(list) {
        new Observable(sourceList: list)
    }

    void produce(event) {
        subscribers.each {
            it(event)
        }
    }

    void subscribe(Closure cl) {
        subscribers << cl
        if (sourceList) {
            sourceList.each {
                cl(it)
            }
        }
    }
}


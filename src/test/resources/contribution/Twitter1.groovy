package contribution

/**
 * @author Jorge Franco
 */

def getCallBacks(){
//def getCallBacks = {
    def callBackArray = []
    def i = 0
    for(;i<3; i++){
        callBackArray[i] = { i }
    }
    return callBackArray
}

def callBackArray = getCallBacks()
assert callBackArray[0]() == 3


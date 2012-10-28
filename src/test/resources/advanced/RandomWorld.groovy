package advanced

/**
 * JFL 28/10/12
 */
final MAX = 5

def randomNumber
def surprise = false
def values = [:]

500.times {
    randomNumber = new Random().nextInt(MAX)
    if (randomNumber>=MAX) {
        surprise = true
    }
    values."$randomNumber" = (values."$randomNumber"?values."$randomNumber"+1:1)
}

values.each { key, value ->
    println "$key repeated $value times"
}

assert !surprise

result = [heads:0, tails:0]
500.times {
    if (new Random().nextBoolean()) {
        result.heads++
    } else {
        result.tails++
    }
}

println (result.heads>result.tails?'Next bid on TAILS':(result.heads==result.tails?'WHO KNOWS!':'Next bid on HEADS'))
println " h:${result.heads} t:${result.tails}"
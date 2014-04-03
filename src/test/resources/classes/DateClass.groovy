package classes

/**
 * User: jorgefrancoleza
 * Date: 03/04/14
 */
def today = new Date().clearTime()
def tomorrow = today + 1
def yesterday = today - 1

assert today + 1 == tomorrow
assert yesterday + 1 == today

assert today.before(tomorrow)
assert today.after(yesterday)

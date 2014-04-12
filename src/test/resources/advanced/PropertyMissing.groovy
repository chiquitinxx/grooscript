package advanced

/**
 * Created by jorge on 12/04/14.
 */

class Bank {
    def money = 20
    def moneys = []

    def propertyMissing(String name) {
        if (name.startsWith('money')) {
            return name.substring(5)
        }
    }

    def propertyMissing(String name, value) {
        if (name.startsWith('money')) {
            moneys << name.substring(5)
        }
    }
}

def bank = new Bank()
assert bank.money == 20
bank.money = 10
assert bank.money == 10
assert bank.moneyYeah == 'Yeah'

bank.moneySave = 10
assert bank.moneys == ['Save']
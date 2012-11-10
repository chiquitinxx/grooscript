package asts

/**
 * JFL 06/11/12
 */


class Data {

    def sayTrue() { /*{
        return true;
    }*/}
}

data = new Data()
//This fail in groovy, but have to work in javascript
assert data.sayTrue()
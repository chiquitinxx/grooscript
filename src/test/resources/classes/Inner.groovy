package classes

/**
 * User: jorgefrancoleza
 * Date: 16/06/14
 */

class Almacen {

    def listaObjetos = []

    Iterador getIterador() {
        new Iterador()
    }

    class Iterador {
        int indice = 0
        Object siguiente() {
            if (indice < listaObjetos.size()) return listaObjetos[indice++]
            else return null
        }
    }
}

Almacen alm = new Almacen()
alm.listaObjetos = [3, 5, 1, 8]

Almacen.Iterador i = alm.getIterador()
assert i.class.name == 'classes.Almacen$Iterador'
assert i.siguiente() == 3
assert i.siguiente() == 5
assert i.siguiente() == 1
assert i.siguiente() == 8
assert i.siguiente() == null
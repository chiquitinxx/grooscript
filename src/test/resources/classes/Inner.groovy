package classes

/**
 * User: jorgefrancoleza
 * Date: 16/06/14
 */

class Almacen {
    private listaObjetos = []
    private numElementos = 0
    Almacen (int maxElementos) {
        listaObjetos = new Object[maxElementos]
    }
    public Object get(int i) {
        return listaObject[i]
    }
    public void add(Object obj) {
        listaObjetos[numElementos++] = obj
    }
    public Iterador getIterador() {
        new Iterador()
    }

    class Iterador {
        int indice = 0
        Object siguiente() {
            if (indice < numElementos) return listaObjetos[indice++]
            else return null
        }
    }
}

Almacen alm = new Almacen(10)
alm.add("one")

Almacen.Iterador i = alm.getIterador()
assert i.class.name == 'classes.Almacen$Iterador'
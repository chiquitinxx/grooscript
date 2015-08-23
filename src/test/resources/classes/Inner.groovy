/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package classes

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
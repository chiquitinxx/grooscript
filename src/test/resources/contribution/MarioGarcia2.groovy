package contribution

/**
 * Description
 *
 * @author Jorge Franco
 */
class Model {
    def name
    def age
}

class Application extends App {

    /**
     * Bootstraping the application
     **/
    def init(){
        println "Bootstraping the application"
        def model = new Model(name:"John",age:21)
        //def controller = new Controller()
        //controller.initFormWith(model)
        println "Application started successfully"
    }
}

class App {
    String name
}
/*

class Controller {
    def name
}*/

new Application().init()
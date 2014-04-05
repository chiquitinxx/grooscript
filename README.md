grooscript 0.5-SNAPSHOT
=======================

Converts your Groovy code to JavaScript(Groovy 2.x to Javascript ECMAScript 5).

Not a full groovy to javascript conversion. Some groovy and java features not supported, check [website](http://grooscript.org) for more info. Converted code, needs [grooscript.js](https://github.com/chiquitinxx/grooscript/blob/master/src/main/resources/META-INF/resources/grooscript.js) to run. grooscript.js inside the jar in META-INF/resources for servlet 3.0 support. Also a Node.js [npm](http://www.npmjs.org/package/grooscript) module imports it.

Can convert .groovy files or a text fragment as:

    @Grab('org.grooscript:grooscript:0.4.5')

    import org.grooscript.GrooScript

    def result = GrooScript.convert '''
        def sayHello = { println "Hello ${it}!" }
        ['Groovy','JavaScript','GrooScript'].each sayHello

        assert [1,2,3].size() == 3
        class Amazing {}

        amazing = new Amazing()
        amazing.metaClass.who = []
        amazing.metaClass.add = { who << it}

        assert amazing.who.size()==0
        amazing.add 'Rafa Nadal\'
        assert amazing.who.size()==1'''

    println result

Build
-----
Only GPars 1.0 and Groovy dependencies in the project, Gradle as build system. Need JDK 1.7 tu run tests. Using gradle wrapper, version 1.11.

Create idea project:

    ./gradlew idea

Build project:

    ./gradlew build

Contact
-------

Twitter: [@grooscript](http://twitter.com/grooscript) [@jfrancoleza](http://twitter.com/jfrancoleza)

Email: <grooscript@gmail.com>

Please all feedback welcome, thanks!

grooscript 0.6.2
================

Converts your Groovy or Java code to JavaScript(Groovy 2.x to Javascript ECMAScript 5). You need groovy jar greater than 2.0 to make conversions.

Also support Java to javascript conversions, but with a lot of java types not allowed. Have to use groovy types, as ArrayList for lists or BigDecimal for numbers. *Supporting java is not the target of this library, groovy is :)*

Not a full groovy to javascript conversion. Some groovy and java features not supported, check [website](http://grooscript.org) for more info. Converted code, needs [grooscript.js](https://github.com/chiquitinxx/grooscript/blob/master/src/main/resources/META-INF/resources/grooscript.js) to run. grooscript.js inside the jar in META-INF/resources for servlet 3.0 support. Also a Node.js [npm](http://www.npmjs.org/package/grooscript) module imports it.

Can convert .java and .groovy files or a text fragment as:

    @Grab('org.grooscript:grooscript:0.6.2')

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
        amazing.add 'Rafa Nadal'
        assert amazing.who.size()==1'''

    println result

Tools
-----

Grails [plugin](http://grails.org/plugin/grooscript)

Gradle [plugin](http://plugins.gradle.org/plugin/org.grooscript.conversion)

Npmjs [package](https://www.npmjs.org/package/grooscript)

Build
-----
Previous requirements: JDK 1.7+
Using gradle wrapper, version 2.1. Only GPars 1.2 and Groovy dependencies in the project, Gradle as build system.

First time, you have to install Node.js stuff, maybe you have to run as administrator, do it with:

    ./gradlew npmInstall

Create IntelliJ IDEA project:

    ./gradlew idea

Build project:

    ./gradlew build

Contact
-------

Twitter: [@grooscript](http://twitter.com/grooscript)

Email: <grooscript@gmail.com>

Please all feedback welcome, thanks!

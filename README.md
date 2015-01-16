[![Build Status](https://snap-ci.com/chiquitinxx/grooscript/branch/master/build_image)](https://snap-ci.com/chiquitinxx/grooscript/branch/master)
grooscript
===

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/chiquitinxx/grooscript?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Converts your Groovy or Java code to JavaScript(Groovy 2.x to Javascript ECMAScript 5). You need groovy jar greater
than 2.0 to make conversions. For more information about the project, see [grooscript.org](http://grooscript.org)

Not a full groovy to javascript conversion. Some groovy and java features not supported, check
[documentation](http://grooscript.org/doc.html) for more info. Converted code, needs
[grooscript.js](https://github.com/chiquitinxx/grooscript/blob/master/src/main/resources/META-INF/resources/grooscript.js)
to run. grooscript.js inside the jar in META-INF/resources for servlet 3.0 support. Also a Node.js
[npm](http://www.npmjs.org/package/grooscript) module imports it.

Try online conversions [here](http://grooscript.org/conversions.html). Can convert .java and .groovy files or a text fragment as:

```groovy
@Grab('org.grooscript:grooscript:1.0.0-rc-1')

import org.grooscript.GrooScript

def result = GrooScript.convert '''
    def sayHello = { println "Hello ${it}!" }
    ['Groovy','JavaScript','GrooScript'].each sayHello'''

println result
```

Latest Versions
---
The latest release version is **1.0.0-rc-1**, released on 2015-01-05. The current development
version is **1.0.0-rc-2**.

Releases are available from [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22grooscript%22)
and [Bintray](https://bintray.com/chiquitinxx/grooscript/org.grooscript%3Agrooscript/view).

Build
---
Using gradle wrapper, version 2.2.1. Only GPars 1.2 and Groovy dependencies in the project, Gradle as build system. You need JDK 1.7 to build and test the project. In JDK 8, nashorn engine fails evaluating some tests converted code.

First time, you have to install Node.js stuff, maybe you have to run as administrator, do it with:

    ./gradlew npmInstall

Create IntelliJ IDEA project:

    ./gradlew idea

Test project:

    ./gradlew test

Build project:

    ./gradlew build

Tools
---

Grails [plugin](http://grails.org/plugin/grooscript)

Gradle [plugin](http://plugins.gradle.org/plugin/org.grooscript.conversion)

Npmjs [package](https://www.npmjs.org/package/grooscript)

License
---

Grooscript licensed under the terms of the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Contact
---

Twitter: [@grooscript](http://twitter.com/grooscript)

Email: <grooscript@gmail.com>

Please all feedback welcome, thanks!

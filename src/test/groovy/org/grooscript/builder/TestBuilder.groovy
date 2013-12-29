package org.grooscript.builder

import org.grooscript.asts.DomainClass
import org.grooscript.test.ConversionMixin
import spock.lang.Specification

/**
 * User: jorgefrancoleza
 * Date: 08/06/13
 */
@Mixin([ConversionMixin])
class TestBuilder extends Specification {

    static final TEXT = 'text'

    void 'process with the builder'() {
        given:
        def result = Builder.process {
            body {
                p TEXT
            }
        }

        expect:
        result.html == "<body><p>${TEXT}</p></body>"

        and: 'works in javascript'
        !checkBuilderCodeAssertsFails('''
            def result = Builder.process {
                body {
                    p 'hola'
                }
            }

            assert result.html == "<body><p>hola</p></body>"
        ''',false)
    }

    void 'works with tag options and t function'() {
        given:
        def result = Builder.process {
            body {
                p(class:'salute') {
                    t 'hello'
                }
            }
        }

        expect:
        result.html == "<body><p class='salute'>hello</p></body>"
    }

    class MyDomainClass {
        static list() {
            [[name:'myDomainClass']]
        }
    }

    void 'works with a model class'() {
        given:
        def result = Builder.process {
            body {
                ul {
                    MyDomainClass.list().each {
                        li 'item: '+ it.name
                    }
                }
            }
        }

        expect:
        result.html == "<body><ul><li>item: myDomainClass</li></ul></body>"
    }

    @DomainClass
    class MyClass {
        String name
    }

    void 'works with a domain class'() {
        given:

        def result = Builder.process {

            def NAME = 'George'
            def myClass = new MyClass(name: NAME)
            myClass.save()

            body {
                ul {
                    MyClass.list().each { myItem ->
                        li(class: 'myClass') {
                            t 'item: '+ myItem.name
                        }
                    }
                }
            }
        }

        expect:
        result.html == "<body><ul><li class='myClass'>item: George</li></ul></body>"

        and: 'works in javascript'
        !checkBuilderCodeAssertsFails('''
            import org.grooscript.asts.DomainClass

            @DomainClass
            class MyClass {
                String name
            }

            def NAME = 'George'
            def myClass = new MyClass(name: NAME)
            myClass.save()

            def result = Builder.process {
                body {
                    ul {
                        MyClass.list().each { myItem ->
                            li(class: 'myClass') {
                                t 'item: '+ myItem.name
                            }
                        }
                    }
                }
            }
            assert result.html == "<body><ul><li class='myClass'>item: George</li></ul></body>"
        ''')
    }
}

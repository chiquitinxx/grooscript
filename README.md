GrooScript
==========

Converts your Groovy code to JavaScript(Groovy 2.0 to Javascript ECMAScript 5).

For run JavaScript need grooscript.js

**Version 0.1 is out!**

In sonar maven repository:

https://oss.sonatype.org/content/groups/public/
org.grooscript:grooscript:0.1

Please all feedback welcome, thanks!

Missing some Groovy stuff:
--------------------------

Basic inheritance, not super except in constructors.

Not class, instanceof, super,... Types not supported.

Not allowed same number of parameters in methods / constructors.

Can add methods and properties with metaclass, but some problems with primitive types as String and Number.

Losing pretty print, had to function == (equals), and I suppose same for other operators.

Javascript 'split' not the same, maybe will translate to tokenize.

String.length() not work, cause length is property in Javascript.

---

Twitter: @jfrancoleza

Website URL (can try conversions there): http://www.grooscript.org
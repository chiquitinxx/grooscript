GrooScript
==========

Converts your Groovy code to JavaScript(Groovy 2.0 to Javascript ECMAScript 5).

Get AST -> To JavaScript, some common functions in other js files.


Missing some Groovy stuff:
--------------------------

Basic inheritance, not super except in constructors.

Not class, instanceof, super,... Types not supported.

Not allowed same number of parameters in methods / constructors.

Can add methods and properties with metaclass, but some problems with primitive types as String and Number.

Losing pretty print, had to function == (equals), and I suppose same for other operators.

Javascript 'split' not the same, maybe will translate to tokenize.

---

Twitter: @jfrancoleza

Website URL (can try conversions there): http://www.grooscript.org
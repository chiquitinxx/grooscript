GrooScript
==========

Converts your Groovy code to JavaScript(Groovy 2.0 to Javascript ECMAScript 5).

Not a full groovy to javascript conversion. Better groovy support means lost pretty print in javascript conversion.

Converted code, needs grooscript.js to run. grooscript.js inside the jar in META-INF/resources for servlet 3.0 support.

Working version 0.2

In sonar maven repository:

https://oss.sonatype.org/content/groups/public/

org.grooscript:grooscript:0.1

Please all feedback welcome, thanks!

Missing some Groovy / Java stuff:
---------------------------------

Basic inheritance, can do super() only in constructors.

Types not supported, can't casting, missing lot of java types.

Only can access class names and instanceof of created classes with a conversion option.

Not allowed same number of parameters in methods / constructors.

Can add methods and properties with metaclass, but some problems with primitive types as String and Number.

No delegate. No ExpandoMetaClass. No libraries out of groovy-core.

Groovy ast transformations as @Cannonical, @ToString, ... not supported.

Variable assignment not allowed inside boolean expressions as ternary (?:).

---

Twitter: @jfrancoleza

Email: grooscript@gmail.com

Website URL (can try conversions there): http://www.grooscript.org
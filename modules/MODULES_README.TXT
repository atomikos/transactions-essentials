MODULE GUIDELINES: WHAT IS A MODULE?

At the level of SOURCE code, a modules is a set of related classes with their 
SEPARATE build step. This separation avoids circular dependencies between
different modules. Module dependencies are captured in the product-specific
build files. This makes sense because products are supposed to be aware
of what modules they need and how to combine them.
Modules are also intended to depend on the SAME SET OF THIRD-PARTY libs.
This is because different version of third-party libs may conflict and
cause problems in the source compilation. Therefore, integration code
with third-party products should go into one module for each (major) version
of that third-party library.

Modules are meant to be:
-reusable across different products
-optional (i.e. can be left out of a build if desired)
-a separate JAR configuration: a module typically has its own
 JAR configuration, although some modules may have different (similar)
 configurations (e.g., transactions-web for tomcat or jetty) and some
 modules may have no JAR by themselves (e.g., transactions-api) but are
 included in the JAR of another module.

Separating code into different modules encourages as sound layer design
and also makes the build more elegant and maintainable.

On the other hand, too many JARS is cumbersome. Therefore: if two things
are always meant to be installed together, try to put them in the same JAR!


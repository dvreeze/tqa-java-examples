=================
TQA Java Examples
=================

This example project shows how to use the XBRL_ taxonomy query API `TQA`_ (written in Scala) from Java, using Maven.

This example project started from a Maven archetype, using the following command (see how-to-create-a-java-project-with-maven_)::

   mvn archetype:generate \
     -DgroupId=eu.cdevreeze.tqa-java-examples \
     -DartifactId=tqa-java-examples \
     -DarchetypeArtifactId=maven-archetype-quickstart \
     -DinteractiveMode=false

This example project uses Scala project TQA from Java code only, using Maven as the build tool. It is tried to use TQA without
having to know Scala. Although TQA uses the Scala Collections API, not the Java Collections API, it is tried to not have to
import any Scala collections in the Java example code.

There are a few things that expose TQA as a Scala project, though, and they should be kept in mind:

* The `TQA API documentation`_ is in a format that is used for Scala (3), not Java 
* TQA depends on the yaidom_ XML query API (written in Scala)
* The `yaidom API documentation`_ (also in a format used for Scala 3) should be opened in a browser tab as well
* Conversions between Scala collections (used by TQA and yaidom) and Java collections (used by Java code) are documented in CollectionConverters_

Also, from the output of command "mvn dependency:tree" we see the Scala major version as part of the artifact IDs, after an
underscore. That's the case for TQA, yaidom etc. It is very important to never mix multiple Scala major versions (2.12, 2.13, 3 etc.)
in a dependency tree involving Scala dependencies. (The only exception is where the Scala library itself depends on another one.)

As a last Scala-related remark, make sure that Java 8+ is used. Scala 3 (as well as 2.12. and 2.13) requires it.

The program to run is "ShowTaxonomyMemoryFootprint". See the documenting comments there for more
information about how to run it, and what input it expects.

.. _XBRL: https://www.xbrl.org/
.. _`TQA`: https://github.com/dvreeze/tqa
.. _how-to-create-a-java-project-with-maven: https://mkyong.com/maven/how-to-create-a-java-project-with-maven/
.. _`TQA API documentation`: https://javadoc.io/doc/eu.cdevreeze.tqa/tqa_3/latest/index.html
.. _yaidom: https://github.com/dvreeze/yaidom
.. _`yaidom API documentation`: https://javadoc.io/doc/eu.cdevreeze.yaidom/yaidom_3/latest/index.html
.. _CollectionConverters: https://dotty.epfl.ch/api/scala/jdk/javaapi/CollectionConverters$.html


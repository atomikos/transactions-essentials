Atomikos OSGi based on service Tracker.
======================================

1) mvn install at root of project
2) import in your favorite IDE (that should support maven's pom.xml)
3) in service-tracker-osgi-tests execute com.atomikos.osgi.sample.OSGiUnitTest.main()
   -> this starts PaxExamRuntime,  a OSGI runtime convenient for OSGi integration tests
4) open a browser and go to http://localhost:8181


Atomikos OSGi based on service Tracker (AXT version)
======================================

1) mvn install at root of project
2) import in your favorite IDE (that should support maven's pom.xml)
3) in service-tracker-osgi-tests-axt execute com.atomikos.osgi.sample.OSGiContainer.main()
   -> this starts PaxExamRuntime,  a OSGI runtime convenient for OSGi integration tests
4) open a browser and go to http://localhost:8181
5) open jconsole.
6) Explore MBeans -> atomikos.jdbc


Troubelshooting
===============

If you get this error message : 
Could not resolve version. Did you configured the plugin in your maven project?Or maybe you did not run the maven build and you are using an IDE?
This means <project>/target/classes/META-INF/maven/dependencies.properties must be regenerated. Simply invoke "mvn compile" to recreate It.

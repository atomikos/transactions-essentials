package com.atomikos.osgi;

import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.workingDirectory;

import java.io.IOException;

import org.ops4j.pax.exam.ExamSystem;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestContainer;
import org.ops4j.pax.exam.spi.PaxExamRuntime;

public class EmbeddedOSGiServer {

	private static Option[] configuration() {
		return options(
				systemProperty("org.osgi.service.http.port").value("8181"),
				frameworkProperty("osgi.console").value("6666"),
				mavenBundle("org.apache.geronimo.specs", "geronimo-servlet_3.0_spec").version("1.0"),
				mavenBundle("org.osgi", "org.osgi.compendium", "4.3.0"),
				mavenBundle("org.slf4j", "slf4j-api").versionAsInProject(),
				//mavenBundle("org.slf4j", "slf4j-simple").versionAsInProject().noStart(),
				composite(paxweb()),
				composite(blueprintBundles()),
				composite(jetty()),
				composite(atomikosBundles()),
				workingDirectory("target/osgi")

		);
	}

	public static  Option[] atomikosBundles() {
		return options(mavenBundle("javax.transaction", "com.springsource.javax.transaction", "1.1.0"),
				mavenBundle("org.apache.derby", "derby", "10.8.1.2"),
				mavenBundle("com.atomikos", "transactions-osgi").versionAsInProject(),
				mavenBundle("com.atomikos", "osgi-example-db").versionAsInProject(),
				mavenBundle("com.atomikos", "osgi-example-api").versionAsInProject(),
				mavenBundle("com.atomikos", "osgi-example-impl").versionAsInProject(),
				mavenBundle("com.atomikos", "osgi-example-webui").versionAsInProject()
				);
	}
	public static Option[] blueprintBundles() {
		return options(mavenBundle("org.apache.aries.blueprint", "org.apache.aries.blueprint", "1.1.0"),
				mavenBundle("org.apache.aries", "org.apache.aries.util", "1.0.0"),
				mavenBundle("org.apache.aries.proxy", "org.apache.aries.proxy", "1.0.0"));
	}

	private static Option[] paxweb() {
		return options(mavenBundle("org.ops4j.pax.web", "pax-web-spi").version("2.0.2"),
				mavenBundle("org.ops4j.pax.web", "pax-web-extender-war").version("2.0.2"),
				mavenBundle("org.ops4j.pax.web", "pax-web-extender-whiteboard").version("2.0.2"),
				mavenBundle("org.ops4j.pax.web", "pax-web-jetty").version("2.0.2"),
				mavenBundle("org.ops4j.pax.web", "pax-web-runtime").version("2.0.2"),
				mavenBundle("org.ops4j.pax.web", "pax-web-jsp").version("2.0.2"));
	}
	private static Option[] jetty() {
		return options(mavenBundle("org.eclipse.jetty", "jetty-util").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-io").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-http").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-continuation").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-server").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-security").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-xml").version("8.1.4.v20120524"),
				mavenBundle("org.eclipse.jetty", "jetty-servlet").version("8.1.4.v20120524"));
	}
	

	public static void main(String[] args) throws IOException {

		ExamSystem system = PaxExamRuntime.createServerSystem(configuration());

		// create Container (you should have exactly one configured!) and start.
		TestContainer container = PaxExamRuntime.createContainer(system);
		container.start();

	}
}
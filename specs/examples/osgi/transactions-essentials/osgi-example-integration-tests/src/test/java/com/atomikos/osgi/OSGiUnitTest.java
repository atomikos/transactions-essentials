package com.atomikos.osgi;

import static com.atomikos.osgi.EmbeddedOSGiServer.atomikosBundles;
import static com.atomikos.osgi.EmbeddedOSGiServer.blueprintBundles;
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.CoreOptions.workingDirectory;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import com.atomikos.osgi.sample.AccountManager;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OSGiUnitTest {
	// @formatter:off
	@Configuration()
	public static Option[] config() {
		return options(
				frameworkProperty("org.osgi.framework.system.packages").value("org.osgi.framework; version=1.7.0,org.osgi.service.startlevel; version=1.1.0,org.osgi.framework.launch; version=1.1.0,javax.net.ssl,javax.xml.parsers,org.w3c.dom,org.xml.sax,javax.xml.transform.stream,javax.xml.validation,org.w3c.dom.ls,"
						+ "javax.naming,javax.naming.spi,javax.sql,javax.sql.rowset.serial,javax.sql.rowset.spi,javax.sql.rowset,"
						+ "javax.naming.directory,javax.naming.event,javax.naming.ldap,javax.naming.spi,javax.xml.namespace,javax.xml.transform,javax.xml.transform.dom,javax.management,javax.net,javax.management.remote,javax.management.openmbean"),
				mavenBundle("ch.qos.logback", "logback-core", "1.1.1"),
				mavenBundle("ch.qos.logback", "logback-classic", "1.1.1"),
				// hack to allow use of $WORKSPACE/.repository on Jenkins
				composite(allowCustomLocalRepository()),
				// blueprint
				composite(blueprintBundles()),
				junitBundles(),
				composite(atomikosBundles()),
				workingDirectory("target/osgi")
		);
	}

	private static Option allowCustomLocalRepository() {
		//see: https://ops4j1.jira.com/browse/PAXEXAM-543
		String localRepo = System.getProperty("maven.repo.local", "");
		return when(localRepo.length() > 0).useOptions(
		    systemProperty("org.ops4j.pax.url.mvn.localRepository").value(localRepo)
		);
	}

	@Inject
	AccountManager accountManager;

	@Test
	public void withdraw() throws Exception {

		// not sure why withdraw() keep on failing on cloudbees...
		Thread.sleep(1000);
		int accno = 50;
		long balance = accountManager.getBalance(accno);
		int amount = 10;
		accountManager.withdraw(accno, amount);
		assertEquals((balance - amount), accountManager.getBalance(accno));
	}

}

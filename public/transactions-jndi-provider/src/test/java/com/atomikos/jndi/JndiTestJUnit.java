/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.jndi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.sql.XADataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.atomikos.jdbc.AtomikosDataSourceBean;

public class JndiTestJUnit  {

	 private Context ctx ;
	  @Before
		public void before() throws NamingException {
			Hashtable<String, String> env = new Hashtable<String, String>();
	        env.put(Context.INITIAL_CONTEXT_FACTORY, AtomikosContextFactory.class.getName());
	        ctx = new InitialContext(env);
		}
	 
	  @Test
    public void testNameParser() throws Exception {
        AtomikosContext atomikosCtd = new AtomikosContext();
        Name name = atomikosCtd.getNameParser("").parse("java:comp/UserTransaction");
        assertEquals("java:comp/UserTransaction", name.toString());
        assertSame(UserTransactionManager.class, atomikosCtd.lookup(name).getClass());

        name = atomikosCtd.getNameParser(new CompositeName()).parse("java:comp/UserTransaction");
        assertEquals("java:comp/UserTransaction", name.toString());
        assertSame(UserTransactionManager.class, atomikosCtd.lookup(name).getClass());
    }

	  @Test
    public void testDefaultUserTransactionAndResources() throws Exception {
    	AtomikosDataSourceBean adsb =  new AtomikosDataSourceBean();
    	adsb.setMaxPoolSize(1);
    	adsb.setXaDataSource(Mockito.mock(XADataSource.class));
    	adsb.setUniqueResourceName("jdbc/pds");
    	adsb.init();
        assertTrue(adsb == ctx.lookup("jdbc/pds"));
    }

	  @After
	public void after() throws NamingException {
		ctx.close();
	}
	  
	  @Test
	public void userTransactionIsBound() throws NamingException {
		Object lookup = ctx.lookup("java:comp/UserTransaction");
		System.err.println(lookup);
		Object lookup2 = ctx.lookup("java:comp/UserTransaction");
		System.err.println(lookup2);
		assertTrue( lookup instanceof UserTransactionManager);
	}



}
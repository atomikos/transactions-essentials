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

package com.atomikos.jndi.spring;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;

public class AtomikosSpringInitialContextFactory implements InitialContextFactory {

	private static final Logger log = LoggerFactory.createLogger(AtomikosSpringInitialContextFactory.class);
	private static Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

	public Context getInitialContext(Hashtable environment) throws NamingException {
		Resource resource = null;
		Object value = environment.get(Context.PROVIDER_URL);
		String key = "atomikos.xml";

		if (value == null) {
			resource = new ClassPathResource(key);
		} else {
			if (value instanceof Resource) {
				resource = (Resource) value;
			} else {
				ResourceEditor editor = new ResourceEditor();
				key = value.toString();
				editor.setAsText(key);
				resource = (Resource) editor.getValue();
			}
		}

		BeanFactory context = loadContext(resource, key);
		Context answer = new DefaultContext(environment);
		return answer;
	}

	private BeanFactory loadContext(Resource resource, String key) {
		synchronized (cache) {
			BeanFactory answer = (BeanFactory) cache.get(key);
			if (answer == null) {
				answer = createContext(resource);
				cache.put(key, answer);
			}
			return answer;
		}
	}

	private BeanFactory createContext(Resource resource) {
		log.logInfo("Loading JNDI context from: " + resource);
		GenericXmlApplicationContext context = new GenericXmlApplicationContext(resource);
		return context;
	}

}

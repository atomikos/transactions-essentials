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

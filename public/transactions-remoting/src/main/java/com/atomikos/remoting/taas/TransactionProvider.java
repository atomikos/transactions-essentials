/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.taas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.support.HeaderNames;

/**
 * Custom marshalling / unmarshalling of transaction data in a request / response.
 */

@Consumes(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
@Produces(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
@Provider
public class TransactionProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionProvider.class);
	
	@Context
	protected Providers providers;

	public long getSize(Object l, Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
		return -1;
	}

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
		return true;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		String content = getStringFromInputStream(entityStream);
		
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace("Incoming REST request payload:\n" + content);
		}
		if(content.contains("|")) {
			return content.split(Pattern.quote("|"));
		} 
		return content;
	}

	private String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			LOGGER.logTrace("Failed to read REST payload.", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		return sb.toString();

	}

	@Override
	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		if(t.getClass().isArray()) {
			Object[] array = (Object[])t;
			for (Object content : array) {
				entityStream.write(content.toString().getBytes());
				entityStream.write("|".getBytes());
			}	
		} else {
			entityStream.write(t.toString().getBytes());
		}
		
		
	}
}

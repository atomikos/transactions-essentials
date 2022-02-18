/**
 * Copyright (C) 2000-2022 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting.twopc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.atomikos.remoting.taas.TransactionProvider;

@Consumes(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
@Produces(HeaderNames.MimeType.APPLICATION_VND_ATOMIKOS_JSON)
@Provider
public class ParticipantsProvider implements MessageBodyWriter<Map<String,Integer>>, MessageBodyReader<Map<String,Integer>> {

	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionProvider.class);
	
	@Context
	protected Providers providers;

	public long getSize(Map<String,Integer> l, Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
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
	public HashMap<String,Integer> readFrom(Class<Map<String,Integer>> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		String content = getStringFromInputStream(entityStream);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.logTrace("Incoming REST request payload:\n" + content);

		}
		HashMap<String,Integer> result = new HashMap<>();
		String keyValues =content.replaceAll("\\{", "");
		keyValues = keyValues.replaceAll("\\}", "");
		String[] array = keyValues.split(",");
		for (String entry : array) { //"key"="value"
			entry = entry.replaceAll("\"", ""); //get git of "
			String[] pair = entry.split("=");
			result.put(pair[0], Integer.valueOf(pair[1]));
		}
		
		
		return result;
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
	public void writeTo(Map<String,Integer> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		String comma="";
		for (Entry<String,Integer> entry : t.entrySet()) {
			buffer.append(comma);
			buffer.append('{').append('"').append(entry.getKey()).append('"').append('=').append(entry.getValue()).append('}');
			comma=",";
		}
		buffer.append("}");
		entityStream.write(buffer.toString().getBytes());

	}


}

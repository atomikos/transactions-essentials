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

package com.atomikos.icatch.tcc.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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
import com.atomikos.tcc.rest.Transaction;

@Consumes(MimeTypes.MIME_TYPE_COORDINATOR_JSON)
@Produces(MimeTypes.MIME_TYPE_COORDINATOR_JSON)
@Provider
public class TransactionProvider implements MessageBodyWriter<Transaction>, MessageBodyReader<Transaction> {

	private static final Logger LOGGER = LoggerFactory.createLogger(TransactionProvider.class);
	@Context
	protected Providers providers;

	public long getSize(Transaction l, Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
		return -1;
	}

	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mt) {
		return Transaction.class.isAssignableFrom(type);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Transaction.class.isAssignableFrom(type);
	}

	@Override
	public Transaction readFrom(Class<Transaction> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		MediaType realMediaType = MediaType.APPLICATION_JSON_TYPE;
		MessageBodyReader<Transaction> reader = this.providers.getMessageBodyReader(type, genericType, annotations,
				realMediaType);
		InputStream in = entityStream;
		if (LOGGER.isDebugEnabled()) {
			String content = getStringFromInputStream(entityStream);
			LOGGER.logDebug("Incoming REST request payload:\n" + content);
			in = new ByteArrayInputStream(content.getBytes());
		}

		return reader.readFrom(type, genericType, annotations, realMediaType, httpHeaders, in);
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
			LOGGER.logDebug("Failed to read REST payload.", e);
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
	public void writeTo(Transaction t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
			WebApplicationException {
		MediaType realMediaType = MediaType.APPLICATION_JSON_TYPE;
		MessageBodyWriter<Transaction> writer = this.providers.getMessageBodyWriter(Transaction.class, genericType,
				annotations, realMediaType);

		writer.writeTo(t, type, genericType, annotations, realMediaType, httpHeaders, entityStream);

	}
}
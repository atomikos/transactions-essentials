/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import java.io.Serializable;

public class PendingTransactionRecord implements Serializable {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final String COLUMN_SEPARATOR = "|";

	private static final long serialVersionUID = 1L;

	public final String id;
	
	public final TxState state;
	
	public final long expires;
	
	public final String superiorId;

    public final String recoveryDomainName;

	
	
	public PendingTransactionRecord(String id, TxState state, long expires, String recoveryDomainName) {
		this(id, state, expires, recoveryDomainName, null);
	}

	public PendingTransactionRecord(String id, TxState state, long expires, String recoveryDomainName, String superiorId) {
		super();
		this.id = id;
		this.state = state;
		this.expires = expires;
		this.superiorId = superiorId;
		this.recoveryDomainName = recoveryDomainName;
	}
	
	public String toRecord() {
		StringBuilder sb = new StringBuilder();
		sb.append(id)
		.append(COLUMN_SEPARATOR)
		.append(state.name())
		.append(COLUMN_SEPARATOR)
		.append(expires)
	    .append(COLUMN_SEPARATOR)
	    .append(recoveryDomainName)
		.append(COLUMN_SEPARATOR)
		.append(superiorId==null?"":superiorId)
		.append(LINE_SEPARATOR);
		return sb.toString();
	}

	/**
	 * 
	 * @throws IllegalArgumentException If the supplied value cannot be parsed.
	 */
	public static PendingTransactionRecord fromRecord(String record) {
		String[] properties = record.split("\\|");
		if (properties.length < 4) {
			throw new IllegalArgumentException("Invalid record value supplied: " + record);
		}
		String id = properties[0];
		TxState state = TxState.valueOf(properties[1]);
		Long expires = Long.valueOf(properties[2]);
		String recoveryDomainName = String.valueOf(properties[3]);
		String superiorId = null;
		if(properties.length > 4) {
			superiorId = properties[4];	
		}
		
		return new PendingTransactionRecord(id, state, expires, recoveryDomainName, superiorId);
	}
	
	
	public PendingTransactionRecord markAsTerminated() {
		return new PendingTransactionRecord(id, TxState.TERMINATED, expires, recoveryDomainName, superiorId);
	}
	
	public PendingTransactionRecord markAsCommitting() {
        return new PendingTransactionRecord(id, TxState.COMMITTING, expires, recoveryDomainName, superiorId);
    }
	
	@Override
	public String toString() {
	    return toRecord();
	}
	
}

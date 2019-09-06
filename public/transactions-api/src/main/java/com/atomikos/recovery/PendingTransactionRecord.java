/**
 * Copyright (C) 2000-2019 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.recovery;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PendingTransactionRecord {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final String COLUMN_SEPARATOR = "|";

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
	
	public static Collection<PendingTransactionRecord> findAllDescendants(PendingTransactionRecord entry, Collection<PendingTransactionRecord> collection) {
	    return collectLineages(
	            (PendingTransactionRecord r)-> entry.id.equals(r.superiorId), 
	            collection);
    }
	
    public static void removeAllDescendants(PendingTransactionRecord entry, Collection<PendingTransactionRecord> allCoordinatorLogEntries) {
        Collection<PendingTransactionRecord>  descendants = findAllDescendants(entry, allCoordinatorLogEntries);
        for (PendingTransactionRecord descendant : descendants) {
            allCoordinatorLogEntries.remove(descendant);
        }
    }
    
    /**
     *
     * @param predicate
     * @param collection
     * @return A collection of all descendants of records that match the given predicate, including the matching records.
     */
    public static Collection<PendingTransactionRecord> collectLineages(AncestorPredicate predicate, Collection<PendingTransactionRecord> collection) {
        Collection<PendingTransactionRecord> results = new HashSet<>();
        Map<String, PendingTransactionRecord> map = map(collection);
        for (PendingTransactionRecord record : collection) {
            if (!predicate.holdsFor(record)) {
                if (record.superiorId != null) { //look for ancestor that matches                   
                    Collection<PendingTransactionRecord> ret = new HashSet<>();
                    collectAncestors(ret, record.superiorId, predicate, map);
                    if(!ret.isEmpty()) {
                        ret.add(record);
                        results.addAll(ret);    
                    }
                }
            } else { //match found already
                results.add(record);  
            }
        }
        return results;
    }
	
    private static Map<String, PendingTransactionRecord> map(Collection<PendingTransactionRecord> collection) {
        Map<String, PendingTransactionRecord> ret = new HashMap<>();
        for (PendingTransactionRecord record : collection) {
            ret.put(record.id, record);
        }
        return ret;
    }

    private static void collectAncestors(Collection<PendingTransactionRecord> collector, String superiorId, AncestorPredicate predicate, Map<String,PendingTransactionRecord> map) {
        PendingTransactionRecord superior = map.get(superiorId);
        if (superior != null) {
            if (predicate.holdsFor(superior)) {
                collector.add(superior);
            } else if (superior.superiorId != null) {
                collectAncestors(collector, superior.superiorId, predicate, map);
            } 
        } 
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

	/**
	 * 
	 * @param recoveryDomainName
	 * @return True iff this is a foreign record in the given domain.
	 */
    public boolean isForeignInDomain(String recoveryDomainName) {
           return !this.recoveryDomainName.equals(recoveryDomainName);
    }

    public boolean isRecoveredByDomain(String recoveryDomainName) {
        boolean ret = true;
        if (isForeignInDomain(recoveryDomainName) && superiorId!= null && superiorId.startsWith("http")) {
            // foreign record with remote recovery available => 
            // this record is recovered by remote recovery in the foreign domain
            ret = false;
        }
        return ret;
    }
    
    public boolean isLocalRoot(String recoveryDomainName) {
        return isForeignInDomain(recoveryDomainName) || superiorId == null;
    }
    
    public boolean allowsHeuristicTermination(String recoveryDomainName) {
        boolean ret = false;
        if (isForeignInDomain(recoveryDomainName) && 
            isRecoveredByDomain(recoveryDomainName) && 
            state.equals(TxState.IN_DOUBT)) {
            ret = true;
        }
        return ret;
    }

    @FunctionalInterface
    public static interface AncestorPredicate {
        
        boolean holdsFor(PendingTransactionRecord record);
    }
    
    public static Collection<String> extractCoordinatorIds(Collection<PendingTransactionRecord> collection, TxState... statesToFilterOn) {
        HashSet<String> ret = new HashSet<>();
        for (PendingTransactionRecord entry : collection) {
            if (entry.state.isOneOf(statesToFilterOn)) {
                ret.add(entry.id);
            }
        }
        return ret;
    }
}

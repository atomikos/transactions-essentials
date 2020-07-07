/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.remoting;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.imp.CompositeTransactionAdaptor;
import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.remoting.twopc.ParticipantAdapter;


public class Parser {

    private static final Logger LOGGER = LoggerFactory.createLogger(Parser.class);
    
    /**
     * Parses an incoming propagation. This method should be forward and backward compatible:
     * 
     * <ul>
     * <li>New (extra) elements in the incoming propagationAsString are logged but ignored, and</li>
     * <li>If new (extra) elements are expected but not found then this is also logged and ignored.</li>
     * </ul>
     * 
     * @param propagationAsString 
     * 
     * @return The parsed propagation.
     */
	public Propagation parsePropagation(String propagationAsString) {
		if (propagationAsString == null) {
			throw new IllegalArgumentException("The supplied string must not be null");
		}
		LOGGER.logDebug("Parsing incoming propagation: " + propagationAsString);
		String[] properties = propagationAsString.split(",");
		if (properties.length < 6) {
			throw new IllegalArgumentException("The supplied propagation is incomplete");
		}
		String version = parseProperty("version", properties[0]);
		if (!Propagation.VERSION.equals(version)) {
		    throw new IllegalArgumentException("The supplied propagation is of a more recent, incompatible version: " + version);
		}
		String domain = parseProperty("domain", properties[1]);
		String timeoutAsString = parseProperty("timeout", properties[2]);
		long timeout = Long.parseLong(timeoutAsString);
		String serialAsString = parseProperty("serial", properties[3]);
		String recoveryCoordinatorURI = parseProperty("recoveryCoordinatorURI", properties[4]);
		boolean serial = Boolean.parseBoolean(serialAsString);
		
		RecoveryCoordinator recoveryCoordinator = new RecoveryCoordinator() {			

			@Override
			public String getURI() {
				return recoveryCoordinatorURI;
			}

            @Override
            public String getRecoveryDomainName() {
                return domain;
            }

		};
		
		int lineageOffset = findFirstOccurrence("parent", properties, 5);
		CompositeTransactionAdaptor rootTransaction = findNextAncestor(properties, serial, lineageOffset, recoveryCoordinator);
		CompositeTransactionAdaptor parentTransaction = null;
		lineageOffset = findLastOccurrence("parent", properties, lineageOffset + 1);
		if (lineageOffset < properties.length) { 
		    parentTransaction = findNextAncestor(properties, serial, lineageOffset, recoveryCoordinator);
		} else {//propagation is for a root
		    parentTransaction = rootTransaction;
		}
		return new Propagation(domain, rootTransaction, parentTransaction, serial, timeout, recoveryCoordinatorURI);
	}

    private CompositeTransactionAdaptor findNextAncestor(String[] properties, boolean serial, int lineageOffset,
            RecoveryCoordinator recoveryCoordinator) {
        CompositeTransactionAdaptor parentTransaction = null;
        if (lineageOffset < properties.length) { 
            String parentId = parseProperty("parent", properties[lineageOffset]);
            parentTransaction = new CompositeTransactionAdaptor(parentId, serial, recoveryCoordinator);
            boolean nextParentFound = false;
            for (int i = lineageOffset + 1; i < properties.length && !nextParentFound; i++) {
                String[] property = parseAssignment(properties[i]);
                if ("parent".equals(property[0])) {
                    nextParentFound = true;
                } else {
                    String propertyKey = property[0].substring(9); //remove 'property.' prefix
                    parentTransaction.setProperty(propertyKey, property[1]);
                }
            }
        }
        return parentTransaction;
    }
    
    private int findLastOccurrence(String propertyName, String[] properties, int startingPosition) {
       int next = startingPosition;
       int ret = properties.length;
       while (next < properties.length) {
           next = findFirstOccurrence(propertyName, properties, next);
           if (next < properties.length) {
               ret = next;
           }
           next++;
       }
       return ret;
    }

    private int findFirstOccurrence(String propertyName, String[] properties, int startingPosition) {
        int ret = properties.length;
        boolean found = false;
        for (int i = startingPosition; i < properties.length && !found; i++) {
            try {
                parseProperty(propertyName, properties[i]);
                ret = i;
                found = true;
            } catch (IllegalArgumentException continueLoop) {
            }
        }
        return ret;
    }

    private String parseProperty(String expectedPropertyName, String propertyAssignmentExpression) {
		String[] parsed = parseAssignment(propertyAssignmentExpression);
		if (!parsed[0].equals(expectedPropertyName)) {
			throw new IllegalArgumentException("Expected: " + expectedPropertyName + " but found: " + parsed[0]);
		}
		return parsed[1];
	}

	private String[] parseAssignment(String propertyAssignmentExpression) {
		String[] parsed = propertyAssignmentExpression.split("=");
		if (parsed.length != 2) {
			throw new IllegalArgumentException(propertyAssignmentExpression + " is not a valid part");
		}
		return parsed;
	}

	/**
     * Parses an incoming extent. This method should be forward and backward compatible:
     * 
     * <ul>
     * <li>New (extra) elements in the incoming extentAsString are logged but ignored, and</li>
     * <li>If new (extra) elements are expected but not found then this is also logged and ignored.</li>
     * </ul>
     * 
     * @param extentAsString 
     * 
     * @return The parsed extent.
     */
	public Extent parseExtent(String extentAsString) {
		if (extentAsString == null) {
			return null;
		}
		LOGGER.logDebug("Parsing incoming extent: " + extentAsString);
		String[] properties = extentAsString.split(",");
		if (properties.length < 5) {
			throw new IllegalArgumentException("The supplied extent is incomplete: "+ extentAsString);
		}
		String version  = parseProperty("version", properties[0]);
		if (!Extent.VERSION.equals(version)) {
            throw new IllegalArgumentException("The supplied extent is of a more recent, incompatible version: " + version);
        }
		String parentTransactionId = parseProperty("parent", properties[1]);
		Extent ret = new Extent(parentTransactionId);
		
		int participantOffset = findFirstOccurrence("uri", properties, 2);
		
		if ((properties.length - participantOffset) / 3 == 0) {
            throw new IllegalArgumentException("The supplied extent is incomplete: "+ extentAsString);
        }		

		while (participantOffset < properties.length) {
		    extractParticipantInfo(properties, ret, participantOffset);
		    participantOffset = findFirstOccurrence("uri", properties, participantOffset + 1);
		}
		
		return ret;
	}

    private void extractParticipantInfo(String[] properties, Extent ret, int offSet) {
        Map<String, Integer> remoteParticipants = new HashMap<>();
        String uri = parseProperty("uri", properties[offSet]);
        String responseCountAsString = parseProperty("responseCount", properties[offSet+1]);
        String directAsString = parseProperty("direct", properties[offSet+2]);
        boolean direct = Boolean.parseBoolean(directAsString);
        int responseCount = Integer.parseInt(responseCountAsString);
        if (direct) {
        	try {
        		ParticipantAdapter p = new ParticipantAdapter(new URI(uri));
        		ret.add(p, responseCount);
        	} catch (URISyntaxException e) {
        		throw new IllegalArgumentException(e);
        	}
        } else {
        	remoteParticipants.put(uri, responseCount);
        	ret.addRemoteParticipants(remoteParticipants);
        }
        int i = offSet + 3;
        while (i < properties.length && !properties[i].startsWith("uri=")) {
            LOGGER.logTrace("Ingoring unknown element in extent: " + properties[i]);
            i++;
        }
    }

}

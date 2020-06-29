/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 *
 * The extent carries the information about the 'size' of a propagation 
 * after it returns: the directly and indirectly invoked servers, and the orphan 
 * detection information for those.
 * 
 * This interface is a system interface; it should not be handled by application
 * level code (besides shipping it around via toString()).
 *
 * This class (and its parsing) represent the minimum information required for
 * transactions to work across remoting calls. All values are required.
 * 
 * In addition, we are liberal in parsing: any additional, unknown values are ignored - 
 * so future releases can add extra, optional properties and still work with 
 * installations of this release.
 */

public class Extent {
    
    /**
     * Major version indicator. Only change this for future releases 
     * that add incompatible changes such as adding/removing required properties
     * and/or changing the semantics of existing properties. As long as optional properties
     * are added/removed, the version can stay the same.
     */
    
    public static final String VERSION ="2019";
    
    private final Map<String, Integer> participants = new Hashtable<String,Integer>();
    private boolean queried = false;
    private final Stack<Participant> directs = new Stack<Participant>();
    private String parentTransactionId; //null for root

    public Extent() {
    }
    
    public Extent (String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }
    
    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public void addRemoteParticipants(Map<String,Integer> participants)
            throws IllegalStateException, SysException {
        if (participants == null) {
            return;
        }
        Set<String> parts = participants.keySet();
        for (String participant : parts) {
            Integer count =  this.participants.get(participant);
            if (count == null) {
                count = 0;
            }

            Integer cnt = participants.get(participant);
            count = count.intValue() + cnt.intValue();

            this.participants.put(participant, count);
            // NOTE: this will replace the old participant, and if
            // it is a proxy then the buffered heuristic msgs will
            // also be replaced. This loses info if multiple PARALLEL calls
            // went to the same FIRST-ORDER server (i.e., directly invoked).
            // Never mind, though: it is considered bad practice
            // to execute parallel calls if they might act on the same
            // data. This is the case if they go to the same directly
            // invoked server.
        }
    }

    /**
     * @return Map Mapping URIs of remote participants (directly or indirectly invoked)
     * to Integer counts that represent the number of invocations detected by each participant.
     */

    public Map<String,Integer> getRemoteParticipants()
    {
        queried = true;
        return new HashMap<String,Integer>(participants);
    }


    /**
     * 
     * @return Stack A stack of <b>direct</b> participants. Direct participants
     * are those that need to be added to the client TM's two-phase
     * commit set.
     *
     * NOTE: If a participant occurs in the direct participant set,
     * it will also be part of the remote set.
     */
     
    
    @SuppressWarnings("unchecked")
    public Stack<Participant> getParticipants ()
    {
        queried = true;
        return (Stack<Participant>) directs.clone();
    }

    /**
     * Adds a participant to the extent.
     * This method is called at the server side, in order to add the work done
     * to the two-phase commit set of the calling (client) side, as well as to 
     * make sure that orphan information is propagated through the system.
     *
     * @param participant This instance will
     *be added to the indirect <b>as well as to the direct</b> participant set.
     *
     * @param count The number of invocations detected by the adding client.
     * @throws IllegalStateException If no longer allowed.
     * @throws SysException
     */

    public synchronized void add(Participant participant, int count)
            throws SysException, IllegalStateException {
        Hashtable<String,Integer> table = new Hashtable<String,Integer>();
        table.put(participant.getURI(), count);
        addRemoteParticipants(table);
        directs.push(participant);
    }

    /**
     * Merges another extent into this one.
     *
     *@param extent The extent to add.
     *
     *@throws IllegalStateException If no longer allowed.
     *@throws SysException 
     */

    public synchronized void add ( Extent extent )
            throws IllegalStateException, SysException
    {
        if (queried)  throw new IllegalStateException("Adding extent no longer allowed");
        addRemoteParticipants(extent.getRemoteParticipants());
        Enumeration<Participant> enumm = extent.getParticipants().elements();
        while (enumm.hasMoreElements()) {
            Participant part =  enumm.nextElement();
            directs.push(part);
        }
    }
    
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("version=").append(VERSION).append(",");
        String delimiter = "";
        if (parentTransactionId != null) { //should not be null but we never know
            ret.append("parent=").append(parentTransactionId);
            delimiter = ",";
        }
        Set<String> alreadyAdded = new HashSet<String>();
        for (Participant p : directs) {
            ret.append(delimiter).append("uri=").append(p.getURI()).append(",").
                append("responseCount=").append(participants.get(p.getURI())).append(",").
                append("direct=").append("true");
            delimiter = ",";
            alreadyAdded.add(p.getURI());
        }
        for (String pUri : participants.keySet()) {
            if (!alreadyAdded.contains(pUri)) {
                ret.append(delimiter).
                append("uri=").append(pUri).append(",").
                append("responseCount=").append(participants.get(pUri)).append(",").
                append("direct=").append("false");
            }
        }
        return ret.toString();
    }
  
    											
}

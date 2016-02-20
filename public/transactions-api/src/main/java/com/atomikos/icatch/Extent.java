/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import java.util.Map;
import java.util.Stack;

/**
 *
 *
 * The extent carries the information about the 'size' of a propagation 
 * after it returns: the directly and indirectly invoked servers, and the orphan 
 * detection information for those.
 * 
 * This interface is a system interface; it should not be handled by application
 * level code (besides shipping it around).
 *
 */

public interface Extent extends java.io.Serializable
{
    /**
     * @return Map Mapping URIs of remote participants (directly or indirectly invoked)
     * to Integer counts that represent the number of invocations detected by each participant.
     */

    public Map<String,Integer> getRemoteParticipants();
    
    /**
     * Merges another extent into this one.
     *
     *@param extent The extent to add.
     *
     *@exception IllegalStateException If no longer allowed.
     *@exception SysException 
     */

    public void add ( Extent extent ) throws  IllegalStateException, SysException;
    
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
     * @exception IllegalStateException If no longer allowed.
     * @exception SysException
     */
     
    public void add ( Participant participant , int count ) 
    	throws IllegalStateException, SysException;
    	
    
     /**
      * 
      * @return Stack A stack of <b>direct</b> participants. Direct participants
      * are those that need to be added to the client TM's two-phase
      * commit set.
      *
      * NOTE: If a participant occurs in the direct participant set,
      * it will also be part of the remote set.
      */
      
    public Stack<Participant> getParticipants();

  
    											
}

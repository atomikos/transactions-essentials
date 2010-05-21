/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
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

package com.atomikos.icatch;
import java.util.Hashtable;
import java.util.Stack;

/**
 *
 *
 *The extent carries the information about the 'size' of a propagation 
 *after it returns: the directly andindirectly invoked servers, and the orphan 
 *detection information for those.
 *This interface is a system interface; it should not be handled by application
 *level code.
 *
 */

public interface Extent extends java.io.Serializable
{
    /**
     *Get the remote participants indirectly invoked.
     *
     *@return Hashtable Mapping URIs of remote 
     *participants to Integer counts.
     */

    public Hashtable getRemoteParticipants();
    
    /**
     *Add another extent  to the current extent.
     *
     *@param extent The extent to add.
     *@exception IllegalStateException If no longer allowed.
     *@exception SysException Unexpected error.
     */

    public void add ( Extent extent ) throws  IllegalStateException, SysException;
    
    /**
     *Add a participant to the extent.
     *This method is called at the server side, in order to add the work done
     *to the two-phase commit set of the calling (client) side, as well as to 
     *make sure that orphan information is propagated through the system.
     *
     *@param participant The participant to add. This instance will
     *be added to the indirect <b>as well as to the direct</b> participant set.
     *
     *@param count The number of invocations detected by the adding client.
     *@exception IllegalStateException If no longer allowed.
     *@exception SysException Unexpected error.
     */
     
    public void add ( Participant participant , int count ) 
    	throws IllegalStateException, SysException;
    	
    
     /**
      *Get the set of <b>direct</b> participants of this extent.
      *@return Stack A stack of direct participants. Direct participants
      *are those that need to be added to the client TM's two-phase
      *commit set.
      *NOTE: If a participant occurs in the direct participant set,
      *it will also be part of the remote set.
      */
      
    public Stack getParticipants();
    
    ///**
//     *Get the participant to add at the client TM.
//     *By calling this method, the client TM can get a handle
//     *to include in its coordinator 2PC set for the 
//     *composite transaction.
//     */
//     
//     
//    public Participant getParticipant()
//        throws SysException;
  
    											
}

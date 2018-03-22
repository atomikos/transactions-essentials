/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch;

import java.util.Stack;

/**
 * Information about the transaction context that can be 
 * shipped along with remote request, to make the other side
 * participate in the transaction present for the current thread in this VM.
 */

public interface Propagation extends java.io.Serializable 
{
    /**
     * Gets the ancestor information as a stack.
     *
     * @return Stack The ancestor transactions.
     */

     Stack<CompositeTransaction> getLineage();
    
    /**
     *
     * @return boolean True if serial mode was set.
     */
    
     boolean isSerial();
    
    
     /**
      *
      *@return long The time left before timeout, in millis.
      */
      
     long getTimeOut();
    
}

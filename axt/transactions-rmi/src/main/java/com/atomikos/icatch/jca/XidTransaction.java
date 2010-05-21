package com.atomikos.icatch.jca;

import java.util.Properties;
import java.util.Stack;

import javax.transaction.xa.Xid;

import com.atomikos.datasource.xa.XID;
import com.atomikos.icatch.CompositeCoordinator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RecoveryCoordinator;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxAwareParticipant;
import com.atomikos.icatch.Synchronization;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionControl;

/**
 * 
 * Copyright &copy; 2005, Atomikos. All rights reserved.
 * 
 * A wrapper class that can be used to disguise
 * Xid instances as CompositeTransactions. 
 * Needed to implement JCA inbound transaction flow.
 * 
 */

class XidTransaction
    implements CompositeTransaction, 
    CompositeCoordinator, RecoveryCoordinator
{
	
	private static void fail()
	throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException ( "This method is not available for an imported Xid" );
	}
	
	static String convertXidToTid ( Xid xid )
	{
		StringBuffer ret = new StringBuffer();
        
		//convert the xid to a unique String
		//by concatenating the different parts
		//of the global id and branch tags;
		//add a '+' sign in between to make it
		//easier to split both parts again 
		//during recovery

		ret.append ( new String ( xid.getGlobalTransactionId() ) );
		ret.append ( "+" );
		ret.append ( new String ( xid.getBranchQualifier() ) );

		return ret.toString();		
	}
	
	static Xid convertTidToXid ( String tid )
	{
		int index = tid.lastIndexOf ( '+' );
		String globalId = tid.substring ( 0 , index );
		String branch = tid.substring ( index + 1 );
		Xid ret = new XID ( globalId , branch );
		return ret;
	}

	private Xid xid;
	//the Xid being wrapped
    
    private Properties properties_;
	
	XidTransaction ( Xid xid )
	{
		//assert that no + character is present
		//to be able to reconstruct Xid from 
		//Atomikos tid during recovery
		String check = 
			new String ( xid.getGlobalTransactionId() ) +
			new String ( xid.getBranchQualifier() );
		if ( check.indexOf ( '+' ) >= 0 )
			throw new IllegalArgumentException ( 
			"Xid should not contain a + character");
			
		this.xid = xid;
        properties_ = new Properties();
	}

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isRoot()
     */
    public boolean isRoot()
    {
    	//XA inflowing transactions are always treated
    	//as root transactions for which a local subtx
    	//is created
        return true;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getLineage()
     */
    public Stack getLineage()
    {
        Stack lineage = new Stack();
        lineage.push ( this );
        return lineage;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getTid()
     */
    public String getTid()
    {
        return convertXidToTid ( xid );
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isAncestorOf(com.atomikos.icatch.CompositeTransaction)
     */
    public boolean isAncestorOf(CompositeTransaction ct)
    {
        //return false is the easiest to do
        //at the expense of losing heavy lock inheritance
        return false;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isDescendantOf(com.atomikos.icatch.CompositeTransaction)
     */
    public boolean isDescendantOf(CompositeTransaction ct)
    {
        //return false is easiest to do
        //at the expense of losing heavy lock inheritance
        return false;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isRelatedTransaction(com.atomikos.icatch.CompositeTransaction)
     */
    public boolean isRelatedTransaction(CompositeTransaction ct)
    {
        return isSameTransaction ( ct );
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isSameTransaction(com.atomikos.icatch.CompositeTransaction)
     */
    public boolean isSameTransaction(CompositeTransaction ct)
    {
        return ct.getTid().equals( getTid() );
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getCompositeCoordinator()
     */
    public CompositeCoordinator getCompositeCoordinator()
        throws UnsupportedOperationException, SysException
    {
        return this;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#addParticipant(com.atomikos.icatch.Participant)
     */
    public RecoveryCoordinator addParticipant(Participant participant)
        throws SysException, IllegalStateException
    {
        throw new UnsupportedOperationException ( "Not implemented" );
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#registerSynchronization(com.atomikos.icatch.Synchronization)
     */
    public void registerSynchronization(Synchronization sync)
        throws IllegalStateException, UnsupportedOperationException, SysException
    {
        throw new UnsupportedOperationException ( "Not implemented" );

    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#addSubTxAwareParticipant(com.atomikos.icatch.SubTxAwareParticipant)
     */
    public void addSubTxAwareParticipant(SubTxAwareParticipant subtxaware)
        throws SysException, UnsupportedOperationException, IllegalStateException
    {
        throw new UnsupportedOperationException ( "Not implemented" );

    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isSerial()
     */
    public boolean isSerial()
    {
    	//J2EE does not support non-serial transactions
        return true;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getTransactionControl()
     */
    public TransactionControl getTransactionControl()
        throws UnsupportedOperationException
    {
        fail();
    	return null;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#isLocal()
     */
    public boolean isLocal()
    {
        //an imported Xid transaction is by definition not local
        return false;
    }

    /**
     * @see com.atomikos.icatch.CompositeCoordinator#getCoordinatorId()
     */
    public String getCoordinatorId()
    {
    	//this is the root, so return our own tid
        return getTid();
    }

    /**
     * @see com.atomikos.icatch.CompositeCoordinator#getRecoveryCoordinator()
     */
    public RecoveryCoordinator getRecoveryCoordinator()
    {
        return this;
    }

    /**
     * @see com.atomikos.icatch.CompositeCoordinator#getTags()
     */
    public HeuristicMessage[] getTags()
    {
        
        return new HeuristicMessage[0];
    }

    /**
     * @see com.atomikos.icatch.RecoveryCoordinator#replayCompletion(com.atomikos.icatch.Participant)
     */
    public Boolean replayCompletion(Participant participant)
        throws IllegalStateException
    {
        //act as if we don't know anything
        //because XA does not support this 
        return null;
    }

    /**
     * @see com.atomikos.finitestates.Stateful#getState()
     */
    public Object getState()
    {
        fail();
        return null;
    }
    
    public String getURI()
    {
    	return getCoordinatorId();
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#createSubTransaction()
     */
    public CompositeTransaction createSubTransaction() throws SysException, IllegalStateException
    {
        fail();
        return null;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setSerial()
     */
    public void setSerial() throws IllegalStateException, SysException
    {
        fail();
        
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getLocalSubTxCount()
     */
    public int getLocalSubTxCount()
    {
        
        return 0;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setTag(com.atomikos.icatch.HeuristicMessage)
     */
    public void setTag(HeuristicMessage tag)
    {
        fail();
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getExtent()
     */
    public Extent getExtent()
    {
        fail();
        return null;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#getTimeout()
     */
    public long getTimeout()
    {
        fail();
        return 0;
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#setRollbackOnly()
     */
    public void setRollbackOnly()
    {
        fail();
        
    }

    /**
     * @see com.atomikos.icatch.CompositeTransaction#commit()
     */
    public void commit() throws HeurRollbackException, HeurMixedException, HeurHazardException, SysException, SecurityException, RollbackException
    {
        fail();
        
    }


    /**
     * @see com.atomikos.icatch.CompositeTransaction#rollback()
     */
    public void rollback() throws IllegalStateException, SysException
    {
        fail();
        
    }
    
    public Boolean isRecoverableWhileActive()
    {
        return new Boolean ( false );
    }



    public void setProperty ( String name , String value ) throws IllegalArgumentException
    {
        if ( getProperty ( name ) == null ) properties_.setProperty ( name , value );
        
    }

    public String getProperty ( String name )
    {
        return properties_.getProperty ( name );
    }


    public Properties getProperties()
    {
        return ( Properties ) properties_.clone();
    }



    public void setRecoverableWhileActive () throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
        
    }

}

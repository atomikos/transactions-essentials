package com.atomikos.icatch.jta.hibernate3.mock;

import com.atomikos.logging.LoggerFactory;
import com.atomikos.logging.Logger;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.atomikos.icatch.system.Configuration;

 /**
  *
  *
  *A test class simulating an XAResource. 
  *Useful for the JTA system test. This class is a light-weight, non-persistent
  *resource that allows manipulation of the return values of 
  *the XAResource calls. 
  */
  
public class TestXAResource
implements XAResource, Cloneable
{
	private static final Logger LOGGER = LoggerFactory.createLogger(TestXAResource.class);

  
    
    /**
     *Constant indicating commit failure mode.
     */
     
    public static final int FAIL_COMMIT = 0;
    
   /**
     *The lower bound on the failure mode value.
     */
     
   public static final int FAIL_MIN = FAIL_COMMIT;
    
     /**
      *Constant indicating start failure mode.
      */
      
    public static final int FAIL_START = 1;
    
     /**
      *Constant indicating end failure mode.
      */
      
    public static final int FAIL_END = 2;
    
     /**
      *Constant indicating rollback failure mode.
      */
    
    public static final int FAIL_ROLLBACK = 3;
    
     /**
      *Constant indicating prepare failure mode.
      */
      
    public static final int FAIL_PREPARE = 4;
    
    /**
     * Constant indicating failure on isSameRM
     */
    public static final int FAIL_IS_SAME_RM = 5;
      
     /**
      *The upper bound of the failure mode values.
      */
    
    public static final int FAIL_MAX = FAIL_IS_SAME_RM;
    
 
      
      
    private Xid lastStartedXid_, lastEndedXid_, lastForgottenXid_,
              lastPreparedXid_, lastCommittedXid_, lastRolledbackXid_;
    //the last xid is buffered and can be retrieved
    
    //below are the exceptions that can be set.
    private XAException startException_;
    private XAException endException_;
    private XAException prepareException_;
    private XAException rollbackException_;
    private XAException commitException_;
    private XAException isSameRMException_;
    
    private boolean readOnly_;
    
    private Xid[] recoverList_;
  
    private boolean recoveryCalled_;
    //true ASA recover was called.
    
    private boolean simulateRecoveryLoop_;
    //if true: oracle 8.1.7 recovery is simulated
    //i.e.: the same xid set is always returned
    //leading to possible infinite recovery loops
    
    private String resourceName_;
    //private, generated in constructor
    //will be same name for cloned instances
    //in order to simulate isSameRM 
    
    /**
     *Create a new instance.
     */
     
    public TestXAResource() 
    {
        reset();
        resourceName_ = this.toString();
    }
    
    
     /**
      *Retrieve that last Xid that was seen
      *by start.
      *@return Xid The last Xid seen by start, or null
      *if start was not yet called on the instance.
      */
      
    public Xid getLastStarted()
    {
        return lastStartedXid_; 
    }
    
     /**
      *Retrieve the last Xid that was seen by commit.
      *
      *@return Xid The last xid that was committed.
      */
      
    public Xid getLastCommitted()
    {
        return lastCommittedXid_; 
    }
    
     /**
      *Get the last xid seen by rollback.
      *@return Xid The last xid that was rolled back.
      */
      
    public Xid getLastRolledback()
    {
        return lastRolledbackXid_; 
    }
    
     /**
      *Get the last xid seen by prepare.
      *@return Xid The last xid seen by prepare.
      */
      
    public Xid getLastPrepared()
    {
        return lastPreparedXid_; 
    }
    
     /**
      *Test if recovery was called since creation or last reset.
      *
      *@return boolean True iff recovery called.
      */
      
    public boolean isRecoveryCalled()
    {
        return recoveryCalled_; 
    }
    
     /**
      *Get the last xid seen by end.
      *@return Xid The last xid used for end.
      */
      
    public Xid getLastEnded()
    {
        return lastEndedXid_; 
    }
    
     /**
      *Get the last xid seen by forget.
      *@return Xid The last xid used for forget.
      */
      
    public Xid getLastForgotten()
    {
      return lastForgottenXid_; 
    }
    
     /**
      *Set the failure mode.
      *@param mode One of the constant failure modes.
      *@param exception The exception to throw on the 
      *occurrence of the simulated failure.
      */
    
    public void setFailureMode ( int mode , XAException exception )
    throws XAException
    {
        switch ( mode ) {
            case  FAIL_COMMIT: commitException_ = exception;
                    //System.err.println ( "Setting FAIL_COMMIT in TestXAResource" );
                    break;
            case FAIL_PREPARE: prepareException_ = exception;
                    break;
            case FAIL_ROLLBACK: rollbackException_ = exception;
                    break;
            case FAIL_START: startException_ = exception;
                    break;
            case FAIL_END: endException_ = exception;
                    break;
            case FAIL_IS_SAME_RM : isSameRMException_ = exception;
            		break;
                    
            default: throw new XAException ( "Unknown mode: " + mode );
            
        } 
    }
    
     /**
      *Set readonly mode.
      *@param readonly If true, then the next prepares will
      *return readonly.
      */
      
    public void setReadOnly ( boolean readOnly )
    {
        readOnly_ = readOnly; 
    }
    
     /**
      *Set the list of xids to be returned on recover.
      *@param list The list of xid instances.
      */
      
    public void setRecoverList ( Xid[] list )
    {
        recoverList_ = list; 
    }
    
     /** 
      *Set the recovery loop mode to the specified value.
      *@param loop If true, then oracle 8.1.7-like recovery 
      *behaviour is done (i.e.: always return the same set
      *of Xids on each call).
      *Default is false.
      */
      
    public void setRecoveryLoop ( boolean loop )
    {
        simulateRecoveryLoop_ = loop; 
    }
     /**
      *Reset the internal state: delete all settings.
      *After calling this method, all exception simulation
      *is cleared and the last xid is null.
      */
      
    public void reset()
    {
    	if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "XAResource.reset called on " + this );
         lastStartedXid_ = null;
         lastEndedXid_ = null;
         lastCommittedXid_ = null;
         lastPreparedXid_ = null;
         lastRolledbackXid_ = null;
         lastForgottenXid_ = null;
         recoveryCalled_ = false;
         simulateRecoveryLoop_ = false;
         
         setReadOnly ( false );
         recoverList_ = null;
         for ( int i = FAIL_MIN ; i < FAIL_MAX ; i++ ) {
          try {
              setFailureMode ( i , null );
          }
          catch ( XAException e ) {
              throw new RuntimeException ( e.getMessage () );
          }   
         }
         
    }
    
     /**
      *@see XAResource
      */
      
    public Xid[] recover ( int flag )
    throws XAException
    {
        Xid[] ret = recoverList_;
        recoveryCalled_ = true;
        //set recoverList_ to null to ensure that xa recovery
        //does not go into infinite loop
        if ( !simulateRecoveryLoop_ )
            recoverList_ = null;
        return ret; 
    }
    
     /** 
      *@see XAResource
      */
      
    public boolean setTransactionTimeout ( int secs ) 
    throws XAException
    {
        return false; 
    }
    
     /**
      *@see XAResource
      */
      
    public int getTransactionTimeout()
    throws XAException
    {
         return 1000;
    }
    
     /**
      *@see XAResource
      */
      
    public boolean isSameRM ( XAResource xares )
    throws XAException
    {
        boolean ret = false;
        
        if ( isSameRMException_ != null )
        	throw isSameRMException_;
        
       	if ( xares instanceof TestXAResource ) {
       		TestXAResource other = ( TestXAResource ) xares;
       		ret = other.resourceName_.equals ( resourceName_ );
       	}
        
        return ret; 
    }
    
     /**
      *@see XAResource
      */
      
    public void start ( Xid xid , int flags )
    throws XAException
    {
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "XAResource.start called on " + this );
        lastStartedXid_ = xid;
        if ( startException_ != null )
            throw startException_;
    }
    
     /**
      *@see XAResource
      */
      
    public void end ( Xid xid , int flags )
    throws XAException
    {
    	    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "XAResource.end called on " + this );
        lastEndedXid_ = xid;
        if ( endException_ != null )
            throw endException_; 
    }
    
     /**
      *@see XAResource
      */
      
    public int prepare ( Xid xid )
    throws XAException
    {
    	    if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "XAResource.prepare called on " + this );
        lastPreparedXid_ = xid;
        if ( prepareException_ != null )
            throw prepareException_;
        
        if ( readOnly_ )
            return XAResource.XA_RDONLY;
        else return XAResource.XA_OK;
    }
    
     /**
      *@see XAResource
      */
      
    public void rollback ( Xid xid )
    throws XAException
    {
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "XAResource.rollback called on " + this );
        lastRolledbackXid_ = xid;
        if ( rollbackException_ != null )
            throw rollbackException_; 
    }
    
     /**
      *@see XAResource
      */
      
    public void commit ( Xid xid , boolean onephase )
    throws XAException
    {
    		if ( LOGGER.isDebugEnabled() ) LOGGER.logDebug ( "XAResource.commit called on " + this );
        lastCommittedXid_ = xid;
        if (  commitException_ != null ) {
            
            throw commitException_;
        }
        
    }
    
     /** 
      *@see XAResource
      */
      
    public void forget ( Xid xid )
    throws XAException
    {
        lastForgottenXid_ = xid;
    }
    
    public Object clone()
    {
    	TestXAResource ret = null;
    	
    	try
        {
            ret = ( TestXAResource ) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            
        }
        ret.reset();
    	
    	return ret;
    }
}
  

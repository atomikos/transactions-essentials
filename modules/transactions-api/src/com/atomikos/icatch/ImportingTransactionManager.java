//$Id: ImportingTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: ImportingTransactionManager.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:40  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:34  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:57  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:22  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.6  2005/08/05 15:03:28  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.5  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.4  2004/10/11 13:39:29  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: ImportingTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Revision 1.3  2003/03/11 06:38:53  guy
//$Id: ImportingTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//Merged in changes from transactionsJTA100 branch.
//$Id: ImportingTransactionManager.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//
//Revision 1.2.4.1  2002/11/17 18:36:00  guy
//Updated ImpTM: terminated does not throw heuristics!
//
//Revision 1.2  2002/01/07 12:25:33  guy
//Updated AbstractUserTransactionService to shutdown resources as well,
//and make it resilient to multiple init/shutdown calls.
//
//Revision 1.1.1.1  2001/10/09 12:37:25  guy
//Core module
//

package com.atomikos.icatch;

/**
 *
 *
 *An interface for the communication layer, for notifying TM of 
 *incoming transactional request.
 *
 *<b>
 *WARNING: this interface and its mechanisms are subject to several patents and
 *pending patents held by Atomikos. Regardless the license
 *under which this interface is distributed, third-party use is 
 *NOT allowed without the prior and explicit 
 *written approval of Atomikos.
 *</b>
 */
 
 public interface ImportingTransactionManager
 {
    /**
     *Notify TM of incoming request with given propagation.
     *Makes the TM start a tx and associate it with calling 
     *thread.
     *
     *@param propagation The ancestor information.
     *@param orphancheck True iff orphans are to be checked.
     *@param heur_commit True iff heuristic means commit.
     *
     *@return CompositeTransaction The local tx instance.
     */
     
    public CompositeTransaction 
      importTransaction ( Propagation propagation , 
                          boolean orphancheck , boolean heur_commit 
                         ) throws SysException;
    
  
                          
    /**
     *Termination callback for current tx. 
     *Called by comm layer right before
     *a remote call returns. 
     *@param commit True iff the invocation had no errors.
     *Implies that the local subtx is committed.
     *
     *@return Extent The extent to return to remote client.
     *@exception SysException Unexpected error.
     *@exception RollbackException If the transaction has timed out.
     */
     
    public Extent terminated( boolean commit ) 
    throws SysException, RollbackException;
   // , HeurRollbackException,
//    HeurMixedException, HeurHazardException;

    
 
    

 }

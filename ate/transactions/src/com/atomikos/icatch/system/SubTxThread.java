package com.atomikos.icatch.system;

import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Propagation;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.SubTxCode;
import com.atomikos.icatch.SysException;

/**
 * 
 * 
 * A thread class for subtransaction threads. A SubTxThread is a thread whose
 * code is automatically executed in a subtransaction of the calling thread's
 * transaction. Creation, start and end of the subtransaction is done by the
 * system so the programmer does not have to worry about that. To create a new
 * instance, you need a <a href="Waiter.html">Waiter</a> and a <a
 * href="SubTxCode.html">SubTxCode</a> instance.
 */

public class SubTxThread extends Thread
{
    private SubTxCode myCode;
    private Waiter myWaiter;
    private Exception myException;
    private CompositeTransaction parent;
    private Propagation propagation;
    private HeuristicMessage msg;

    /**
     * Creates a new instance for a given waiter, with a given subtx code.
     * 
     * @param w
     *            The waiter for synchronization. The creating thread should
     *            wait for all subtx threads to finish.
     * @param code
     *            The SubTxCode to execute.
     * @param tag
     *            A heuristic message for this thread's execution. Useful in
     *            case of heuristic problems.
     * @exception SysException
     *                On failure; for instance, if the calling thread has no
     *                transaction associated to it, or no transaction manager is
     *                running.
     */

    public SubTxThread ( Waiter w , SubTxCode code , HeuristicMessage tag )
            throws SysException
    {
        parent = Configuration.getCompositeTransactionManager ()
                .getCompositeTransaction ();
        // if (ct.getSerial()) throw new SystemException("Tx in serial mode;
        // multiple threads not allowed");

        msg = tag;
        try {
            propagation = Configuration.getExportingTransactionManager ()
                    .getPropagation ();
        } catch ( RollbackException e ) {
            throw new SysException ( "Transaction already rolled back" );
        }
        myWaiter = w;
        myWaiter.incActives ();
        myCode = code;
        myException = null;

    }

    public Exception getException ()
    {
        return myException;
    }

    public void run ()
    {
        CompositeTransaction ct = null;
        try {
            ct = Configuration.getImportingTransactionManager ()
                    .importTransaction ( propagation, true, false );
            ct.getTransactionControl ().setTag ( msg );
            if ( propagation.isSerial () )
                myWaiter.getToken ();
            myCode.exec ();
            // delegates call to implementation

            ct.getTransactionControl ().getTerminator ().commit ();
        } catch ( Exception e ) {
            myWaiter.incAbortCount ();
            myException = e;
            ct.getTransactionControl ().getTerminator ().rollback ();
        } finally {
            if ( propagation.isSerial () )
                myWaiter.giveToken ();
            myWaiter.decActives ();
        }

    }

}

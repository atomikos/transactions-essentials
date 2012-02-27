/**
 *
 *
 *A test class for composite transaction implementations.
 */

package com.atomikos.icatch.imp;
import java.util.Stack;

import com.atomikos.icatch.CompositeTerminator;
import com.atomikos.icatch.CompositeTransaction;
import com.atomikos.icatch.Extent;
import com.atomikos.icatch.SysException;
import com.atomikos.util.UniqueIdMgr;

public class CompTxImpTester
{
	protected static UniqueIdMgr tidmgr_ = new UniqueIdMgr (  "CompTxImpTester");

	/**
	 *Constructs a new tester instance.
	 *
	 */

	public CompTxImpTester ( )
	{
		tidmgr_ = new UniqueIdMgr ("CompTxImpTester");
	}


 	static CompositeTransaction createCompositeTransaction ()
 	{
 		String tid = tidmgr_.get();
 		CoordinatorImp coord = new CoordinatorImp ( tid,
 													true,
 													true);
 		return new CompositeTransactionImp ( null, tid, true, coord  );
 	}


	/**
	 *Perform the testing.
	 *
	 *@exception Exception If test fails.
	 */

	public static void test () throws Exception
	{
		CompositeTransaction ct = createCompositeTransaction();
		CompositeTransaction nested = null;

		if ( !ct.isRoot() )
			throw new Exception("new tx is not root?");
		if ( ct.getTid() == null )
			throw new Exception ("new tx has no tid?");
		if ( ct.getCompositeCoordinator() == null )
			throw new Exception ("new tx has no coordinator?");
		if ( ct.getTransactionControl().getExtent() == null )
			throw new Exception ("new tx has no extent?");
		if ( ct.getTransactionControl().getTerminator() == null )
			throw new Exception ("new tx has no terminator?");


		CompositeTerminator terminator2 =
		  ct.getTransactionControl().getTerminator();
		terminator2.commit ();

		Extent extent = ct.getTransactionControl().getExtent();

	}



	public static void main ( String[] args )
	{
		System.out.println("Starting: CompTxImpTester...");
		try {

			test();
		}
		catch ( SysException se ) {
			System.out.println("ERROR: " + se.getMessage());
			se.printStackTrace();
			if ( se.getErrors() != null ) {
				Stack errors = se.getErrors();
				while ( ! errors.empty() ) {
					Exception err = (Exception) errors.pop();
					System.out.println("Nested exception :"+
										err.getMessage() + " " +
										err.getClass().getName() );
					err.printStackTrace();
				}
			}
		}
		catch ( Exception e ) {
			System.out.println("ERROR: " + e.getMessage());
			e.printStackTrace();
		}
		finally {
			System.out.println("Done:     CompTxImpTester.");
		}
	}

}

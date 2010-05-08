//$Id: CompTxImpTester.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: CompTxImpTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:29  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.2  2006/03/15 10:24:25  guy
//Corrected/improved JUnit tests.
//
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 15:23:38  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.2  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/18 13:32:19  guy
//Added test files to package under CVS.
//

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
 													null,
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

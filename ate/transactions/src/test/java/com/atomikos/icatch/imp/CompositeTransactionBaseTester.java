//$Id: CompositeTransactionBaseTester.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: CompositeTransactionBaseTester.java,v $
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
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.2  2004/10/12 13:03:25  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/18 13:32:19  guy
//Added test files to package under CVS.
//

package com.atomikos.icatch.imp;
import java.util.Stack;

import com.atomikos.util.UniqueIdMgr;

  /**
   *
   *
   *A tester class for CompositeTransactionBase.
   */
   
  public class CompositeTransactionBaseTester
  {
    //*****************************************************************
    //
    //
    //BELOW IS TEST FACILITIES
    //
    //
    //*****************************************************************

    /**
     *Main test method.
     */

    public static void test ( ) throws Exception 
    {
        UniqueIdMgr tidmgr = new UniqueIdMgr ("TestTIDMgr");
        String tid1 = null, tid2 = null, tid3 = null, tid4 = null,
	  tid5 = null, tid6 = null, tid7 = null;
        Stack lineage1 = new Stack();
        Stack lineage2 = new Stack();
        Stack lineage3 = new Stack();
        Stack lineage4 = new Stack();

        tid1 = tidmgr.get();
        
        //make tx1 a root
        TestCompositeTransactionBase tx1 = 
	  new TestCompositeTransactionBase ( tid1 , lineage1 );
        
        tid2 = tidmgr.get();

        //make tx2 a descendant of tx1
        lineage2.push ( tx1 );
        TestCompositeTransactionBase tx2 =
	  new TestCompositeTransactionBase ( tid2 , lineage2 );

        if ( ! tx1.isAncestorOf ( tx2 ) ) 
	  throw new Exception ("ERROR: isAncestor does not work well");
        if ( tx2.isAncestorOf ( tx1 ) )
	  throw new Exception ("ERROR: isAncestor fails if target is " +
			   "a descendant");

        if ( ! tx2.isDescendantOf ( tx1 ) )
	  throw new Exception ("ERROR: isDescendant does not work");
         if ( tx1.isDescendantOf ( tx2 ) )
	  throw new Exception ("ERROR: isDescendant fails if target is " +
			   "a descendant");
         if ( ! tx1.isDescendantOf ( tx1 ) ||
	    ! tx2.isDescendantOf ( tx2 ) )
	   throw new Exception ("ERROR: isDescendant is not reflexive");
         
         if ( ! tx1.isAncestorOf ( tx1 ) ||
	    ! tx2.isAncestorOf ( tx2 ) )
	   throw new Exception ("ERROR: isAncestor is not reflexive");

        if ( ! tx1.isRelatedTransaction ( tx2 ) )
	  throw new Exception ("ERROR: isRelated does not work");

        if ( ! tx1.isRelatedTransaction ( tx1 ) ||
	   ! tx2.isRelatedTransaction ( tx2) )
	  throw new Exception ("ERROR: isRelated is not reflexive");

        if ( ! tx1.isRoot() ) 
	  throw new Exception ("ERROR: isRoot does not work for root");
        if ( tx2.isRoot () )
	  throw new Exception ("ERROR: isRoot does not work for non-root");
        if ( ! tx1.isSameTransaction ( tx1 ) )
	  throw new Exception ("ERROR: isSame does not work on same tx");
        if ( tx2.isSameTransaction ( tx1 ) )
	  throw new Exception ("ERROR: isSame does not work for diff. txs");

        //
        //NEXT, TEST WITH A BRANCH IN TX TREE
        //

        tid3 = tidmgr.get();
        lineage3.push ( tx1 );
        TestCompositeTransactionBase tx3 =
	  new TestCompositeTransactionBase ( tid3 , lineage3 );
        
        if ( ! tx3.isRelatedTransaction ( tx2 ) ||
	   ! tx2.isRelatedTransaction ( tx3 ) )
	  throw new Exception ("ERROR: isRelated fails for siblings");
        if ( tx2.isDescendantOf ( tx3 ) ||
	   tx2.isAncestorOf ( tx3 ) )
	  throw new Exception ("ERROR: ancestor relationship wrong");

        //
        //NEXT, TEST DIFF TX WITH SAME UniqueId AS ANOTHER
        //
        
        lineage4.push ( tx1 );
        TestCompositeTransactionBase tx4 =
	  new TestCompositeTransactionBase ( tid3, lineage4 );
        if ( ! tx4.isSameTransaction ( tx3 ) )
	  throw new Exception ("ERROR: isSameTransaction fails for diff. "+
			   "txs with SAME TID");

    

    }
    
    /**
     *For testing this class separately.
     */

    public static void main ( String[] args )
    {
        try {
	  Test.getOutput().println("Starting: CompositeTransactionBase Test");
	  test();
        }
        catch ( Exception e ) {
	  Test.getOutput().println("ERROR in test: "+e.getMessage());
	  e.printStackTrace();
        }
        finally {
	  Test.getOutput().println("Done:     CompositeTransactionBase Test");
        }
    }

  }

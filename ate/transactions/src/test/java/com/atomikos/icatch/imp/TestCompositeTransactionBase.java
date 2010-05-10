//$Id: TestCompositeTransactionBase.java,v 1.1.1.1 2006/08/29 10:01:07 guy Exp $
//$Log: TestCompositeTransactionBase.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:07  guy
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
//Revision 1.2  2006/03/21 13:23:31  guy
//Introduced active recovery and CompTx properties as meta-tags.
//
//Revision 1.1.1.1  2006/03/09 14:59:18  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2005/08/09 15:23:39  guy
//Updated javadoc, and redesigned CompositeTransaction interface
//(eliminated TransactionControl and CompositeTerminator).
//
//Revision 1.2  2004/10/12 13:03:26  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.1  2002/02/18 13:32:20  guy
//Added test files to package under CVS.
//
//Revision 1.1  2001/03/23 17:00:30  pardon
//Lots of implementations for Terminator and proxies.
//

package com.atomikos.icatch.imp;
import java.util.Stack;

/**
 *
 *
 *A test class for CompositeTransactionBase.
 */

public class TestCompositeTransactionBase extends AbstractCompositeTransaction
{

    public TestCompositeTransactionBase ( String tid , Stack lineage )
    {
        super ( tid , lineage , true);
    }


}

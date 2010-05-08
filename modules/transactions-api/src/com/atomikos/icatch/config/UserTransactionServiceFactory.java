//$Id: UserTransactionServiceFactory.java,v 1.1.1.1 2006/08/29 10:01:08 guy Exp $
//$Log: UserTransactionServiceFactory.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:08  guy
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
//Revision 1.1.1.1  2006/03/22 13:46:58  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:23  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/10/12 13:03:27  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.2  2004/03/22 15:36:54  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.1.2.3  2003/05/30 15:18:43  guy
//Changed UserTransactionFactory.getUserTransaction: added Properties arg.
//
//Revision 1.1.2.2  2003/05/22 15:32:00  guy
//Added comments.
//
//Revision 1.1.2.1  2003/05/22 15:23:15  guy
//Added UserTransactionServiceFactory interface, according to JNDI context pattern.
//

package com.atomikos.icatch.config;

import java.util.Properties;

/**
 *
 *
 *
 *A factory for UserTransactionService instances.
 *Each product will typically have its own implementation.
 *A system property can be used to indicate which one to use.
 *Implementations should have a public no-arg constructor!
 */

public interface UserTransactionServiceFactory
{
    /**
     * Gets the user transaction service instance.
     *
     * @return UserTransactionService A user handle that corresponds
     * to the underlying transaction service implementation.
     * @param properties The properties that can be used to initialize.
     */

    public UserTransactionService getUserTransactionService( Properties properties );
}

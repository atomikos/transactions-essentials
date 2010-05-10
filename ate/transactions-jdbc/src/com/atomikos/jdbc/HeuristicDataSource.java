//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//$Log: HeuristicDataSource.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:12  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:38  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:31  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:54  guy
//Import.
//
//Revision 1.2  2006/03/15 10:32:01  guy
//Formatted code.
//
//Revision 1.1.1.1  2006/03/09 14:59:14  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2004/10/25 08:46:21  guy
//Removed old todos
//
//Revision 1.6  2004/10/12 13:04:27  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2004/10/11 13:39:55  guy
//Fixed javadoc and EOL delimiters.
//
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.4  2004/10/08 07:11:43  guy
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Improved automatic registration for recovery.
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Added methods to HeuristicDataSource.
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Improved user/paswwd handling in XAConnectionFactory.
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.3  2004/03/22 15:39:16  guy
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged-in changes from branch redesign-4-2003.
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2.2.1  2003/06/20 16:31:59  guy
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//*** empty log message ***
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Revision 1.2  2003/03/11 06:42:18  guy
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//Merged in changes from transactionsJTA100 branch.&
//$Id: HeuristicDataSource.java,v 1.1.1.1 2006/08/29 10:01:12 guy Exp $
//
//Revision 1.1.4.1  2002/09/23 08:52:47  guy
//Added getConnection with user and password, for JDBC consistency.
//
//Revision 1.1  2002/03/19 14:08:00  guy
//Conceptual cleanup, and addition of HeuristicDataSource.
//

package com.atomikos.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.atomikos.icatch.HeuristicMessage;

/**
 * 
 * 
 * A data source that supports the addition of heuristic messages to SQL data
 * access.
 */

public interface HeuristicDataSource extends DataSource
{

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */
    public Connection getConnection ( String msg ) throws SQLException;

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param user
     *            The user name to use.
     * @param passwd
     *            The password.
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */

    public Connection getConnection ( String user , String passwd , String msg )
            throws SQLException;

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */

    public Connection getConnection ( HeuristicMessage msg )
            throws SQLException;

    /**
     * Get a connection to the datasource for the given description of the work.
     * 
     * @param user
     *            The user name to use.
     * @param passwd
     *            The password.
     * @param msg
     *            The heuristic message that best describes the work about to be
     *            done.
     * @return Connection The connection.
     * @exception SQLException
     *                On error.
     */

    public Connection getConnection ( String user , String passwd ,
            HeuristicMessage msg ) throws SQLException;
}

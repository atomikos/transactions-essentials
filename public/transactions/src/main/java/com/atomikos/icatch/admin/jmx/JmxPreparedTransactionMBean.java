/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.SysException;

/**
 * An MBean interface with specific methods for prepared transactions.
 */

public interface JmxPreparedTransactionMBean extends JmxTransactionMBean
{

    /**
     * Forces commit of the transaction.
     * 
     */

    public void forceCommit () throws HeurRollbackException,
            HeurHazardException, HeurMixedException, SysException;

    /**
     * Forces rollback of the transaction.
     * 
     */

    public void forceRollback () throws HeurCommitException,
            HeurHazardException, HeurMixedException, SysException;

}

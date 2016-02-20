/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.admin.jmx;

import com.atomikos.icatch.admin.AdminTransaction;

/**
 * A default JMX transaction bean, for transactions whose state does not allow
 * special actions from the user.
 */

public class JmxDefaultTransaction extends JmxTransaction
{

    /**
     * @param adminTransaction
     */

    public JmxDefaultTransaction ( AdminTransaction adminTransaction )
    {
        super ( adminTransaction );

    }

}

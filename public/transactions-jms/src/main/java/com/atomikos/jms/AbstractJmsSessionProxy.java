/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jms;

import com.atomikos.icatch.CompositeTransaction;

abstract class AbstractJmsSessionProxy extends AbstractJmsProxy
{

	protected abstract boolean isAvailable();

	protected abstract boolean isErroneous();

	protected abstract boolean isInTransaction ( CompositeTransaction ct );

	protected boolean isInactiveTransaction ( CompositeTransaction ct )
	{
		//default to false: be pessimistic and disallow reuse if not sure
		return false;
	}
}

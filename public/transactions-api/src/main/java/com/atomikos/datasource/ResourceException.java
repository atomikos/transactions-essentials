/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.datasource;


/**
* Exception on the level of the resource manager.
* Contains more detailed info of actual underlying exception.
*/

public class ResourceException extends com.atomikos.icatch.SysException
{

    public ResourceException(String msg){
        super(msg);

    }

	public ResourceException(String msg, Throwable cause) {
		super(msg, cause);
	}

}


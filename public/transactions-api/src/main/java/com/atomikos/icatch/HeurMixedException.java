/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */


//
//Revision 1.2  2001/03/01 19:26:57  pardon
//Added more.
//
//Revision 1.1  2001/02/21 19:51:23  pardon
//Redesign!
//

package com.atomikos.icatch;



/**
 * An exception signaling that some participants
 * have committed whereas others performed a rollback.
 */

public class HeurMixedException extends HeuristicException
{
	private static final long serialVersionUID = 1L;

	public HeurMixedException ()
    {
        super("Heuristic Mixed Exception");
    }
}

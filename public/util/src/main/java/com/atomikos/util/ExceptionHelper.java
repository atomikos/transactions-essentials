/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.util;

import java.io.PrintWriter;
import java.io.StringWriter;

 /**
  * Helper class to deal with exception details.
  *
  */

public class ExceptionHelper
{

	public static String convertStackTrace ( Throwable e )
	{
		String ret = null;
		if ( e != null ) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
	    	pw.close();
	        String string = sw.toString();
			ret = string.substring(0, string.length()-1); // strip the trailing '\n'
		}
		return ret;
	}

	private ExceptionHelper(){}


}

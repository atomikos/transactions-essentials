//$Id: RotatingFileConsoleTester.java,v 1.1.1.1 2006/08/29 10:01:16 guy Exp $
//$Log: RotatingFileConsoleTester.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:16  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:41  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:46  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.1  2005/04/29 14:30:09  guy
//Added rotating console implementation.
//
package com.atomikos.diagnostics;

/**
 * 
 * 
 * 
 * 
 *
 * 
 */
public class RotatingFileConsoleTester
{
	
	public static void test ( int level , int maxSize , int fileCount )
	throws Exception
	{
		RotatingFileConsole console = new RotatingFileConsole ( 
			"testConsole" , maxSize , fileCount );
		console.setLevel ( level );
		for ( int i = 0 ; i < maxSize + 1 ; i++ ) {
			console.print ( "WARN" , Console.WARN );
			console.println ( "WARN" , Console.WARN );
			console.print ( "INFO" , Console.INFO );
			console.println ( "INFO" , Console.INFO );
			console.print ( "DEBUG" , Console.DEBUG );
			console.println ( "DEBUG" , Console.DEBUG );
		}
		console.close();
	}

    public static void main(String[] args)
    throws Exception
    {
    	int level = Console.INFO;
    	int maxSize = 100;
    	int fileCount = 2;
    	if ( args.length == 3 ) {
    		level = Integer.parseInt ( args[0] );
    		maxSize = Integer.parseInt ( args[1] );
    		fileCount = Integer.parseInt ( args[2] );
    	}
    	else {
    		System.out.println ( "Note: optionally use arguments: <log level> <maxSize in bytes> <fileCount>");
    	}
    	test ( level , maxSize , fileCount );
    	System.out.println ( "Done! Please check the output files to assert no inappropriate levels are present");
    }
}

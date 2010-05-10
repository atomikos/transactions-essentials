//$Id: CascadedConsole.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: CascadedConsole.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:51  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:40  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:37  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:43  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.6  2005/10/10 08:11:53  guy
//Added getLevel method to inspect log level.
//
//Revision 1.5  2005/08/09 15:25:45  guy
//Updated javadoc.
//
//Revision 1.4  2004/10/12 13:05:04  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.3  2004/09/01 13:41:37  guy
//Merged in changes of TransactionsRMI 1.22
//
//Revision 1.2.12.1  2004/04/30 14:34:01  guy
//Added the concept of different levels.
//
//Revision 1.2  2002/01/29 11:26:28  guy
//Updated to latest state: repository seemed to be outdated?
//

package com.atomikos.diagnostics;

import java.io.IOException;

/**
 *
 *
 *A cascaded message console for system output warnings.
 *
 */

public class  CascadedConsole implements Console
{
    private Console first_ , last_ ;
    
    /**
    *Construct a new instance.
    *
    *@param first The console where messages are displayed first.
    *@param last The console where messages are cascaded 
    *after display on first console.
    */
    
    public CascadedConsole ( Console first , Console last )
    {
        first_ = first;
        last_ = last;
    }
    
    /**
     *Print a message to the output of the console(s).
     *This message is cascaded internally.
     *
     *@param string The message to output.
     *@param java.io.IOException On failure.
     */

    public void println(String string) throws java.io.IOException
    {
        first_.println ( string );
        last_.println ( string );	
    }
    
     /**
     *Print a message to the output of the console(s).
     *This message is cascaded internally.
     *
     *@param string The message to output.
     *@param java.io.IOException On failure.
     */

    public void print(String string) throws java.io.IOException
    {
        first_.print ( string );
        last_.print ( string );	
    }
    
    /**
     *Closes the underlying consoles.
     */
     
    public void close() throws java.io.IOException
    {
        first_.close();
        last_.close();	
    }

  
    public void println(String string, int level) throws IOException
    {
		first_.println ( string , level );
		last_.println ( string , level );
        
    }

 
    public void print(String string, int level) throws IOException
    {
        first_.print( string , level );
        last_.print ( string , level );
        
    }

    
    public void setLevel(int level)
    {
        first_.setLevel ( level );
        last_.setLevel ( level );
        
    }

    /**
     * @see com.atomikos.diagnostics.Console#getLevel()
     */
    public int getLevel()
    {
        return first_.getLevel();
    }

    
}

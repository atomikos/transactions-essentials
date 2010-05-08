//$Log: ExtensionsFileFilter.java,v $
//Revision 1.2  2006/09/19 08:03:57  guy
//FIXED 10050
//
//Revision 1.1.1.1  2006/08/29 10:01:15  guy
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
//Revision 1.1.1.1  2006/03/22 13:47:03  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:44  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2004/03/22 15:39:53  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.2.2.1  2003/08/21 20:32:09  guy
//*** empty log message ***
//
//Revision 1.2  2003/03/11 06:43:40  guy
//Merged in changes from transactionsJTA100 branch.
//
//Revision 1.1.2.1  2002/10/09 17:14:55  guy
//Added filter for arbitrary file extensions (useful in dialogs).
//
//$Id: ExtensionsFileFilter.java,v 1.2 2006/09/19 08:03:57 guy Exp $

package com.atomikos.swing;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

 /**
  *
  *
  *A file filter for displaying only directories and a files
  *with an extension that corresponds to one of a given set.
  */

public class ExtensionsFileFilter
extends javax.swing.filechooser.FileFilter
{
      public static String getExtension ( File f )
      {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if ( i > 0 &&  i < s.length() - 1 ) {
            ext = s.substring ( i+1 ).toLowerCase();
        }
        return ext; 
      } 
        
      private Hashtable extensions_;
      
      public ExtensionsFileFilter ( String[] extensions )
      {
          super();
          extensions_ = new Hashtable();
          for ( int i = 0 ; i < extensions.length ; i++ ) {
              extensions_.put ( extensions[i].toLowerCase() , extensions[i].toLowerCase() ); 
          }
      }
      
      public String getDescription()
      {
          StringBuffer ret = new StringBuffer();
          Enumeration enumm = extensions_.keys();
          while ( enumm.hasMoreElements() ) {
              String nxt = ( String ) enumm.nextElement();
              ret.append ( nxt ); ret.append ( " " );
          } 
          return ret.toString();
      }
      
      public boolean accept ( File f )
      {
        boolean ret = false;
        if ( f.isDirectory() ) {
            ret = true;
        }
        else {
            String extension = getExtension ( f );
            if ( extension != null && extensions_.containsKey ( extension ) )
                ret = true;
        }

      return ret;
      }
}

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

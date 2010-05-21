/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

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

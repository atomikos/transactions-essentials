//$Id: PropertiesTableModel.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: PropertiesTableModel.java,v $
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
//Revision 1.1.1.1  2006/03/22 13:47:04  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:45  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.3  2002/01/29 12:55:40  guy
//Added files again; deleted by mistake.
//
//Revision 1.1.1.1  2001/10/05 13:22:18  guy
//GUI module
//

package com.atomikos.swing;
import javax.swing.table.TableModel;

 /**
  *
  *
  *A TableModel for a PropertiesPanel.
  *Provides functions in case of insert, edit or delete of a table's rows.
  */
  
  public interface PropertiesTableModel
  {
    
        /**
         *Gets the Swing table model for this one.
         *@return TableModel The swing table model.
         */
         
        public TableModel getTableModel();
        
        /**
         *Indicates that one or more rows have changed, and
         *the table view needs an update.
         */
         
        public void refresh();
        
        /** 
         *Indicates that a row was deleted.
         *
         *@param row The row's index.
         */
         
        public void rowDeleted ( int row );
        
        /**
         *Indicates that a new row has been inserted.
         */
         
        public void rowInserted ();
  }

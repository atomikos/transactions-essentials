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

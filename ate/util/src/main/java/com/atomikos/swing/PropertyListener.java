package com.atomikos.swing;

 /**
  *
  *
  *A listener for a new property event.
  */
  
  public interface PropertyListener
  {
      /**
       *Notification that the new button was pressed, and the user
       *thus wishes to insert a new property in the given table.
       *
       *@param table The table model.
       */
       
      public void newProperty ( PropertiesTableModel table ); 
      
      /**
       *Notification that the edit button was pressed, and the user
       *thus wishes to edit the currently selected property in the
       *given table.
       *
       *@param table The table model.
       *@param index Indicates which row to edit.
       */
       
      public void editProperty ( PropertiesTableModel table , int index );
      
      /**
       *Notification that the delete button was pressed, and thus 
       *the indicated property should be deleted from the table.
       *
       *@param table The table model.
       *@param index The index of the row to delete.
       */
       
      public void deleteProperty ( PropertiesTableModel table , int index );
  }

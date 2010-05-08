//$Id: PropertyListener.java,v 1.1.1.1 2006/08/29 10:01:15 guy Exp $
//$Log: PropertyListener.java,v $
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

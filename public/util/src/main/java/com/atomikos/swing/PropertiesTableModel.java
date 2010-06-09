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
